import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { constant, isEqual, shuffle, sortBy, uniqBy } from 'lodash-es';
import { colors } from 'src/app/games/game.constants';

const blackFunc = constant('#000000');

@Component({
  selector: 'app-game-board',
  templateUrl: './game-board.component.html',
  styleUrls: ['./game-board.component.css']
})
export class GameBoardComponent implements OnInit, OnChanges {
  @Input()
  board!: number[][];

  @Input()
  palette: string[] = colors;

  boardColors!: string[][];

  readonly #revealedPositions: Array<[number, number]>;

  constructor() {
    this.#revealedPositions = [];
  }

  get width() {
    return this.board[0].length;
  }

  get height() {
    return this.board.length;
  }

  ngOnInit(): void {
    this.#fillBoardWithBlack();
    this.#progressivelyRevealBoard();
  }

  ngOnChanges(): void {
    for (const position of this.#revealedPositions) {
      this.#fillPositionWithActualColor(position);
    }
  }

  #fillBoardWithBlack() {
    this.boardColors = this.board.map(row => row.map(blackFunc));
  }

  #fillPositionWithActualColor(position: [number, number]) {
    const [row, col] = position;
    this.boardColors[row][col] = this.#getBoardColor(position);
  }

  #getBoardColor([row, col]: [number, number]): string {
    return this.palette[this.board[row][col]];
  }

  #progressivelyRevealBoard() {
    const width = this.width;
    const height = this.height;

    const initialPosition: [number, number] = [0, 0];
    const initialColor = this.#getBoardColor(initialPosition);
    const positionsWithInitialColor = getAllContiguousPositions(
      [initialPosition],
      width,
      height,
      pos => this.#getBoardColor(pos) === initialColor
    );

    const remainingPositions: Array<[number, number]> = new Array(height)
      .fill(0)
      .flatMap((_rowValue, row) =>
        new Array(width)
          .fill(0)
          .map((_colValue, col) => [row, col] as [number, number])
      )
      .filter(
        pos => !positionsWithInitialColor.some(init => isEqual(init, pos))
      );

    const positionsToReveal = [
      ...sortBy(shuffle(positionsWithInitialColor), ([row, col]) => row + col),
      ...sortBy(shuffle(remainingPositions), ([row, col]) => row + col)
    ];

    const lastPositionIndex = width * height - 1;
    const when = positionsToReveal
      .map(
        (position, i) =>
          [position, logslider(i, lastPositionIndex)] as [
            [number, number],
            number
          ]
      )
      .reduce(
        (memo, [pos, ms]) => [
          ...memo,
          [pos, ms - memo.reduce((mm, [_p, m]) => mm + m, 0)] as [
            [number, number],
            number
          ]
        ],
        [] as Array<[[number, number], number]>
      );

    this.#progressivelyRevealPositions(when);
  }

  #progressivelyRevealPositions(
    [[position, delay], ...remainingPositions]: Array<
      [[number, number], number]
    >,
    i = 0
  ) {
    setTimeout(() => {
      this.#fillPositionWithActualColor(position);
      this.#revealedPositions.push(position);

      if (remainingPositions.length !== 0) {
        this.#progressivelyRevealPositions(remainingPositions, i + 1);
      }
    }, delay);
  }
}

function logslider(i: number, maxp: number) {
  // positions will be between 0 and maxp
  const minp = 0;

  // The result should be between 100 an 10000000
  const minv = Math.log(250);
  const maxv = Math.log(1250);

  // calculate adjustment factor
  const scale = (maxv - minv) / (maxp - minp);

  return Math.exp(minv + scale * (i - minp));
}

function getAllContiguousPositions(
  positions: Array<[number, number]>,
  width: number,
  height: number,
  predicate: (position: [number, number]) => boolean
): Array<[number, number]> {
  const newPositions = positions
    .flatMap(position =>
      getPositionsAdjacentTo(position, width, height, predicate)
    )
    .filter(pos => !positions.some(known => isEqual(known, pos)));
  if (newPositions.length === 0) {
    return positions;
  }

  return uniqBy(
    getAllContiguousPositions(
      [...positions, ...newPositions],
      width,
      height,
      predicate
    ),
    ([row, col]) => row * height + col
  );
}

function getPositionsAdjacentTo(
  [row, col]: [number, number],
  width: number,
  height: number,
  predicate: (position: [number, number]) => boolean = constant(true)
): Array<[number, number]> {
  const candidates: Array<[number, number]> = [
    [row - 1, col], // top
    [row, col + 1], // right
    [row + 1, col], // bottom
    [row, col - 1] // left
  ];

  return candidates.filter(pos => {
    const [row, col] = pos;
    return (
      row >= 0 && row < height && col >= 0 && col < width && predicate(pos)
    );
  });
}

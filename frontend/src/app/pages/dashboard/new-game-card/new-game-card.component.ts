import { Component, Input, OnChanges } from '@angular/core';
import { random } from 'lodash-es';
import { ToastrService } from 'ngx-toastr';
import { colors } from 'src/app/games/game.constants';
import { GameService } from 'src/app/games/game.service';
import { NavigationService } from 'src/app/navigation.service';

@Component({
  selector: 'app-new-game-card',
  templateUrl: './new-game-card.component.html',
  styleUrls: ['./new-game-card.component.css']
})
export class NewGameCardComponent implements OnChanges {
  @Input()
  title!: string;

  @Input()
  description!: string;

  @Input()
  cardClass: string | string[] = [];

  @Input()
  boardWidth!: number;

  @Input()
  boardHeight!: number;

  @Input()
  numberOfColors!: number;

  @Input()
  maxMoves!: number;

  @Input()
  playerName!: string;

  @Input()
  palette: string[] = colors;

  board?: number[][];

  constructor(
    private readonly gameService: GameService,
    private readonly navigationService: NavigationService,
    private readonly toastrService: ToastrService
  ) {}

  ngOnChanges(): void {
    this.board = new Array(this.boardHeight)
      .fill(0)
      .map(row =>
        new Array(this.boardWidth)
          .fill(0)
          .map(() => random(0, this.numberOfColors - 1))
      );
  }

  startGame(): void {
    this.gameService
      .startGame$({
        playerName: this.playerName,
        boardWidth: this.boardWidth,
        boardHeight: this.boardHeight,
        numberOfColors: this.numberOfColors,
        maxMoves: this.maxMoves
      })
      .subscribe({
        next: () => this.navigationService.goToCurrentGame(),
        error: err => {
          console.warn(err);
          this.toastrService.error(
            'An error occurred while creating the game.',
            'Oops?'
          );
        }
      });
  }
}

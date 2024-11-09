import { Injectable } from '@angular/core';

import { GameService } from './game.service';

@Injectable({
  providedIn: 'root'
})
export class ContinueCurrentGameGuard  {
  constructor(private readonly gameService: GameService) {}

  canActivate(): boolean {
    return !this.gameService.continueCurrentGame();
  }
}

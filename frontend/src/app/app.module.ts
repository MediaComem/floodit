import { CommonModule } from '@angular/common';
import {
  provideHttpClient,
  withInterceptorsFromDi
} from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutComponent } from './layout/layout.component';
import { NavbarComponent } from './layout/navbar/navbar.component';
import { CurrentGameComponent } from './pages/current-game/current-game.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { NewGameCardComponent } from './pages/dashboard/new-game-card/new-game-card.component';
import { PreviousGamesComponent } from './pages/dashboard/previous-games/previous-games.component';
import { GameBoardComponent } from './components/game-board/game-board.component';
import { RegistrationComponent } from './pages/dashboard/registration/registration.component';
import { FormsModule } from '@angular/forms';
import { ToastrModule } from 'ngx-toastr';

@NgModule({
  bootstrap: [AppComponent],
  declarations: [
    AppComponent,
    DashboardComponent,
    CurrentGameComponent,
    LayoutComponent,
    NavbarComponent,
    NewGameCardComponent,
    PreviousGamesComponent,
    GameBoardComponent,
    RegistrationComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    CommonModule,
    FormsModule,
    ToastrModule.forRoot()
  ],
  providers: [provideHttpClient(withInterceptorsFromDi())]
})
export class AppModule {}

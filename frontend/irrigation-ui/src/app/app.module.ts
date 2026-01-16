import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { StationsComponent } from './components/stations/stations.component';
import { PrevisionsComponent } from './components/previsions/previsions.component';
import { ProgrammesComponent } from './components/programmes/programmes.component';
import { JournauxComponent } from './components/journaux/journaux.component';

@NgModule({
  declarations: [
    AppComponent,
    StationsComponent,
    PrevisionsComponent,
    ProgrammesComponent,
    JournauxComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

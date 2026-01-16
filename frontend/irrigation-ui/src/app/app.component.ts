import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Système d\'Irrigation Intelligent';
  activeTab = 'stations';

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }
}

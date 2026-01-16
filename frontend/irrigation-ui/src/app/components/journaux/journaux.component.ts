import { Component, OnInit } from '@angular/core';
import { ArrosageService, JournalArrosage } from '../../services/arrosage.service';

@Component({
  selector: 'app-journaux',
  templateUrl: './journaux.component.html',
  styleUrls: ['./journaux.component.css']
})
export class JournauxComponent implements OnInit {
  journaux: JournalArrosage[] = [];
  loading = false;
  error: string | null = null;

  constructor(private arrosageService: ArrosageService) {}

  ngOnInit(): void {
    this.loadJournaux();
  }

  loadJournaux(): void {
    this.loading = true;
    this.error = null;
    
    this.arrosageService.getAllJournaux().subscribe({
      next: (data) => {
        this.journaux = data.sort((a, b) => {
          return new Date(b.dateExecution).getTime() - new Date(a.dateExecution).getTime();
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des journaux: ' + err.message;
        this.loading = false;
      }
    });
  }

  getTypeExecutionBadgeClass(type: string): string {
    switch (type) {
      case 'MANUEL': return 'badge-info';
      case 'AUTOMATIQUE': return 'badge-success';
      case 'AJUSTE_METEO': return 'badge-warning';
      default: return 'badge-info';
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('fr-FR');
  }
}

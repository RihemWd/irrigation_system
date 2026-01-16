import { Component, OnInit } from '@angular/core';
import { MeteoService, Prevision, Station } from '../../services/meteo.service';

@Component({
  selector: 'app-previsions',
  templateUrl: './previsions.component.html',
  styleUrls: ['./previsions.component.css']
})
export class PrevisionsComponent implements OnInit {
  previsions: Prevision[] = [];
  stations: Station[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
  showForm = false;

  newPrevision: any = {
    stationId: 0,
    dateDebut: '',
    dateFin: '',
    temperatureMax: 20,
    temperatureMin: 10,
    pluiePrevue: 0,
    vent: 10
  };

  constructor(private meteoService: MeteoService) {}

  ngOnInit(): void {
    this.loadPrevisions();
    this.loadStations();
  }

  loadPrevisions(): void {
    this.loading = true;
    this.error = null;
    
    this.meteoService.getAllPrevisions().subscribe({
      next: (data) => {
        this.previsions = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des prévisions: ' + err.message;
        this.loading = false;
      }
    });
  }

  loadStations(): void {
    this.meteoService.getAllStations().subscribe({
      next: (data) => {
        this.stations = data;
        if (data.length > 0) {
          this.newPrevision.stationId = data[0].id;
        }
      },
      error: (err) => {
        console.error('Erreur lors du chargement des stations:', err);
      }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  createPrevision(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    this.meteoService.createPrevision(this.newPrevision).subscribe({
      next: (prevision) => {
        this.success = 'Prévision créée avec succès! (Event RabbitMQ publié)';
        this.loadPrevisions();
        this.resetForm();
        this.showForm = false;
        setTimeout(() => this.success = null, 3000);
      },
      error: (err) => {
        this.error = 'Erreur lors de la création: ' + err.message;
        this.loading = false;
      }
    });
  }

  deletePrevision(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette prévision ?')) {
      this.meteoService.deletePrevision(id).subscribe({
        next: () => {
          this.success = 'Prévision supprimée avec succès!';
          this.loadPrevisions();
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression: ' + err.message;
        }
      });
    }
  }

  resetForm(): void {
    const today = new Date().toISOString().split('T')[0];
    this.newPrevision = {
      stationId: this.stations.length > 0 ? this.stations[0].id : 0,
      dateDebut: today,
      dateFin: today,
      temperatureMax: 20,
      temperatureMin: 10,
      pluiePrevue: 0,
      vent: 10
    };
  }

  getStationName(stationId: number): string {
    const station = this.stations.find(s => s.id === stationId);
    return station ? station.nom : `Station #${stationId}`;
  }
}

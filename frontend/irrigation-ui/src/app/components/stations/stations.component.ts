import { Component, OnInit } from '@angular/core';
import { MeteoService, Station } from '../../services/meteo.service';

@Component({
  selector: 'app-stations',
  templateUrl: './stations.component.html',
  styleUrls: ['./stations.component.css']
})
export class StationsComponent implements OnInit {
  stations: Station[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
  showForm = false;

  newStation: Station = {
    nom: '',
    latitude: 0,
    longitude: 0,
    fournisseur: ''
  };

  constructor(private meteoService: MeteoService) {}

  ngOnInit(): void {
    this.loadStations();
  }

  loadStations(): void {
    this.loading = true;
    this.error = null;
    
    this.meteoService.getAllStations().subscribe({
      next: (data) => {
        this.stations = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des stations: ' + err.message;
        this.loading = false;
      }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  createStation(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    this.meteoService.createStation(this.newStation).subscribe({
      next: (station) => {
        this.success = `Station "${station.nom}" créée avec succès!`;
        this.loadStations();
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

  deleteStation(id: number, nom: string): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer la station "${nom}" ?`)) {
      this.meteoService.deleteStation(id).subscribe({
        next: () => {
          this.success = `Station "${nom}" supprimée avec succès!`;
          this.loadStations();
          setTimeout(() => this.success = null, 3000);
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression: ' + err.message;
        }
      });
    }
  }

  resetForm(): void {
    this.newStation = {
      nom: '',
      latitude: 0,
      longitude: 0,
      fournisseur: ''
    };
  }
}

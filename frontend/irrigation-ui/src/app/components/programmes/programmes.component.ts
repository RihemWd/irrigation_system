import { Component, OnInit } from '@angular/core';
import { ArrosageService, ProgrammeArrosage } from '../../services/arrosage.service';
import { MeteoService, Station } from '../../services/meteo.service';

@Component({
  selector: 'app-programmes',
  templateUrl: './programmes.component.html',
  styleUrls: ['./programmes.component.css']
})
export class ProgrammesComponent implements OnInit {
  programmes: ProgrammeArrosage[] = [];
  stations: Station[] = [];
  loading = false;
  error: string | null = null;
  success: string | null = null;
  showForm = false;
  useMeteo = false;

  newProgramme: any = {
    dateDebut: '',
    heure: 8,
    duree: 30,
    volumeEau: 100,
    etatPrevuInitial: 'PLANIFIE',
    stationMeteoId: 0
  };

  constructor(
    private arrosageService: ArrosageService,
    private meteoService: MeteoService
  ) {}

  ngOnInit(): void {
    this.loadProgrammes();
    this.loadStations();
  }

  loadProgrammes(): void {
    this.loading = true;
    this.error = null;
    
    this.arrosageService.getAllProgrammes().subscribe({
      next: (data) => {
        this.programmes = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des programmes: ' + err.message;
        this.loading = false;
      }
    });
  }

  loadStations(): void {
    this.meteoService.getAllStations().subscribe({
      next: (data) => {
        this.stations = data;
        if (data.length > 0) {
          this.newProgramme.stationMeteoId = data[0].id;
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

  createProgramme(): void {
    this.loading = true;
    this.error = null;
    this.success = null;

    const programmeData: any = {
      dateDebut: this.newProgramme.dateDebut,
      heure: this.newProgramme.heure,
      duree: this.newProgramme.duree,
      volumeEau: this.newProgramme.volumeEau,
      etatPrevuInitial: this.newProgramme.etatPrevuInitial
    };

    if (this.useMeteo && this.newProgramme.stationMeteoId) {
      programmeData.stationMeteoId = this.newProgramme.stationMeteoId;
    }

    this.arrosageService.createProgramme(programmeData).subscribe({
      next: (programme) => {
        this.success = 'Programme d\'arrosage créé avec succès!';
        this.loadProgrammes();
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

  executerProgramme(id: number): void {
    if (confirm('Voulez-vous exécuter ce programme maintenant ?')) {
      this.arrosageService.executerProgramme(id).subscribe({
        next: (journal) => {
          this.success = `Programme exécuté! Journal #${journal.id} créé avec ${journal.volumeReel}L`;
          this.loadProgrammes();
          setTimeout(() => this.success = null, 5000);
        },
        error: (err) => {
          this.error = 'Erreur lors de l\'exécution: ' + err.message;
        }
      });
    }
  }

  deleteProgramme(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce programme ?')) {
      this.arrosageService.deleteProgramme(id).subscribe({
        next: () => {
          this.success = 'Programme supprimé avec succès!';
          this.loadProgrammes();
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
    this.newProgramme = {
      dateDebut: today,
      heure: 8,
      duree: 30,
      volumeEau: 100,
      etatPrevuInitial: 'PLANIFIE',
      stationMeteoId: this.stations.length > 0 ? this.stations[0].id : 0
    };
    this.useMeteo = false;
  }

  getStatutBadgeClass(statut: string): string {
    switch (statut) {
      case 'PLANIFIE': return 'badge-info';
      case 'AJUSTE': return 'badge-warning';
      case 'TERMINE': return 'badge-success';
      case 'IGNORE': return 'badge-danger';
      default: return 'badge-info';
    }
  }

  getStationName(stationId: number): string {
    const station = this.stations.find(s => s.id === stationId);
    return station ? station.nom : `Station #${stationId}`;
  }
}

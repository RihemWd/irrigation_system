import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProgrammeArrosage {
  id?: number;
  parcelleId: number;
  datePlanifiee: string;
  duree: number;
  volumePrevu: number;
  statut: string;
  temperatureMax?: number;
  temperatureMin?: number;
  pluiePrevue?: boolean;
  vent?: number;
  stationMeteoId?: number;
}

export interface JournalArrosage {
  id?: number;
  programmeId: number;
  dateExecution: string;
  volumeReel: number;
  remarque: string;
  typeExecution: string;
}

@Injectable({
  providedIn: 'root'
})
export class ArrosageService {
  private apiUrl = 'http://localhost:8080/api/arrosage';

  constructor(private http: HttpClient) {}

  // Programmes
  getAllProgrammes(): Observable<ProgrammeArrosage[]> {
    return this.http.get<ProgrammeArrosage[]>(`${this.apiUrl}/programmes`);
  }

  getProgrammeById(id: number): Observable<ProgrammeArrosage> {
    return this.http.get<ProgrammeArrosage>(`${this.apiUrl}/programmes/${id}`);
  }

  getProgrammesByParcelle(parcelleId: number): Observable<ProgrammeArrosage[]> {
    return this.http.get<ProgrammeArrosage[]>(`${this.apiUrl}/programmes/parcelle/${parcelleId}`);
  }

  createProgramme(programme: ProgrammeArrosage): Observable<ProgrammeArrosage> {
    return this.http.post<ProgrammeArrosage>(`${this.apiUrl}/programmes`, programme);
  }

  createProgrammeAvecMeteo(parcelleId: number, stationMeteoId: number, date: string): Observable<ProgrammeArrosage> {
    const params = new HttpParams()
      .set('parcelleId', parcelleId.toString())
      .set('stationMeteoId', stationMeteoId.toString())
      .set('date', date);
    return this.http.post<ProgrammeArrosage>(`${this.apiUrl}/programmes/avec-meteo`, null, { params });
  }

  updateProgramme(id: number, programme: ProgrammeArrosage): Observable<ProgrammeArrosage> {
    return this.http.put<ProgrammeArrosage>(`${this.apiUrl}/programmes/${id}`, programme);
  }

  deleteProgramme(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/programmes/${id}`);
  }

  executerProgramme(id: number): Observable<JournalArrosage> {
    return this.http.post<JournalArrosage>(`${this.apiUrl}/programmes/${id}/executer`, null);
  }

  // Journaux
  getAllJournaux(): Observable<JournalArrosage[]> {
    return this.http.get<JournalArrosage[]>(`${this.apiUrl}/journaux`);
  }

  getJournalById(id: number): Observable<JournalArrosage> {
    return this.http.get<JournalArrosage>(`${this.apiUrl}/journaux/${id}`);
  }

  getJournauxByProgramme(programmeId: number): Observable<JournalArrosage[]> {
    return this.http.get<JournalArrosage[]>(`${this.apiUrl}/journaux/programme/${programmeId}`);
  }

  createJournal(journal: JournalArrosage): Observable<JournalArrosage> {
    return this.http.post<JournalArrosage>(`${this.apiUrl}/journaux`, journal);
  }
}

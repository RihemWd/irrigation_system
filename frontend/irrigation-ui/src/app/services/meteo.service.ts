import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Station {
  id?: number;
  nom: string;
  latitude: number;
  longitude: number;
  fournisseur: string;
}

export interface Prevision {
  id?: number;
  date: string;
  temperatureMax: number;
  temperatureMin: number;
  pluiePrevue: boolean;
  vent: number;
  station?: Station;
}

@Injectable({
  providedIn: 'root'
})
export class MeteoService {
  private apiUrl = 'http://localhost:8080/api/meteo';

  constructor(private http: HttpClient) {}

  // Stations
  getAllStations(): Observable<Station[]> {
    return this.http.get<Station[]>(`${this.apiUrl}/stations`);
  }

  getStationById(id: number): Observable<Station> {
    return this.http.get<Station>(`${this.apiUrl}/stations/${id}`);
  }

  createStation(station: Station): Observable<Station> {
    return this.http.post<Station>(`${this.apiUrl}/stations`, station);
  }

  updateStation(id: number, station: Station): Observable<Station> {
    return this.http.put<Station>(`${this.apiUrl}/stations/${id}`, station);
  }

  deleteStation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/stations/${id}`);
  }

  // Prévisions
  getAllPrevisions(): Observable<Prevision[]> {
    return this.http.get<Prevision[]>(`${this.apiUrl}/previsions/all`);
  }

  getPrevisions(stationId: number, date: string): Observable<Prevision[]> {
    const params = new HttpParams()
      .set('stationId', stationId.toString())
      .set('date', date);
    return this.http.get<Prevision[]>(`${this.apiUrl}/previsions`, { params });
  }

  createPrevision(prevision: Prevision): Observable<Prevision> {
    return this.http.post<Prevision>(`${this.apiUrl}/previsions`, prevision);
  }

  updatePrevision(id: number, prevision: Prevision): Observable<Prevision> {
    return this.http.put<Prevision>(`${this.apiUrl}/previsions/${id}`, prevision);
  }

  deletePrevision(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/previsions/${id}`);
  }
}

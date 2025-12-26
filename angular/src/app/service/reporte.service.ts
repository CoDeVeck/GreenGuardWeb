import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../enviroments/environment';
import { Reporte } from '../shared/dto/reporteDTO';
import { EstadisticasGenerales} from '../shared/dto/estadisticasGeneralesDTO';
import { ReporteStats} from '../shared/dto/reporteStatsDTO';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${environment.api_URL}/reportes`;

  constructor(private http: HttpClient) {}

  obtenerEstadisticasGenerales(): Observable<EstadisticasGenerales> {
    return this.http.get<EstadisticasGenerales>(`${this.apiUrl}/estadisticas-generales`);
  }

  reportesPorClasificacion(): Observable<ReporteStats[]> {
    return this.http.get<ReporteStats[]>(`${this.apiUrl}/por-clasificacion`);
  }

  reportesPorTipoIncidente(): Observable<ReporteStats[]> {
    return this.http.get<ReporteStats[]>(`${this.apiUrl}/por-tipo-incidente`);
  }

  reportesPorDistrito(): Observable<ReporteStats[]> {
    return this.http.get<ReporteStats[]>(`${this.apiUrl}/por-distrito`);
  }

  obtenerUltimosReportes(): Observable<Reporte[]> {
    return this.http.get<Reporte[]>(`${this.apiUrl}/ultimos`);
  }
}
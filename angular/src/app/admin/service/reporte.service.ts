import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../enviroments/environment';
import { 
  Reporte, 
  ReporteFiltro, 
  TipoIncidente, 
  TipoClasificacion, 
  Distrito, 
  EstadoCatalogo,
  FiltrosReporte 
} from '../../shared/model/reporte.model';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = `${environment.api_URL}/reportes`;
  private catalogosUrl = `${environment.api_URL}/catalogos`;

  constructor(private http: HttpClient) {}

  // Listar todos los reportes
  listarTodos(): Observable<Reporte[]> {
    return this.http.get<Reporte[]>(this.apiUrl);
  }

  // Filtrar reportes
  filtrarReportes(filtros: FiltrosReporte): Observable<ReporteFiltro[]> {
    let params = new HttpParams();
    
    if (filtros.estado) {
      params = params.set('estado', filtros.estado);
    }
    if (filtros.incidente) {
      params = params.set('incidente', filtros.incidente.toString());
    }
    if (filtros.clasificacion) {
      params = params.set('clasificacion', filtros.clasificacion.toString());
    }

    return this.http.get<ReporteFiltro[]>(`${this.apiUrl}/filtrar`, { params });
  }

  // Obtener reporte por ID
  obtenerPorId(id: number): Observable<Reporte> {
    return this.http.get<Reporte>(`${this.apiUrl}/${id}`);
  }

  // Cambiar estados
  cambiarAEnProceso(id: number): Observable<Reporte> {
    return this.http.put<Reporte>(`${this.apiUrl}/${id}/estado/en-proceso`, {});
  }

  cambiarAResuelto(id: number): Observable<Reporte> {
    return this.http.put<Reporte>(`${this.apiUrl}/${id}/estado/resuelto`, {});
  }

  cancelarReporte(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/cancelar`, {});
  }

  // Catálogos
  obtenerEstados(): Observable<EstadoCatalogo[]> {
    return this.http.get<EstadoCatalogo[]>(`${this.catalogosUrl}/estados`);
  }

  obtenerTiposIncidentes(): Observable<TipoIncidente[]> {
    return this.http.get<TipoIncidente[]>(`${this.catalogosUrl}/tipos-incidentes`);
  }

  obtenerTiposClasificacion(): Observable<TipoClasificacion[]> {
    return this.http.get<TipoClasificacion[]>(`${this.catalogosUrl}/tipos-clasificacion`);
  }

  obtenerDistritos(): Observable<Distrito[]> {
    return this.http.get<Distrito[]>(`${this.catalogosUrl}/distritos`);
  }


  // Exportar CSV
exportarCSV(filtros: FiltrosReporte): Observable<Blob> {
  let params = new HttpParams();
  
  if (filtros.estado) {
    params = params.set('estado', filtros.estado);
  }
  if (filtros.incidente) {
    params = params.set('incidente', filtros.incidente.toString());
  }
  if (filtros.clasificacion) {
    params = params.set('clasificacion', filtros.clasificacion.toString());
  }

  return this.http.get(`${this.apiUrl}/exportar/csv`, { 
    params, 
    responseType: 'blob' 
  });
}

// Exportar PDF
exportarPDF(filtros: FiltrosReporte): Observable<Blob> {
  let params = new HttpParams();
  
  if (filtros.estado) {
    params = params.set('estado', filtros.estado);
  }
  if (filtros.incidente) {
    params = params.set('incidente', filtros.incidente.toString());
  }
  if (filtros.clasificacion) {
    params = params.set('clasificacion', filtros.clasificacion.toString());
  }

  return this.http.get(`${this.apiUrl}/exportar/pdf`, { 
    params, 
    responseType: 'blob' 
  });
}

}
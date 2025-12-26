export interface Reporte {
  idReporte: number;
  numReport: string;
  detalleRepo: string;
  imagenRepo: string;
  latitud: number;
  longitud: number;
  repoRegistado: string;
  repoResuelto?: string;
  estado: EstadoReporte;
  puntosGanados: number;
  usuario: Usuario;
  tipoIncidente: TipoIncidente;
  tipoClasificacion: TipoClasificacion;
  distrito: Distrito;
}

export interface ReporteFiltro {
  idReporte: number;
  imagenRepo: string;
  estado: EstadoReporte;
  idTipoInci: number;
  idTipoClasi: number;
  detalleRepo: string;
  repoRegistado: string;
}

export interface Usuario {
  idUsu: number;
  nomUsu: string;
  apePatUsu: string;
  apeMatUsu: string;
  correoUsu: string;
  telefonoUsu: string;
}

export interface TipoIncidente {
  idTipoInci: number;
  descTipoInci: string;
}

export interface TipoClasificacion {
  idTipoClasi: number;
  descTipoClasi: string;
}

export interface Distrito {
  idDistrito: number;
  descDistrito: string;
}

export interface EstadoCatalogo {
  codigo: string;
  descripcion: string;
}

export type EstadoReporte = 'PE' | 'EP' | 'RE' | 'CA';

export interface FiltrosReporte {
  estado?: string;
  incidente?: number;
  clasificacion?: number;
}
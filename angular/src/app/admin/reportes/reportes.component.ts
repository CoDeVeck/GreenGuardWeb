import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ReporteService } from '../service/reporte.service';
import { 
  Reporte, 
  ReporteFiltro, 
  EstadoCatalogo, 
  TipoIncidente, 
  TipoClasificacion, 
  Distrito 
} from '../../shared/model/reporte.model';

@Component({
  selector: 'app-reportes-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesListComponent implements OnInit {
  reportes: ReporteFiltro[] = [];
  reporteSeleccionado: Reporte | null = null;
  loading = false;
  mostrarFiltros = true;
  mostrarModal = false;
  
  // Catálogos para filtros
  estados: EstadoCatalogo[] = [];
  tiposIncidentes: TipoIncidente[] = [];
  tiposClasificacion: TipoClasificacion[] = [];
  distritos: Distrito[] = [];
  
  filtrosForm: FormGroup;

  constructor(
    private reporteService: ReporteService,
    private fb: FormBuilder
  ) {
    this.filtrosForm = this.fb.group({
      estado: [''],
      incidente: [''],
      clasificacion: ['']
    });
  }

  ngOnInit(): void {
    this.cargarCatalogos();
    this.cargarReportes();
    
    // Aplicar filtros automáticamente cuando cambie
    this.filtrosForm.valueChanges.subscribe(() => {
      this.aplicarFiltros();
    });
  }

  cargarCatalogos(): void {
    this.reporteService.obtenerEstados().subscribe(data => this.estados = data);
    this.reporteService.obtenerTiposIncidentes().subscribe(data => this.tiposIncidentes = data);
    this.reporteService.obtenerTiposClasificacion().subscribe(data => this.tiposClasificacion = data);
    this.reporteService.obtenerDistritos().subscribe(data => this.distritos = data);
  }

  cargarReportes(): void {
    this.loading = true;
    this.reporteService.filtrarReportes({}).subscribe({
      next: (data) => {
        this.reportes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar reportes', err);
        this.loading = false;
      }
    });
  }

  aplicarFiltros(): void {
    this.loading = true;
    const filtros = {
      estado: this.filtrosForm.value.estado || undefined,
      incidente: this.filtrosForm.value.incidente || undefined,
      clasificacion: this.filtrosForm.value.clasificacion || undefined
    };

    this.reporteService.filtrarReportes(filtros).subscribe({
      next: (data) => {
        this.reportes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al filtrar reportes', err);
        this.loading = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtrosForm.reset();
    this.cargarReportes();
  }

  toggleFiltros(): void {
    this.mostrarFiltros = !this.mostrarFiltros;
  }

  verDetalle(id: number): void {
    this.reporteService.obtenerPorId(id).subscribe({
      next: (reporte) => {
        this.reporteSeleccionado = reporte;
        this.mostrarModal = true;
      },
      error: (err) => console.error('Error al obtener detalle', err)
    });
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.reporteSeleccionado = null;
  }

  cambiarEstado(id: number, nuevoEstado: string): void {
    if (!confirm(`¿Estás seguro de cambiar el estado a ${this.getEstadoTexto(nuevoEstado)}?`)) {
      return;
    }

    let request;
    switch (nuevoEstado) {
      case 'EP':
        request = this.reporteService.cambiarAEnProceso(id);
        break;
      case 'RE':
        request = this.reporteService.cambiarAResuelto(id);
        break;
      case 'CA':
        request = this.reporteService.cancelarReporte(id);
        break;
      default:
        return;
    }

    request.subscribe({
      next: () => {
        alert('Estado actualizado correctamente');
        this.aplicarFiltros();
        if (this.mostrarModal) {
          this.cerrarModal();
        }
      },
      error: (err) => {
        console.error('Error al cambiar estado', err);
        alert('Error al cambiar el estado');
      }
    });
  }

  getEstadoBadgeClass(estado: string): string {
    const classes: any = {
      'PE': 'bg-warning text-dark',
      'EP': 'bg-primary',
      'RE': 'bg-success',
      'CA': 'bg-danger'
    };
    return classes[estado] || 'bg-secondary';
  }

  getEstadoTexto(estado: string): string {
    const textos: any = {
      'PE': 'Pendiente',
      'EP': 'En Proceso',
      'RE': 'Resuelto',
      'CA': 'Cancelado'
    };
    return textos[estado] || estado;
  }

  getClasificacionBadge(idClasi: number): string {
    const badges: any = {
      1: 'bg-info',
      2: 'bg-warning',
      3: 'bg-orange',
      4: 'bg-danger'
    };
    return badges[idClasi] || 'bg-secondary';
  }

  getClasificacionTexto(idClasi: number): string {
    const clasi = this.tiposClasificacion.find(c => c.idTipoClasi === idClasi);
    return clasi ? clasi.descTipoClasi : 'N/A';
  }

  getIncidenteTexto(idInci: number): string {
    const inci = this.tiposIncidentes.find(i => i.idTipoInci === idInci);
    return inci ? inci.descTipoInci : 'N/A';
  }



  descargarCSV(): void {
  const filtros = {
    estado: this.filtrosForm.value.estado || undefined,
    incidente: this.filtrosForm.value.incidente || undefined,
    clasificacion: this.filtrosForm.value.clasificacion || undefined
  };

  this.reporteService.exportarCSV(filtros).subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `reportes_${new Date().getTime()}.csv`;
      link.click();
      window.URL.revokeObjectURL(url);
    },
    error: (err) => {
      console.error('Error al descargar CSV', err);
      alert('Error al descargar el archivo CSV');
    }
  });
}

descargarPDF(): void {
  const filtros = {
    estado: this.filtrosForm.value.estado || undefined,
    incidente: this.filtrosForm.value.incidente || undefined,
    clasificacion: this.filtrosForm.value.clasificacion || undefined
  };

  this.reporteService.exportarPDF(filtros).subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `reportes_${new Date().getTime()}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    },
    error: (err) => {
      console.error('Error al descargar PDF', err);
      alert('Error al descargar el archivo PDF');
    }
  });
}
}
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteService } from '../../service/reporte.service';
import { Reporte } from '../../shared/dto/reporteDTO';
import { EstadisticasGenerales} from '../../shared/dto/estadisticasGeneralesDTO';
import { ReporteStats} from '../../shared/dto/reporteStatsDTO';
import { AuthService } from '../../service/auth.service';
import { Chart, registerables } from 'chart.js';


Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('pieCanvas') pieCanvas!: ElementRef<HTMLCanvasElement>;
  private chart?: Chart;

  stats: EstadisticasGenerales = {
    totalReportes: 0,
    reportesPendientes: 0,
    reportesEnProceso: 0,
    reportesResueltos: 0,
    reportesCancelados: 0
  };

  clasificacionData: any[] = [];
  incidenteData: any[] = [];
  distritoData: any[] = [];
  ultimosReportes: Reporte[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private reporteService: ReporteService,
    private authService: AuthService
  ) {}



  ngOnInit(): void {
    this.cargarDatos();
  }

ngAfterViewInit() {
    
  }

  cargarDatos(): void {
    this.loading = true;

    // Cargar estadísticas generales
    this.reporteService.obtenerEstadisticasGenerales().subscribe({
      next: (data) => this.stats = data,
      error: (err) => console.error('Error al cargar estadísticas', err)
    });

    // Cargar reportes por clasificación
    this.reporteService.reportesPorClasificacion().subscribe({
      next: (data) => {
        this.clasificacionData = data.map(item => ({
          name: item.clasificacion,
          value: item.cantidad
        }));
      },
      error: (err) => console.error('Error al cargar clasificación', err)
    });

    // Cargar reportes por tipo de incidente
    this.reporteService.reportesPorTipoIncidente().subscribe({
      next: (data) => {
        this.incidenteData = data.map(item => ({
          name: item.tipoIncidente,
          value: item.cantidad
        }));
      },
      error: (err) => console.error('Error al cargar incidentes', err)
    });

    this.reporteService.reportesPorDistrito().subscribe({
  next: (data) => {
    this.distritoData = data.map(item => ({
      name: item.distrito,
      value: item.cantidad
    }));
    
    // Esperar a que el DOM se actualice
    setTimeout(() => {
      this.createChart();
    }, 100);
  },
  error: (err) => console.error('Error al cargar distritos', err)
});
  

    // Cargar últimos reportes
    this.reporteService.obtenerUltimosReportes().subscribe({
      next: (data) => {
        this.ultimosReportes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar últimos reportes', err);
        this.loading = false;
      }
    });
  }

createChart() {
  // Validar que existan datos y el canvas
  if (!this.pieCanvas || this.distritoData.length === 0) {
    console.log('No se puede crear el gráfico:', {
      pieCanvas: !!this.pieCanvas,
      dataLength: this.distritoData.length
    });
    return;
  }

  // Destruir gráfico anterior si existe
  if (this.chart) {
    this.chart.destroy();
  }

  const ctx = this.pieCanvas.nativeElement.getContext('2d');
  if (!ctx) {
    console.error('No se pudo obtener el contexto del canvas');
    return;
  }

  console.log('Creando gráfico con datos:', this.distritoData);

  // Crear el gráfico de pastel
  this.chart = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: this.distritoData.map(item => item.name),
      datasets: [{
        data: this.distritoData.map(item => item.value),
        backgroundColor: [
          '#FF6384',
          '#36A2EB',
          '#FFCE56',
          '#4BC0C0',
          '#9966FF',
          '#FF9F40',
          '#E74C3C',
          '#8E44AD',
          '#3498DB',
          '#2ECC71'
        ],
        borderColor: '#fff',
        borderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'right',
          labels: {
            padding: 15,
            font: {
              size: 13
            },
            usePointStyle: true,
            pointStyle: 'circle'
          }
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          padding: 12,
          callbacks: {
            label: (context) => {
              const label = context.label || '';
              const value = context.parsed || 0;
              const dataset = context.dataset.data as number[];
              const total = dataset.reduce((a, b) => a + b, 0);
              const percentage = ((value / total) * 100).toFixed(1);
              return `${label}: ${value} reportes (${percentage}%)`;
            }
          }
        }
      }
    }
  });

  console.log('Gráfico creado exitosamente');

  
}

  getEstadoClass(estado: string): string {
    const classes: any = {
      'PE': 'bg-yellow-100 text-yellow-800',
      'EP': 'bg-blue-100 text-blue-800',
      'RE': 'bg-green-100 text-green-800',
      'CA': 'bg-red-100 text-red-800'
    };
    return classes[estado] || 'bg-gray-100 text-gray-800';
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

  getBarColor(index: number): string {
  const colors = [
    'bg-green-600',
    'bg-blue-600',
    'bg-yellow-600',
    'bg-red-600',
    'bg-purple-600',
    'bg-pink-600',
    'bg-indigo-600'
  ];
  return colors[index % colors.length];
}

getBadgeColor(index: number): string {
  const colors = [
    'bg-success',
    'bg-primary', 
    'bg-warning',
    'bg-danger',
    'bg-info',
    'bg-secondary'
  ];
  return colors[index % colors.length];
}

getProgressColor(index: number): string {
  const colors = [
    'bg-success',
    'bg-primary',
    'bg-warning', 
    'bg-danger',
    'bg-info',
    'bg-secondary'
  ];
  return colors[index % colors.length];
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


ngOnDestroy() {
  // Limpiar el gráfico al destruir el componente
  if (this.chart) {
    this.chart.destroy();
  }
}
}
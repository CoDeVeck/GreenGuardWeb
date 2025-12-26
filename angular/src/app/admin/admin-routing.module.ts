import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from '../admin/dashboard/dashboard.component';
import { AdminLayoutComponent } from '../admin-layout/admin-layout.component.';
import { ReportesListComponent } from './reportes/reportes.component';

const routes: Routes = [{
  path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: { title: 'Dashboard' },
      },
      {
      path: 'reportes',
      component: ReportesListComponent,
      data: { title: 'Reportes' },
    }
    ]
    }
  
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }

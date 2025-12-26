import { Routes } from '@angular/router';

export const routes: Routes = [

    {
      path:'',
      redirectTo: 'auth/login',
      pathMatch: 'full'
    },
    {
      path: 'auth',
      loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
    },
    {
    path: 'cliente',
    loadChildren: () =>
      import('./cliente/cliente.module').then((m) => m.ClienteModule),
    },
    {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
    },
     {
    path: 'tienda',
    loadChildren: () =>
      import('./tienda/tienda.module').then((m) => m.TiendaModule),
    },
     {
    path: 'municipalidad',
    loadChildren: () =>
      import('./municipalidad/municipalidad.module').then((m) => m.MunicipalidadModule),
    },
    
    { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
    { path: '**', redirectTo: 'auth/login' },
];

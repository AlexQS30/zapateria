import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { CategoriaComponent } from './pages/categoria/categoria';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { MisPedidosComponent } from './pages/mis-pedidos/mis-pedidos';
import { ProfileComponent } from './pages/profile/profile';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'categoria/:name', component: CategoriaComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'perfil', component: ProfileComponent },
  { path: 'mis-pedidos', component: MisPedidosComponent },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    children: [
        { path: 'dashboard', component: AdminDashboardComponent }
    ]
  },
];


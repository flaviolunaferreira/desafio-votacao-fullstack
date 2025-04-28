import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PautaListComponent } from './pages/pautas/pauta-list/pauta-list.component';
import { PautaFormComponent } from './pages/pautas/pauta-form/pauta-form.component';
import { SessaoListComponent } from './pages/sessoes/sessao-list/sessao-list.component';
import { SessaoFormComponent } from './pages/sessoes/sessao-form/sessao-form.component';
import { VotoFormComponent } from './pages/votos/voto-form/voto-form.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'pautas', component: PautaListComponent },
  { path: 'pautas/novo', component: PautaFormComponent },
  { path: 'pautas/:id', component: PautaFormComponent },
  { path: 'sessoes', component: SessaoListComponent },
  { path: 'sessoes/novo', component: SessaoFormComponent },
  { path: 'sessoes/:id', component: SessaoFormComponent },
  { path: 'votos/novo', component: VotoFormComponent },
  { path: 'votos/:id', component: VotoFormComponent },
  { path: '**', component: NotFoundComponent }
];

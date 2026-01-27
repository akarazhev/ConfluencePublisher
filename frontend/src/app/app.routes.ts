import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./compose/compose.component').then(m => m.ComposeComponent)
  },
  {
    path: 'schedules',
    loadComponent: () => import('./schedules/schedules.component').then(m => m.SchedulesComponent)
  }
];

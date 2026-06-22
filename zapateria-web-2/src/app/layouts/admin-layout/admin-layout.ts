import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterOutlet],
  template: `
    <div class="admin-wrapper">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .admin-wrapper { min-height: 100vh; background: #f8f9fa; }
  `]
})
export class AdminLayoutComponent {}

import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex flex-col bg-gray-50">
      <header class="bg-white border-b">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div class="flex items-center justify-between">
            <h1 class="text-2xl font-bold text-gray-900">Confluence Publisher</h1>
            <nav class="flex space-x-4">
              <a routerLink="/" 
                 routerLinkActive="text-blue-600 font-semibold" 
                 [routerLinkActiveOptions]="{exact: true}"
                 class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
                Compose
              </a>
              <a routerLink="/schedules" 
                 routerLinkActive="text-blue-600 font-semibold" 
                 class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
                Schedules
              </a>
            </nav>
          </div>
        </div>
      </header>
      <main class="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-8">
        <router-outlet></router-outlet>
      </main>
      <footer class="border-t bg-white py-4">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <p class="text-sm text-gray-600">© 2024 Confluence Publisher</p>
        </div>
      </footer>
    </div>
  `,
  styles: []
})
export class AppComponent {
}

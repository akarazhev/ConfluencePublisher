import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex flex-col bg-gray-50">
      <header class="bg-white border-b border-gray-200">
        <div class="max-w-7xl mx-auto px-4 py-4 sm:px-6 lg:px-8">
          <div class="flex items-center justify-between">
            <h1 class="text-2xl font-bold text-gray-900">Confluence Publisher</h1>
            <nav class="flex gap-4">
              <a 
                routerLink="/" 
                routerLinkActive="text-blue-600 font-semibold" 
                [routerLinkActiveOptions]="{exact: true}"
                class="px-4 py-2 text-gray-700 hover:text-blue-600 transition-colors">
                Compose
              </a>
              <a 
                routerLink="/schedules" 
                routerLinkActive="text-blue-600 font-semibold"
                class="px-4 py-2 text-gray-700 hover:text-blue-600 transition-colors">
                Schedules
              </a>
            </nav>
          </div>
        </div>
      </header>
      <main class="flex-1">
        <div class="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
          <router-outlet></router-outlet>
        </div>
      </main>
      <footer class="border-t border-gray-200 bg-white">
        <div class="max-w-7xl mx-auto px-4 py-4 sm:px-6 lg:px-8">
          <p class="text-sm text-gray-600 text-center">© 2024 Confluence Publisher</p>
        </div>
      </footer>
    </div>
  `,
  styles: []
})
export class AppComponent {}


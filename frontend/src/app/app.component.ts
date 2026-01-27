import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen flex flex-col bg-gray-50">
      <header class="bg-white border-b border-gray-200">
        <div class="container mx-auto px-4 py-4">
          <div class="flex items-center justify-between">
            <h1 class="text-xl font-semibold text-gray-900">Confluence Publisher</h1>
            <nav class="flex gap-4">
              <a 
                routerLink="/" 
                routerLinkActive="text-blue-600 font-medium"
                class="text-gray-700 hover:text-gray-900 transition-colors"
              >
                Compose
              </a>
              <a 
                routerLink="/schedules" 
                routerLinkActive="text-blue-600 font-medium"
                class="text-gray-700 hover:text-gray-900 transition-colors"
              >
                Schedules
              </a>
            </nav>
          </div>
        </div>
      </header>
      
      <main class="flex-1 container mx-auto px-4 py-8">
        <router-outlet></router-outlet>
      </main>
      
      <footer class="bg-white border-t border-gray-200 py-4">
        <div class="container mx-auto px-4">
          <p class="text-sm text-gray-600 text-center">
            © 2024 Confluence Publisher
          </p>
        </div>
      </footer>
    </div>
  `,
  styles: []
})
export class AppComponent {
  title = 'Confluence Publisher';
}

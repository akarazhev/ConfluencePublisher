import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  template: `
    <div class="px-4 py-6 sm:px-0">
      <div class="border-4 border-dashed border-gray-200 rounded-lg h-96 flex items-center justify-center">
        <div class="text-center">
          <h2 class="text-2xl font-semibold text-gray-700 mb-2">Welcome to Confluence Publisher</h2>
          <p class="text-gray-500">Application is ready for development</p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class HomeComponent {
}


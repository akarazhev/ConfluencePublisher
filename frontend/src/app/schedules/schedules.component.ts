import { Component, ChangeDetectionStrategy, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ApiService, Schedule } from '../services/api.service';

@Component({
  selector: 'app-schedules',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="max-w-6xl mx-auto p-6">
      <!-- Header Row -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-900">Publication Schedules</h2>
        <button
          type="button"
          (click)="load()"
          [disabled]="busy()"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Refresh
        </button>
      </div>

      <!-- Data Table -->
      <div class="bg-white rounded-lg shadow-md border border-gray-200 overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">ID</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">Page ID</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">Status</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">Scheduled</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">Attempts</th>
              <th class="px-4 py-3 text-left text-xs font-medium text-gray-700 uppercase tracking-wider border-b border-gray-200">Error</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            @if (rows().length === 0) {
              <tr>
                <td colspan="6" class="px-4 py-8 text-center text-gray-500">
                  No schedules found
                </td>
              </tr>
            } @else {
              @for (schedule of rows(); track schedule.id; let even = $even) {
                <tr [class.bg-gray-50]="even" [class.bg-white]="!even">
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ schedule.id }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ schedule.pageId }}</td>
                  <td class="px-4 py-3 text-sm border-b border-gray-200">
                    <span [class]="getStatusClass(schedule.status)">
                      {{ schedule.status }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">
                    {{ formatDate(schedule.scheduledAt) }}
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ schedule.attemptCount }}</td>
                  <td class="px-4 py-3 text-sm border-b border-gray-200">
                    @if (schedule.lastError) {
                      <span 
                        class="text-red-600 truncate block" 
                        [title]="schedule.lastError"
                      >
                        {{ truncateError(schedule.lastError) }}
                      </span>
                    } @else {
                      <span class="text-gray-400">-</span>
                    }
                  </td>
                </tr>
              }
            }
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class SchedulesComponent implements OnInit, OnDestroy {
  private apiService = inject(ApiService);
  private refreshInterval: number | null = null;

  // State signals
  rows = signal<Schedule[]>([]);
  busy = signal<boolean>(false);

  ngOnInit(): void {
    // Load immediately
    this.load();

    // Set up auto-refresh every 5 seconds (SSR safety check)
    if (typeof window !== 'undefined') {
      this.refreshInterval = window.setInterval(() => {
        this.load();
      }, 5000);
    }
  }

  ngOnDestroy(): void {
    // Clear the interval
    if (this.refreshInterval !== null && typeof window !== 'undefined') {
      window.clearInterval(this.refreshInterval);
    }
  }

  async load(): Promise<void> {
    this.busy.set(true);
    try {
      const schedules = await firstValueFrom(this.apiService.getSchedules());
      this.rows.set(schedules);
    } catch (error) {
      console.error('Failed to load schedules:', error);
      // On error, keep existing data but log the error
    } finally {
      this.busy.set(false);
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) {
      return '-';
    }
    try {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('en-US', {
        month: 'numeric',
        day: 'numeric',
        year: '2-digit',
        hour: 'numeric',
        minute: '2-digit',
        hour12: true
      }).format(date);
    } catch (error) {
      console.error('Error formatting date:', error);
      return dateString;
    }
  }

  getStatusClass(status: string): string {
    const baseClasses = 'px-2 py-1 rounded-full text-xs font-medium';
    switch (status.toLowerCase()) {
      case 'posted':
        return `${baseClasses} bg-green-100 text-green-800`;
      case 'queued':
        return `${baseClasses} bg-yellow-100 text-yellow-800`;
      case 'failed':
        return `${baseClasses} bg-red-100 text-red-800`;
      default:
        return `${baseClasses} bg-gray-100 text-gray-800`;
    }
  }

  truncateError(error: string): string {
    if (!error) {
      return '-';
    }
    const maxLength = 30;
    if (error.length <= maxLength) {
      return error;
    }
    return error.substring(0, maxLength) + '...';
  }
}

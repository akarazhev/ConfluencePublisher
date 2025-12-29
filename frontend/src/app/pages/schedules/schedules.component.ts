import { Component, OnInit, OnDestroy, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, Schedule } from '../../services/api.service';

@Component({
  selector: 'app-schedules',
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="p-6">
      <!-- Header Row -->
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-2xl font-bold text-gray-900">Publication Schedules</h2>
        <button
          (click)="load()"
          [disabled]="busy()"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ busy() ? 'Loading...' : 'Refresh' }}
        </button>
      </div>

      <!-- Data Table -->
      <div class="overflow-x-auto">
        <table class="min-w-full bg-white border border-gray-200">
          <thead class="bg-gray-100">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">ID</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">Page ID</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">Status</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">Scheduled</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">Attempts</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700 border-b border-gray-200">Error</th>
            </tr>
          </thead>
          <tbody>
            @if (rows().length === 0) {
              <tr>
                <td colspan="6" class="px-4 py-8 text-center text-gray-500">
                  {{ busy() ? 'Loading schedules...' : 'No schedules found' }}
                </td>
              </tr>
            } @else {
              @for (row of rows(); track row.id; let even = $even) {
                <tr [class.bg-gray-50]="even" [class.bg-white]="!even">
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ row.id }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ row.pageId }}</td>
                  <td class="px-4 py-3 text-sm border-b border-gray-200">
                    <span [class]="getStatusClass(row.status)" class="px-2 py-1 rounded-full text-xs font-medium">
                      {{ row.status }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ formatDate(row.scheduledAt) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900 border-b border-gray-200">{{ row.attemptCount }}</td>
                  <td class="px-4 py-3 text-sm border-b border-gray-200">
                    @if (row.lastError) {
                      <span class="text-red-600 truncate block" [title]="row.lastError">
                        {{ truncateError(row.lastError) }}
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
  `,
  styles: []
})
export class SchedulesComponent implements OnInit, OnDestroy {
  // State using Angular Signals
  rows = signal<Schedule[]>([]);
  busy = signal<boolean>(false);

  private intervalId?: number;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    // Load immediately
    this.load();

    // Set up auto-refresh every 5 seconds (SSR safe)
    if (typeof window !== 'undefined') {
      this.intervalId = window.setInterval(() => {
        this.load();
      }, 5000);
    }
  }

  ngOnDestroy(): void {
    // Clear interval to prevent memory leaks
    if (this.intervalId !== undefined) {
      clearInterval(this.intervalId);
    }
  }

  /**
   * Load schedules from API and update rows signal
   */
  load(): void {
    this.busy.set(true);
    this.apiService.getSchedules().subscribe({
      next: (schedules) => {
        this.rows.set(schedules);
        this.busy.set(false);
      },
      error: (error) => {
        console.error('Failed to load schedules:', error);
        this.busy.set(false);
      }
    });
  }

  /**
   * Format ISO date string to localized date
   */
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      month: '2-digit',
      day: '2-digit',
      year: '2-digit',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    });
  }

  /**
   * Get Tailwind CSS classes for status badge based on status value
   */
  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'posted':
        return 'bg-green-100 text-green-800';
      case 'queued':
        return 'bg-yellow-100 text-yellow-800';
      case 'failed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  /**
   * Truncate error message to 30 characters with ellipsis
   */
  truncateError(error: string): string {
    if (error.length <= 30) {
      return error;
    }
    return error.substring(0, 30) + '...';
  }
}


import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { ApiService, Schedule } from '../../services/api.service';

@Component({
  selector: 'app-schedules',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="container mx-auto px-4 py-8 space-y-4">
      <!-- Header Row -->
      <div class="flex items-center justify-between">
        <h2 class="text-2xl font-bold text-gray-900">Publication Schedules</h2>
        <button
          type="button"
          (click)="load()"
          [disabled]="busy()"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Refresh
        </button>
      </div>

      <!-- Data Table -->
      <div class="overflow-x-auto bg-white rounded-lg shadow border border-gray-200">
        <table class="w-full">
          <thead>
            <tr class="bg-gray-100 border-b border-gray-200">
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Page ID</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Status</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Scheduled</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Attempts</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Error</th>
            </tr>
          </thead>
          <tbody>
            @if (rows().length === 0) {
              <tr>
                <td colspan="6" class="px-4 py-8 text-center text-gray-500">
                  No schedules found
                </td>
              </tr>
            } @else {
              @for (schedule of rows(); track schedule.id; let even = $even) {
                <tr [class]="even ? 'bg-gray-50' : 'bg-white'" class="border-b border-gray-200">
                  <td class="px-4 py-3 text-sm text-gray-900">{{ schedule.id }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900">{{ schedule.pageId }}</td>
                  <td class="px-4 py-3 text-sm">
                    <span [class]="getStatusClass(schedule.status)" class="px-2 py-1 rounded-md text-xs font-medium">
                      {{ schedule.status }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-900">{{ formatDate(schedule.scheduledAt) }}</td>
                  <td class="px-4 py-3 text-sm text-gray-900">{{ schedule.attemptCount }}</td>
                  <td class="px-4 py-3 text-sm">
                    @if (schedule.lastError) {
                      <span
                        class="text-red-600 truncate"
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
  `,
  styles: []
})
export class SchedulesComponent implements OnInit, OnDestroy {
  private apiService = inject(ApiService);
  private refreshInterval: number | null = null;

  // State signals
  rows = signal<Schedule[]>([]);
  busy = signal<boolean>(false);

  ngOnInit(): void {
    // Call load immediately
    this.load();

    // Set up interval for auto-refresh (SSR safety check)
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
      this.refreshInterval = null;
    }
  }

  async load(): Promise<void> {
    this.busy.set(true);
    try {
      const schedules = await firstValueFrom(this.apiService.getSchedules());
      this.rows.set(schedules);
    } catch (error) {
      console.error('Failed to load schedules:', error);
      alert('Failed to load schedules. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
      month: 'numeric',
      day: 'numeric',
      year: '2-digit',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    }).format(date);
  }

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

  truncateError(error: string): string {
    if (error.length <= 30) {
      return error;
    }
    return error.substring(0, 30) + '...';
  }
}

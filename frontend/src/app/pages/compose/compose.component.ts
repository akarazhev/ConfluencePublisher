import { Component, OnInit, ChangeDetectionStrategy, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ApiService, Attachment } from '../../services/api.service';

@Component({
  selector: 'app-compose',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="container mx-auto px-4 py-8 space-y-8">
      <!-- Page Creation Section -->
      <section class="bg-white rounded-lg shadow p-6 space-y-4">
        <h2 class="text-2xl font-bold text-gray-900">Create Page</h2>
        
        <div class="space-y-4">
          <div>
            <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
              Title <span class="text-red-500">*</span>
            </label>
            <input
              id="title"
              type="text"
              [(ngModel)]="title"
              [disabled]="busy()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder="Enter page title"
            />
          </div>

          <div>
            <label for="spaceKey" class="block text-sm font-medium text-gray-700 mb-1">
              Space Key
            </label>
            <input
              id="spaceKey"
              type="text"
              [(ngModel)]="spaceKey"
              [disabled]="busy()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder="Leave empty to use default"
            />
          </div>

          <div>
            <label for="parentPageId" class="block text-sm font-medium text-gray-700 mb-1">
              Parent Page ID
            </label>
            <input
              id="parentPageId"
              type="number"
              [(ngModel)]="parentPageId"
              [disabled]="busy()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder="Optional parent page ID"
            />
          </div>

          <div>
            <label for="content" class="block text-sm font-medium text-gray-700 mb-1">
              Content <span class="text-red-500">*</span>
            </label>
            <textarea
              id="content"
              [(ngModel)]="content"
              [disabled]="busy()"
              rows="8"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
              placeholder="Enter page content"
            ></textarea>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-wrap gap-3">
            <button
              type="button"
              (click)="improveContent()"
              [disabled]="!content().trim() || busy()"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Improve content
            </button>

            <button
              type="button"
              (click)="createPage()"
              [disabled]="!title().trim() || !content().trim() || busy()"
              class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Create page
            </button>

            <button
              type="button"
              (click)="publishNow()"
              [disabled]="pageId() === null || busy()"
              class="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Publish now
            </button>

            <button
              type="button"
              (click)="schedule()"
              [disabled]="pageId() === null || busy()"
              class="px-4 py-2 bg-amber-600 text-white rounded-md hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-amber-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Schedule
            </button>
          </div>

          <!-- Suggestions List -->
          @if (suggestions().length > 0) {
            <div class="mt-4">
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Content Suggestions</h3>
              <ul class="space-y-2">
                @for (suggestion of suggestions(); track $index) {
                  <li>
                    <button
                      type="button"
                      (click)="content.set(suggestion)"
                      class="w-full text-left px-4 py-2 bg-gray-50 border border-gray-200 rounded-md hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    >
                      {{ suggestion }}
                    </button>
                  </li>
                }
              </ul>
            </div>
          }
        </div>
      </section>

      <!-- Attachments Section -->
      <section class="bg-white rounded-lg shadow p-6 space-y-4">
        <h2 class="text-2xl font-bold text-gray-900">Attachments</h2>
        
        <div class="space-y-4">
          <div>
            <label for="fileInput" class="block text-sm font-medium text-gray-700 mb-1">
              Select Files
            </label>
            <input
              id="fileInput"
              type="file"
              multiple
              (change)="onFiles($event)"
              [disabled]="busy()"
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            />
          </div>

          <!-- File Descriptions -->
          @if (files().length > 0) {
            <div class="space-y-3">
              @for (file of files(); track $index) {
                <div class="flex items-center gap-3">
                  <span class="flex-1 text-sm text-gray-700">{{ file.name }}</span>
                  <input
                    type="text"
                    [value]="descriptions()[$index] || ''"
                    (input)="updateDescription($index, $any($event.target).value)"
                    [disabled]="busy()"
                    placeholder="Description (optional)"
                    class="flex-1 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                  />
                </div>
              }
            </div>
          }

          <button
            type="button"
            (click)="uploadAll()"
            [disabled]="!canUpload() || busy()"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Upload
          </button>

          <!-- Uploaded Attachments List -->
          @if (attachments().length > 0) {
            <div class="mt-4">
              <h3 class="text-lg font-semibold text-gray-900 mb-2">Uploaded Attachments</h3>
              <ul class="space-y-2">
                @for (attachment of attachments(); track attachment.id) {
                  <li class="px-4 py-2 bg-gray-50 border border-gray-200 rounded-md">
                    <div class="font-medium text-gray-900">{{ attachment.filename }}</div>
                    @if (attachment.description) {
                      <div class="text-sm text-gray-600">{{ attachment.description }}</div>
                    }
                  </li>
                }
              </ul>
            </div>
          }
        </div>
      </section>
    </div>
  `,
  styles: []
})
export class ComposeComponent implements OnInit {
  private apiService = inject(ApiService);

  // State signals
  title = signal<string>('');
  content = signal<string>('');
  spaceKey = signal<string>('');
  parentPageId = signal<string>('');
  files = signal<File[]>([]);
  descriptions = signal<string[]>([]);
  attachments = signal<Attachment[]>([]);
  busy = signal<boolean>(false);
  suggestions = signal<string[]>([]);
  pageId = signal<number | null>(null);
  scheduleId = signal<number | null>(null);

  // Computed signal
  canUpload = computed(() => this.files().length > 0);

  async ngOnInit(): Promise<void> {
    try {
      const config = await firstValueFrom(this.apiService.getConfig());
      this.spaceKey.set(config.defaultSpace);
    } catch (error) {
      console.error('Failed to load config:', error);
      alert('Failed to load configuration');
    }
  }

  onFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const fileArray = Array.from(input.files);
      this.files.set(fileArray);
      // Initialize descriptions array with empty strings
      this.descriptions.set(new Array(fileArray.length).fill(''));
    }
  }

  updateDescription(index: number, value: string): void {
    const currentDescriptions = [...this.descriptions()];
    currentDescriptions[index] = value;
    this.descriptions.set(currentDescriptions);
  }

  async uploadAll(): Promise<void> {
    if (this.files().length === 0) {
      return;
    }

    this.busy.set(true);
    try {
      const uploadPromises = this.files().map((file, index) => {
        const description = this.descriptions()[index]?.trim() || undefined;
        return firstValueFrom(this.apiService.uploadAttachment(file, description));
      });

      const uploadedAttachments = await Promise.all(uploadPromises);
      
      // Add to existing attachments
      this.attachments.set([...this.attachments(), ...uploadedAttachments]);
      
      // Clear files and descriptions
      this.files.set([]);
      this.descriptions.set([]);
      
      // Reset file input
      const fileInput = document.getElementById('fileInput') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = '';
      }

      alert(`Successfully uploaded ${uploadedAttachments.length} file(s)`);
    } catch (error) {
      console.error('Upload error:', error);
      alert('Failed to upload files. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  async improveContent(): Promise<void> {
    if (!this.content().trim()) {
      return;
    }

    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.improveContent(this.content())
      );
      this.suggestions.set(response.suggestions);
    } catch (error) {
      console.error('Content improvement error:', error);
      alert('Failed to get content suggestions. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  async createPage(): Promise<void> {
    if (!this.title().trim() || !this.content().trim()) {
      return;
    }

    this.busy.set(true);
    try {
      const attachmentIds = this.attachments().map(a => a.id);
      const parentPageIdStr = this.parentPageId().trim();
      let parentPageIdValue: number | undefined = undefined;
      if (parentPageIdStr) {
        const parsed = Number.parseInt(parentPageIdStr, 10);
        if (!Number.isNaN(parsed)) {
          parentPageIdValue = parsed;
        }
      }
      const spaceKeyValue = this.spaceKey().trim() || undefined;

      const response = await firstValueFrom(
        this.apiService.createPage(
          this.title(),
          this.content(),
          spaceKeyValue,
          attachmentIds.length > 0 ? attachmentIds : undefined,
          parentPageIdValue
        )
      );

      this.pageId.set(response.id);
      alert(`Page created successfully with ID: ${response.id}`);
    } catch (error) {
      console.error('Page creation error:', error);
      alert('Failed to create page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  async publishNow(): Promise<void> {
    if (this.pageId() === null) {
      return;
    }

    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.publishNow(this.pageId()!)
      );
      alert(`Publish status: ${response.status}${response.confluencePageId ? ` (Page ID: ${response.confluencePageId})` : ''}`);
    } catch (error) {
      console.error('Publish error:', error);
      alert('Failed to publish page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  async schedule(): Promise<void> {
    if (this.pageId() === null) {
      return;
    }

    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.schedulePage(this.pageId()!)
      );
      this.scheduleId.set(response.id);
      alert(`Page scheduled successfully with schedule ID: ${response.id}`);
    } catch (error) {
      console.error('Schedule error:', error);
      alert('Failed to schedule page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }
}

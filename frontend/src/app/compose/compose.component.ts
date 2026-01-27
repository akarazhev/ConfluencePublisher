import { Component, ChangeDetectionStrategy, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { ApiService, Attachment } from '../services/api.service';

@Component({
  selector: 'app-compose',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="max-w-4xl mx-auto p-6 space-y-8">
      <!-- Page Creation Section -->
      <section class="bg-white rounded-lg shadow-md p-6 space-y-4">
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Create Page</h2>
        
        <!-- Title Input -->
        <div>
          <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
            Title <span class="text-red-500">*</span>
          </label>
          <input
            id="title"
            type="text"
            [(ngModel)]="title"
            [disabled]="busy()"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
            placeholder="Enter page title"
          />
        </div>

        <!-- Space Key Input -->
        <div>
          <label for="spaceKey" class="block text-sm font-medium text-gray-700 mb-1">
            Space Key
          </label>
          <input
            id="spaceKey"
            type="text"
            [(ngModel)]="spaceKey"
            [disabled]="busy()"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
            placeholder="Enter space key (optional)"
          />
        </div>

        <!-- Parent Page ID Input -->
        <div>
          <label for="parentPageId" class="block text-sm font-medium text-gray-700 mb-1">
            Parent Page ID
          </label>
          <input
            id="parentPageId"
            type="number"
            [(ngModel)]="parentPageId"
            [disabled]="busy()"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
            placeholder="Enter parent page ID (optional)"
          />
        </div>

        <!-- Content Textarea -->
        <div>
          <label for="content" class="block text-sm font-medium text-gray-700 mb-1">
            Content <span class="text-red-500">*</span>
          </label>
          <textarea
            id="content"
            [(ngModel)]="content"
            [disabled]="busy()"
            rows="8"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
            placeholder="Enter page content"
          ></textarea>
        </div>

        <!-- Suggestions List -->
        @if (suggestions().length > 0) {
          <div class="bg-blue-50 border border-blue-200 rounded-md p-4">
            <h3 class="text-sm font-semibold text-blue-900 mb-2">Content Improvement Suggestions:</h3>
            <ul class="space-y-2">
              @for (suggestion of suggestions(); track $index) {
                <li
                  (click)="applySuggestion(suggestion)"
                  class="cursor-pointer text-sm text-blue-800 hover:text-blue-900 hover:bg-blue-100 p-2 rounded transition-colors"
                >
                  {{ suggestion }}
                </li>
              }
            </ul>
          </div>
        }

        <!-- Action Buttons -->
        <div class="flex flex-wrap gap-3 pt-2">
          <button
            type="button"
            (click)="improveContent()"
            [disabled]="!content().trim() || busy()"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Improve Content
          </button>
          
          <button
            type="button"
            (click)="createPage()"
            [disabled]="!title().trim() || !content().trim() || busy()"
            class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Create Page
          </button>
          
          <button
            type="button"
            (click)="publishNow()"
            [disabled]="pageId() === null || busy()"
            class="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Publish Now
          </button>
          
          <button
            type="button"
            (click)="schedule()"
            [disabled]="pageId() === null || busy()"
            class="px-4 py-2 bg-amber-600 text-white rounded-md hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-amber-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Schedule
          </button>
        </div>
      </section>

      <!-- Attachments Section -->
      <section class="bg-white rounded-lg shadow-md p-6 space-y-4">
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Attachments</h2>
        
        <!-- File Input -->
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
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
          />
        </div>

        <!-- File List with Descriptions -->
        @if (files().length > 0) {
          <div class="space-y-3">
            <h3 class="text-sm font-semibold text-gray-700">Selected Files:</h3>
            @for (file of files(); track $index) {
              <div class="border border-gray-200 rounded-md p-3 bg-gray-50">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm font-medium text-gray-900">{{ file.name }}</span>
                  <span class="text-xs text-gray-500">{{ formatFileSize(file.size) }}</span>
                </div>
                <input
                  type="text"
                  [value]="descriptions()[$index] || ''"
                  (input)="updateDescription($index, $any($event.target).value)"
                  [disabled]="busy()"
                  class="w-full px-3 py-2 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:opacity-50 disabled:cursor-not-allowed"
                  placeholder="Enter description (optional)"
                />
              </div>
            }
            
            <button
              type="button"
              (click)="uploadAll()"
              [disabled]="!canUpload() || busy()"
              class="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              Upload All Files
            </button>
          </div>
        }

        <!-- Uploaded Attachments List -->
        @if (attachments().length > 0) {
          <div class="space-y-2">
            <h3 class="text-sm font-semibold text-gray-700">Uploaded Attachments:</h3>
            <ul class="space-y-2">
              @for (attachment of attachments(); track attachment.id) {
                <li class="flex items-center justify-between p-3 bg-green-50 border border-green-200 rounded-md">
                  <div>
                    <span class="text-sm font-medium text-gray-900">{{ attachment.filename }}</span>
                    @if (attachment.description) {
                      <p class="text-xs text-gray-600 mt-1">{{ attachment.description }}</p>
                    }
                  </div>
                  <span class="text-xs text-green-700 font-medium">✓ Uploaded</span>
                </li>
              }
            </ul>
          </div>
        }
      </section>
    </div>
  `
})
export class ComposeComponent {
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

  // Computed signals
  canUpload = computed(() => this.files().length > 0);

  constructor() {
    // Load default space from config
    this.loadDefaultSpace();
  }

  private async loadDefaultSpace(): Promise<void> {
    try {
      const config = await firstValueFrom(this.apiService.getConfig());
      if (config.defaultSpace) {
        this.spaceKey.set(config.defaultSpace);
      }
    } catch (error) {
      console.error('Failed to load default space:', error);
      // Don't show alert here, just log - space key can be entered manually
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

  applySuggestion(suggestion: string): void {
    this.content.set(suggestion);
    this.suggestions.set([]);
  }

  async createPage(): Promise<void> {
    if (!this.title().trim() || !this.content().trim()) {
      return;
    }

    this.busy.set(true);
    try {
      const attachmentIds = this.attachments().map(a => a.id);
      const spaceKeyValue = this.spaceKey().trim() || undefined;
      const parentPageIdValue = this.parentPageId().trim() 
        ? parseInt(this.parentPageId().trim(), 10) 
        : undefined;

      const response = await firstValueFrom(
        this.apiService.createPage(
          this.title().trim(),
          this.content().trim(),
          spaceKeyValue,
          attachmentIds.length > 0 ? attachmentIds : undefined,
          parentPageIdValue
        )
      );

      this.pageId.set(response.id);
      alert(`Page created successfully! Page ID: ${response.id}`);
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
      
      const message = response.confluencePageId
        ? `Page published successfully! Confluence Page ID: ${response.confluencePageId}`
        : `Publish status: ${response.status}`;
      alert(message);
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
      alert(`Page scheduled successfully! Schedule ID: ${response.id}`);
    } catch (error) {
      console.error('Schedule error:', error);
      alert('Failed to schedule page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }
}

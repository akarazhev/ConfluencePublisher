import { Component, signal, computed, inject, ChangeDetectionStrategy } from '@angular/core';
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
    <div class="p-6 max-w-4xl mx-auto space-y-8">
      <!-- Page Creation Section -->
      <section class="bg-white rounded-lg shadow p-6 space-y-4">
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
            placeholder="Enter page title"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
            placeholder="Enter Confluence space key (optional)"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
            placeholder="Enter parent page ID (optional)"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
            rows="8"
            placeholder="Enter page content"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          ></textarea>
        </div>

        <!-- Action Buttons -->
        <div class="flex flex-wrap gap-3">
          <button
            (click)="improveContent()"
            [disabled]="!content() || busy()"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Improve Content
          </button>
          
          <button
            (click)="createPage()"
            [disabled]="!title() || !content() || busy()"
            class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Create Page
          </button>
          
          <button
            (click)="publishNow()"
            [disabled]="!pageId() || busy()"
            class="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Publish Now
          </button>
          
          <button
            (click)="schedule()"
            [disabled]="!pageId() || busy()"
            class="px-4 py-2 bg-amber-600 text-white rounded-md hover:bg-amber-700 focus:outline-none focus:ring-2 focus:ring-amber-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Schedule
          </button>
        </div>

        <!-- Suggestions List -->
        @if (suggestions().length > 0) {
          <div class="mt-4">
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Suggestions:</h3>
            <ul class="space-y-2">
              @for (suggestion of suggestions(); track $index) {
                <li
                  (click)="content.set(suggestion)"
                  class="p-3 bg-blue-50 border border-blue-200 rounded-md cursor-pointer hover:bg-blue-100 transition-colors"
                >
                  {{ suggestion }}
                </li>
              }
            </ul>
          </div>
        }
      </section>

      <!-- Attachments Section -->
      <section class="bg-white rounded-lg shadow p-6 space-y-4">
        <h2 class="text-2xl font-bold text-gray-900 mb-4">Attachments</h2>
        
        <!-- File Input -->
        <div>
          <label for="files" class="block text-sm font-medium text-gray-700 mb-1">
            Select Files
          </label>
          <input
            id="files"
            type="file"
            multiple
            (change)="onFiles($event)"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <!-- Selected Files with Description Inputs -->
        @if (files().length > 0) {
          <div class="space-y-3">
            <h3 class="text-lg font-semibold text-gray-900">Selected Files:</h3>
            @for (file of files(); track $index) {
              <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-md">
                <span class="flex-shrink-0 text-sm font-medium text-gray-700">
                  {{ file.name }}
                </span>
                <input
                  type="text"
                  [value]="descriptions()[$index] || ''"
                  (input)="updateDescription($index, $any($event.target).value)"
                  placeholder="Enter description (optional)"
                  class="flex-1 px-3 py-1 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>
            }
            
            <button
              (click)="uploadAll()"
              [disabled]="!canUpload() || busy()"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Upload All
            </button>
          </div>
        }

        <!-- Uploaded Attachments List -->
        @if (attachments().length > 0) {
          <div class="mt-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Uploaded Attachments:</h3>
            <ul class="space-y-2">
              @for (attachment of attachments(); track attachment.id) {
                <li class="p-3 bg-green-50 border border-green-200 rounded-md">
                  <div class="flex items-center gap-2">
                    <span class="font-medium text-gray-900">{{ attachment.filename }}</span>
                    @if (attachment.description) {
                      <span class="text-sm text-gray-600">- {{ attachment.description }}</span>
                    }
                  </div>
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

  // Computed signal
  canUpload = computed(() => this.files().length > 0);

  constructor() {
    // Load default space configuration
    this.loadDefaultSpace();
  }

  /**
   * Load default space from API configuration
   */
  private async loadDefaultSpace(): Promise<void> {
    try {
      const config = await firstValueFrom(this.apiService.getConfig());
      this.spaceKey.set(config.defaultSpace);
    } catch (error) {
      console.error('Failed to load default space:', error);
    }
  }

  /**
   * Handle file selection
   */
  onFiles(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const fileArray = Array.from(input.files);
      this.files.set(fileArray);
      this.descriptions.set(new Array(fileArray.length).fill(''));
    }
  }

  /**
   * Update description at specific index
   */
  updateDescription(index: number, value: string): void {
    const current = [...this.descriptions()];
    current[index] = value;
    this.descriptions.set(current);
  }

  /**
   * Upload all selected files
   */
  async uploadAll(): Promise<void> {
    this.busy.set(true);
    try {
      const uploadPromises = this.files().map((file, index) => {
        const description = this.descriptions()[index];
        return firstValueFrom(
          this.apiService.uploadAttachment(file, description || undefined)
        );
      });

      const uploadedAttachments = await Promise.all(uploadPromises);
      this.attachments.set([...this.attachments(), ...uploadedAttachments]);
      
      // Clear files and descriptions
      this.files.set([]);
      this.descriptions.set([]);
      
      // Reset file input
      const fileInput = document.getElementById('files') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = '';
      }
      
      alert(`Successfully uploaded ${uploadedAttachments.length} file(s)`);
    } catch (error) {
      console.error('Upload failed:', error);
      alert('Failed to upload files. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  /**
   * Get AI-powered content improvement suggestions
   */
  async improveContent(): Promise<void> {
    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.improveContent(this.content())
      );
      this.suggestions.set(response.suggestions);
      alert(`Generated ${response.suggestions.length} suggestion(s)`);
    } catch (error) {
      console.error('Content improvement failed:', error);
      alert('Failed to improve content. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  /**
   * Create a new page
   */
  async createPage(): Promise<void> {
    this.busy.set(true);
    try {
      const attachmentIds = this.attachments().map(att => att.id);
      const parentId = this.parentPageId() ? Number(this.parentPageId()) : undefined;
      
      const response = await firstValueFrom(
        this.apiService.createPage(
          this.title(),
          this.content(),
          this.spaceKey(),
          attachmentIds,
          parentId
        )
      );
      
      this.pageId.set(response.id);
      alert(`Page created successfully! ID: ${response.id}`);
    } catch (error) {
      console.error('Page creation failed:', error);
      alert('Failed to create page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  /**
   * Publish page to Confluence immediately
   */
  async publishNow(): Promise<void> {
    const id = this.pageId();
    if (!id) return;

    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.publishNow(id)
      );
      alert(`Publish status: ${response.status}${response.confluencePageId ? ` - Confluence Page ID: ${response.confluencePageId}` : ''}`);
    } catch (error) {
      console.error('Publish failed:', error);
      alert('Failed to publish page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }

  /**
   * Schedule page for publishing
   */
  async schedule(): Promise<void> {
    const id = this.pageId();
    if (!id) return;

    this.busy.set(true);
    try {
      const response = await firstValueFrom(
        this.apiService.schedulePage(id)
      );
      this.scheduleId.set(response.id);
      alert(`Page scheduled successfully! Schedule ID: ${response.id}`);
    } catch (error) {
      console.error('Scheduling failed:', error);
      alert('Failed to schedule page. Please try again.');
    } finally {
      this.busy.set(false);
    }
  }
}


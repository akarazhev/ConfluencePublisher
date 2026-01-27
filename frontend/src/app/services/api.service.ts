import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// TypeScript Interfaces
export interface Attachment {
  id: number;
  filename: string;
  description?: string;
}

export interface Schedule {
  id: number;
  pageId: number;
  status: string;
  scheduledAt: string;
  attemptCount: number;
  lastError?: string;
}

export interface ContentImprovementResponse {
  suggestions: string[];
}

export interface AttachmentDescriptionResponse {
  description: string;
}

export interface PageResponse {
  id: number;
  title: string;
  content: string;
  spaceKey: string;
  parentPageId?: number;
  attachments?: Attachment[];
}

export interface PublishResponse {
  logId?: number;
  status: string;
  confluencePageId?: string;
}

export interface ConfigResponse {
  defaultSpace: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private apiBase = environment.apiBase;

  /**
   * Helper method to construct full API URL
   * @param path API endpoint path (e.g., '/pages', '/attachments')
   * @returns Full URL: `${apiBase}/api${path}`
   */
  private api(path: string): string {
    return `${this.apiBase}/api${path}`;
  }

  /**
   * Upload an attachment file
   * @param file The file to upload
   * @param description Optional description for the attachment
   * @returns Observable of the uploaded attachment
   */
  uploadAttachment(file: File, description?: string): Observable<Attachment> {
    const formData = new FormData();
    formData.append('file', file);
    if (description) {
      formData.append('description', description);
    }
    return this.http.post<Attachment>(this.api('/attachments'), formData);
  }

  /**
   * Get AI suggestions to improve content
   * @param content The content to improve
   * @returns Observable of content improvement suggestions
   */
  improveContent(content: string): Observable<ContentImprovementResponse> {
    return this.http.post<ContentImprovementResponse>(
      this.api('/ai/improve-content'),
      { content }
    );
  }

  /**
   * Create a new page
   * @param title Page title
   * @param content Page content
   * @param spaceKey Optional space key (backend uses default if not provided)
   * @param attachmentIds Optional array of attachment IDs
   * @param parentPageId Optional parent page ID
   * @returns Observable of the created page
   */
  createPage(
    title: string,
    content: string,
    spaceKey?: string,
    attachmentIds?: number[],
    parentPageId?: number
  ): Observable<PageResponse> {
    const body: Record<string, unknown> = {
      title,
      content,
      attachmentIds: attachmentIds || []
    };
    
    // Only include spaceKey if provided
    if (spaceKey) {
      body['spaceKey'] = spaceKey;
    }
    
    // Only include parentPageId if provided
    if (parentPageId !== undefined) {
      body['parentPageId'] = parentPageId;
    }
    
    return this.http.post<PageResponse>(this.api('/pages'), body);
  }

  /**
   * Publish a page to Confluence immediately
   * @param pageId The ID of the page to publish
   * @returns Observable of the publish response
   */
  publishNow(pageId: number): Observable<PublishResponse> {
    return this.http.post<PublishResponse>(
      this.api('/confluence/publish'),
      { pageId }
    );
  }

  /**
   * Schedule a page for publishing
   * @param pageId The ID of the page to schedule
   * @returns Observable of the created schedule
   */
  schedulePage(pageId: number): Observable<Schedule> {
    return this.http.post<Schedule>(
      this.api('/schedules'),
      { pageId }
    );
  }

  /**
   * Get all schedules
   * @returns Observable of all schedules
   */
  getSchedules(): Observable<Schedule[]> {
    return this.http.get<Schedule[]>(this.api('/schedules'));
  }

  /**
   * Get application configuration
   * @returns Observable of configuration including default space
   */
  getConfig(): Observable<ConfigResponse> {
    return this.http.get<ConfigResponse>(this.api('/config'));
  }

  /**
   * Generate a description for an attachment using AI
   * @param description Optional initial description to improve
   * @returns Observable of the generated description
   */
  generateDescription(description?: string): Observable<AttachmentDescriptionResponse> {
    const body: Record<string, unknown> = {};
    if (description) {
      body['description'] = description;
    }
    return this.http.post<AttachmentDescriptionResponse>(
      this.api('/ai/generate-description'),
      body
    );
  }
}

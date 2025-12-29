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
   */
  private api(path: string): string {
    return `${this.apiBase}/api${path}`;
  }

  /**
   * Upload an attachment with optional description
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
   * Get AI-powered content improvement suggestions
   */
  improveContent(content: string): Observable<ContentImprovementResponse> {
    return this.http.post<ContentImprovementResponse>(
      this.api('/ai/improve-content'),
      { content }
    );
  }

  /**
   * Create a new page with optional attachments and parent page
   */
  createPage(
    title: string,
    content: string,
    spaceKey: string,
    attachmentIds: number[],
    parentPageId?: number
  ): Observable<PageResponse> {
    const body: any = {
      title,
      content,
      spaceKey,
      attachmentIds
    };
    
    if (parentPageId !== undefined) {
      body.parentPageId = parentPageId;
    }
    
    return this.http.post<PageResponse>(this.api('/pages'), body);
  }

  /**
   * Publish a page to Confluence immediately
   */
  publishNow(pageId: number): Observable<PublishResponse> {
    return this.http.post<PublishResponse>(
      this.api('/confluence/publish'),
      { pageId }
    );
  }

  /**
   * Schedule a page for publishing
   */
  schedulePage(pageId: number): Observable<Schedule> {
    return this.http.post<Schedule>(
      this.api('/schedules'),
      { pageId }
    );
  }

  /**
   * Get all scheduled pages
   */
  getSchedules(): Observable<Schedule[]> {
    return this.http.get<Schedule[]>(this.api('/schedules'));
  }

  /**
   * Get application configuration
   */
  getConfig(): Observable<ConfigResponse> {
    return this.http.get<ConfigResponse>(this.api('/config'));
  }

  /**
   * Generate AI description for an attachment
   */
  generateDescription(description?: string): Observable<AttachmentDescriptionResponse> {
    return this.http.post<AttachmentDescriptionResponse>(
      this.api('/ai/generate-description'),
      { description }
    );
  }
}


import { ApplicationListItemDTO, ApplicationReviewDTO, RevisionComparisonDTO, FeedbackDTO, CommentTemplateDTO, RevisionDTO, AuditEntryDTO, NotificationDTO } from '../types'

const API_BASE = (import.meta.env.VITE_API_BASE_URL as string)

async function fetchJson<T>(url: string, opts?: RequestInit): Promise<T> {
  const res = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...opts })
  if (res.status === 404) throw new Error('NOT_FOUND')
  if (res.status === 409) throw new Error('CONFLICT')
  if (!res.ok) throw new Error('SERVER_ERROR')
  return await res.json() as T
}

export async function listApplications(): Promise<ApplicationListItemDTO[]> {
  return fetchJson<ApplicationListItemDTO[]>(`${API_BASE}/applications/list`)
}

export async function getApplicationReview(applicationId: string): Promise<ApplicationReviewDTO> {
  return fetchJson<ApplicationReviewDTO>(`${API_BASE}/applications/${applicationId}/review`)
}

export async function listRevisions(applicationId: string): Promise<RevisionDTO[]>{
  return fetchJson<RevisionDTO[]>(`${API_BASE}/applications/${applicationId}/revisions`)
}

export async function compareRevisions(applicationId: string, from:number, to:number): Promise<RevisionComparisonDTO>{
  return fetchJson<RevisionComparisonDTO>(`${API_BASE}/applications/${applicationId}/revisions/compare?from=${from}&to=${to}`)
}

export type CreateFeedbackRequest = { targetType: string; targetKey: string; comment: string; officerName?: string; revisionId?: string }
export type OperatorResubmissionField = { key: string; value: string }
export type OperatorResubmissionRequest = { operatorName?: string; fields: OperatorResubmissionField[] }

export async function createFeedback(applicationId: string, payload: CreateFeedbackRequest): Promise<FeedbackDTO>{
  const res = await fetch(`${API_BASE}/applications/${applicationId}/feedback`, { method: 'POST', body: JSON.stringify(payload), headers: { 'Content-Type': 'application/json' } })
  if (res.status === 404) throw new Error('NOT_FOUND')
  if (res.status === 409) throw new Error('CONFLICT')
  if (!res.ok) throw new Error('SERVER_ERROR')
  return res.json()
}

export async function requestInformation(applicationId: string, officerName?: string): Promise<ApplicationReviewDTO>{
  return fetchJson<ApplicationReviewDTO>(`${API_BASE}/applications/${applicationId}/request-information`, { method: 'POST', body: JSON.stringify({ officerName }) })
}

export async function submitOperatorResubmission(applicationId: string, payload: OperatorResubmissionRequest): Promise<ApplicationReviewDTO> {
  return fetchJson<ApplicationReviewDTO>(`${API_BASE}/applications/${applicationId}/operator-resubmission`, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function listCommentTemplates(): Promise<CommentTemplateDTO[]>{
  return fetchJson<CommentTemplateDTO[]>(`${API_BASE}/comment-templates`)
}

export async function resolveFeedback(feedbackId: string, officerName?: string): Promise<FeedbackDTO>{
  const res = await fetch(`${API_BASE}/feedback/${feedbackId}/resolve?officerName=${encodeURIComponent(officerName || '')}`, { method: 'PATCH' })
  if (res.status === 404) throw new Error('NOT_FOUND')
  if (res.status === 409) throw new Error('CONFLICT')
  if (!res.ok) throw new Error('SERVER_ERROR')
  return res.json()
}

export async function getAudit(applicationId: string): Promise<AuditEntryDTO[]>{
  return fetchJson<AuditEntryDTO[]>(`${API_BASE}/applications/${applicationId}/audit`)
}

export async function getNotifications(applicationId: string): Promise<NotificationDTO[]>{
  return fetchJson<NotificationDTO[]>(`${API_BASE}/applications/${applicationId}/notifications`)
}

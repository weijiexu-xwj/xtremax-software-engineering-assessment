export type UUID = string

export interface FieldDTO { key: string; value: string }
export interface AIVerificationDTO { id: UUID; passed: boolean; details?: string; checkedAt?: string }
export interface DocumentDTO { id: UUID; key: string; filename: string; aiResult?: AIVerificationDTO }
export interface RevisionDTO { id: UUID; revisionNumber: number; createdBy: string; createdAt?: string; fields: FieldDTO[]; documents: DocumentDTO[] }
export interface FeedbackDTO { id: UUID; targetType: string; targetKey: string; comment: string; status: string; resolvedBy?: string; resolvedAt?: string; createdAt?: string }
export interface ApplicationReviewDTO { id: UUID; referenceNumber: string; officerStatusLabel: string; operatorStatusLabel?: string; version?: number; latestRevision?: RevisionDTO; feedback: FeedbackDTO[]; auditEntries: AuditEntryDTO[]; notifications: NotificationDTO[] }
export interface CommentTemplateDTO { id: UUID; title: string; text: string }
export interface AuditEntryDTO { id: UUID; actor: string; action: string; details?: string; timestamp?: string }
export interface NotificationDTO { id: UUID; recipient: string; message: string; sentAt?: string }
export interface RevisionComparisonDTO { addedFields: ComparisonEntryDTO[]; removedFields: ComparisonEntryDTO[]; modifiedFields: ComparisonEntryDTO[]; addedDocuments: ComparisonEntryDTO[]; removedDocuments: ComparisonEntryDTO[]; modifiedDocuments: ComparisonEntryDTO[] }
export interface ComparisonEntryDTO { key: string; previousValue?: string | null; currentValue?: string | null }

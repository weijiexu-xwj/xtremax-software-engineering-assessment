export const OFFICER_TO_OPERATOR_STATUS: Record<string, string> = {
  'Application Received': 'Submitted',
  'Under Review': 'Under Review',
  'Pending Pre-Site Resubmission': 'Pending Pre-Site Resubmission',
  'Pre-Site Resubmitted': 'Pre-Site Resubmitted',
  'Site Visit Scheduled': 'Pending Site Visit',
  'Site Visit Done': 'Pending Post-Site Clarification',
  'Awaiting Post-Site Clarification': 'Pending Post-Site Clarification',
  'Pending Post-Site Resubmission': 'Pending Post-Site Resubmission',
  'Awaiting Post-Site Resubmission': 'Pending Post-Site Resubmission',
  'Post-Site Clarification Resubmitted': 'Post-Site Resubmitted',
  'Route to Approval': 'Pending Approval',
  'Pending Approval': 'Pending Approval',
  Approved: 'Approved',
  Rejected: 'Rejected',
}

export function getOperatorStatusLabel(officerStatusLabel?: string | null): string {
  if (!officerStatusLabel) return 'Unknown'
  return OFFICER_TO_OPERATOR_STATUS[officerStatusLabel] ?? officerStatusLabel
}

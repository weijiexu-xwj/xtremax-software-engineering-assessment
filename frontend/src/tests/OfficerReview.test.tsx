import { describe, it, expect } from 'vitest'
import { getOperatorStatusLabel } from '../statusLabels'
import { extractAppIdFromPath, getRoute } from '../App'
import { getRequestInformationErrorMessage } from '../pages/OfficerReview'

describe('OfficerReview', () => {
  it('keeps the officer route as the default view', () => {
    expect(getRoute('/')).toBe('officer')
    expect(getRoute('/officer')).toBe('officer')
  })

  it('does not remap officer labels away from the official status text', () => {
    expect(getOperatorStatusLabel('Under Review')).toBe('Under Review')
    expect(getOperatorStatusLabel('Approved')).toBe('Approved')
  })

  it('shows a user-friendly explanation when pre-site resubmission is rejected with a conflict', () => {
    expect(getRequestInformationErrorMessage('CONFLICT')).toBe('A pre-site resubmission can only be requested when the application is Under Review and has at least one open feedback item.')
  })

  it('keeps a fallback error for unexpected request-information failures', () => {
    expect(getRequestInformationErrorMessage('SERVER_ERROR')).toBe('Failed to request information')
    expect(getRequestInformationErrorMessage()).toBe('Failed to request information')
  })

  it('extracts application ID from officer and operator routes', () => {
    expect(extractAppIdFromPath('/officer/11111111-1111-1111-1111-111111111111')).toBe('11111111-1111-1111-1111-111111111111')
    expect(extractAppIdFromPath('/operator/22222222-2222-2222-2222-222222222222')).toBe('22222222-2222-2222-2222-222222222222')
  })

  it('returns empty string for invalid application routes', () => {
    expect(extractAppIdFromPath('/')).toBe('')
    expect(extractAppIdFromPath('/officer')).toBe('')
    expect(extractAppIdFromPath('/invalid/path')).toBe('')
  })

  it('handles UUID-shaped application routes consistently', () => {
    const uuid1 = 'a0000000-0000-0000-0000-000000000000'
    const uuid2 = 'ffffffff-ffff-ffff-ffff-ffffffffffff'

    expect(extractAppIdFromPath(`/officer/${uuid1}`)).toBe(uuid1)
    expect(extractAppIdFromPath(`/operator/${uuid2}`)).toBe(uuid2)
  })
})

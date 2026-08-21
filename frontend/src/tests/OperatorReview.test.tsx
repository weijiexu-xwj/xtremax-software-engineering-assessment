import { describe, expect, it } from 'vitest'
import { getRoute } from '../App'
import { getOperatorStatusLabel } from '../statusLabels'

describe('OperatorReview', () => {
  it('maps officer status labels to operator labels', () => {
    expect(getOperatorStatusLabel('Site Visit Scheduled')).toBe('Pending Site Visit')
    expect(getOperatorStatusLabel('Application Received')).toBe('Submitted')
    expect(getOperatorStatusLabel('Route to Approval')).toBe('Pending Approval')
  })

  it('renders the operator route and keeps the officer view as default fallback', () => {
    expect(getRoute('/operator')).toBe('operator')
    expect(getRoute('/officer')).toBe('officer')
    expect(getRoute('/')).toBe('officer')
  })
})

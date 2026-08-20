import { describe, it, expect } from 'vitest'
import { getOperatorStatusLabel } from '../statusLabels'
import { getRoute } from '../App'

describe('OfficerReview', () => {
  it('keeps the officer route as the default view', () => {
    expect(getRoute('/')).toBe('officer')
    expect(getRoute('/officer')).toBe('officer')
  })

  it('does not remap officer labels away from the official status text', () => {
    expect(getOperatorStatusLabel('Under Review')).toBe('Under Review')
    expect(getOperatorStatusLabel('Approved')).toBe('Approved')
  })
})

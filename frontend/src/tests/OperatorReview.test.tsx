import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import { getRoute } from '../App'
import * as api from '../api/client'
import OperatorReview from '../pages/OperatorReview'
import { getOperatorStatusLabel } from '../statusLabels'

vi.mock('../api/client', async () => {
  const actual = await vi.importActual<typeof import('../api/client')>('../api/client')
  return {
    ...actual,
    getApplicationReview: vi.fn(),
    submitOperatorResubmission: vi.fn(),
  }
})

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

  it('submits operator resubmission and shows success', async () => {
    vi.mocked(api.getApplicationReview).mockResolvedValue({
      id: 'app-1',
      referenceNumber: 'APP-001',
      officerStatusLabel: 'Pending Pre-Site Resubmission',
      operatorStatusLabel: 'Pending Pre-Site Resubmission',
      latestRevision: {
        id: 'rev-1',
        revisionNumber: 1,
        createdBy: 'operator',
        fields: [{ key: 'Business Name', value: 'Original Ltd' }],
        documents: [],
      },
      feedback: [{ id: 'fb-1', targetType: 'FIELD', targetKey: 'Business Name', comment: 'Check the business name.', status: 'OPEN' }],
      auditEntries: [],
      notifications: [],
    } as any)
    vi.mocked(api.submitOperatorResubmission).mockResolvedValue({
      id: 'app-1',
      referenceNumber: 'APP-001',
      officerStatusLabel: 'Pre-Site Resubmitted',
      operatorStatusLabel: 'Pre-Site Resubmitted',
      latestRevision: {
        id: 'rev-2',
        revisionNumber: 2,
        createdBy: 'operator',
        fields: [{ key: 'Business Name', value: 'Updated Ltd' }],
        documents: [],
      },
      feedback: [],
      auditEntries: [],
      notifications: [{ id: 'n-1', recipient: 'officer', message: 'Operator resubmitted revision 2' }],
    } as any)

    render(<OperatorReview appId="app-1" />)

    expect(await screen.findByRole('button', { name: /Submit corrected information/i })).toBeInTheDocument()

    fireEvent.change(screen.getByLabelText('Business Name'), { target: { value: 'Updated Ltd' } })
    fireEvent.click(screen.getByRole('button', { name: /Submit corrected information/i }))

    await waitFor(() => {
      expect(api.submitOperatorResubmission).toHaveBeenCalledWith('app-1', {
        operatorName: 'operator',
        fields: [{ key: 'Business Name', value: 'Updated Ltd' }],
      })
    })
    expect(await screen.findByText(/Operator resubmitted revision 2/i)).toBeInTheDocument()
    expect(screen.getByText('Pre-Site Resubmitted')).toBeInTheDocument()
  })
})

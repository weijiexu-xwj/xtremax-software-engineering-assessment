import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { vi } from 'vitest'
import OfficerReview from '../pages/OfficerReview'
import * as api from '../api/client'

const mockApp = {
  id:'1', referenceNumber:'REF-1', officerStatusLabel:'Under Review', latestRevision:{ id:'r1', revisionNumber:1, createdBy:'op', fields:[{key:'name',value:'Alice'}], documents:[] }, feedback:[], auditEntries:[], notifications:[] }

vi.mock('../api/client')

describe('OfficerReview', ()=>{
  beforeEach(()=>{
    (api.getApplicationReview as any).mockResolvedValue(mockApp)
    (api.listCommentTemplates as any).mockResolvedValue([])
    (api.listRevisions as any).mockResolvedValue([{id:'r1',revisionNumber:1,createdBy:'op',fields:[],documents:[]}])
  })

  it('loads application', async ()=>{
    render(<OfficerReview />)
    await waitFor(()=> expect(screen.getByText(/Application REF-1/)).toBeInTheDocument())
  })

  it('displays form data', async ()=>{
    render(<OfficerReview />)
    await waitFor(()=> expect(screen.getByText('Alice')).toBeInTheDocument())
  })

  it('creates feedback', async ()=>{
    (api.createFeedback as any).mockResolvedValue({id:'f1', targetType:'FIELD', targetKey:'name', comment:'Please', status:'OPEN'})
    render(<OfficerReview />)
    await waitFor(()=> expect(screen.getByText('Alice')).toBeInTheDocument())
    fireEvent.click(screen.getByText('Add feedback'))
    fireEvent.change(screen.getByLabelText('feedback comment'), { target: { value: 'Please' } })
    fireEvent.click(screen.getByText('Submit'))
    await waitFor(()=> expect((api.createFeedback as any)).toHaveBeenCalled())
  })
})

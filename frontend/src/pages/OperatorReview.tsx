import React, { useEffect, useState } from 'react'
import { getApplicationReview } from '../api/client'
import { ApplicationReviewDTO } from '../types'
import { getOperatorStatusLabel } from '../statusLabels'

function Loading(){ return <div>Loading…</div> }
function ErrorMessage({message}:{message:string}){ return <div role="alert" style={{color:'crimson'}}>{message}</div> }

const ACTION_REQUIRED_STATUSES = new Set([
  'Pending Pre-Site Resubmission',
  'Pending Site Visit',
  'Pending Post-Site Clarification',
  'Pending Post-Site Resubmission',
  'Pending Approval',
])

export default function OperatorReview(){
  const [appId] = useState<string>(() => '11111111-1111-1111-1111-111111111111')
  const [data, setData] = useState<ApplicationReviewDTO | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(()=>{ void load() }, [])

  async function load(){
    setLoading(true); setError(null)
    try {
      const r = await getApplicationReview(appId)
      setData({ ...r, operatorStatusLabel: r.operatorStatusLabel ?? getOperatorStatusLabel(r.officerStatusLabel) })
    } catch (e: any) {
      if (e?.message === 'NOT_FOUND') {
        setError('Application not found')
      } else if (e?.message === 'CONFLICT') {
        setError('Conflict: application changed, refresh')
      } else {
        setError('Server error')
      }
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <Loading />
  if (error) return <ErrorMessage message={error} />
  if (!data) return <div>No application loaded</div>

  const operatorStatus = data.operatorStatusLabel ?? getOperatorStatusLabel(data.officerStatusLabel)
  const showActionAlert = ACTION_REQUIRED_STATUSES.has(operatorStatus)

  return (
    <section>
      <h2>Operator Application Status</h2>
      <div>Application: <strong>{data.referenceNumber}</strong></div>
      <div>Status: <strong>{operatorStatus}</strong></div>
      <div role="status" style={{
        marginTop: 12,
        padding: 10,
        border: '1px solid #d9d9d9',
        borderRadius: 6,
        background: showActionAlert ? '#fff7e6' : '#f6f8fa',
      }}>
        {showActionAlert ? 'Action required for this application.' : 'No operator action required at this time.'}
      </div>

      <div style={{ display: 'flex', gap: 20, marginTop: 16 }}>
        <div style={{ flex: 2 }}>
          <h3>Application summary</h3>
          {data.latestRevision?.fields.map(f => (
            <div key={f.key} style={{ borderBottom: '1px solid #eee', padding: 6 }}>
              <label><strong>{f.key}</strong></label>
              <div>{f.value}</div>
            </div>
          ))}

          <h3>Documents</h3>
          {data.latestRevision?.documents.length ? data.latestRevision.documents.map(d => (
            <div key={d.id} style={{ borderBottom: '1px solid #eee', padding: 6 }}>
              <div><strong>{d.key}</strong> — {d.filename}</div>
              {d.aiResult && !d.aiResult.passed && <div style={{ color: 'orange' }}>AI flagged: {d.aiResult.details}</div>}
            </div>
          )) : <div>No documents attached.</div>}

          <h3>Audit history</h3>
          {data.auditEntries.length ? data.auditEntries.map((e: any) => (
            <div key={e.id}><strong>{e.actor}</strong> {e.action} — {e.details}</div>
          )) : <div>No audit entries.</div>}
        </div>

        <aside style={{ flex: 1 }}>
          <h3>Notifications</h3>
          {data.notifications.length ? data.notifications.map(n => (
            <div key={n.id} style={{ border: '1px solid #eee', padding: 8, marginBottom: 8 }}>
              {n.message}
            </div>
          )) : <div>No notifications.</div>}

          <h3>Officer feedback</h3>
          {data.feedback.length ? data.feedback.map(f => (
            <div key={f.id} style={{ border: '1px solid #ddd', padding: 8, marginBottom: 8 }}>
              <div><strong>{f.targetKey}</strong> ({f.targetType})</div>
              <div>{f.comment}</div>
              <div style={{ color: '#666' }}>{f.status}</div>
            </div>
          )) : <div>No feedback available.</div>}
        </aside>
      </div>
    </section>
  )
}

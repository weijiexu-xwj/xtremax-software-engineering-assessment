import React, { useEffect, useState } from 'react'
import { getApplicationReview, listCommentTemplates, createFeedback, requestInformation, listRevisions, compareRevisions, resolveFeedback, getAudit, getNotifications } from '../api/client'
import { ApplicationReviewDTO, RevisionDTO, FeedbackDTO, CommentTemplateDTO, RevisionComparisonDTO } from '../types'

function Loading(){ return <div>Loading…</div> }
function ErrorMessage({message}:{message:string}){ return <div role="alert" style={{color:'crimson'}}>{message}</div> }

export default function OfficerReview({ appId }: { appId: string }){
  const [data, setData] = useState<ApplicationReviewDTO | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [templates, setTemplates] = useState<CommentTemplateDTO[]>([])
  const [revisions, setRevisions] = useState<RevisionDTO[]>([])
  const [compareResult, setCompareResult] = useState<RevisionComparisonDTO | null>(null)

  useEffect(()=>{ load() ; loadTemplates(); loadRevisions() }, [appId])

  async function load(){
    setLoading(true); setError(null)
    try{
      const r = await getApplicationReview(appId)
      setData(r)
    }catch(e:any){
      if (e?.message === 'NOT_FOUND') {
        setError('Application not found')
      } else if (e?.message === 'CONFLICT') {
        setError('Conflict: application changed, refresh')
      } else {
        setError('Server error')
      }
    }finally{ setLoading(false) }
  }

  async function loadTemplates(){
    try{ setTemplates(await listCommentTemplates()) }catch(e){ /* ignore */ }
  }

  async function loadRevisions(){
    try{ setRevisions(await listRevisions(appId)) }catch(e){ }
  }

  async function handleCreateFeedback(targetType:string, targetKey:string, comment:string, revisionId?:string){
    try{
      await createFeedback(appId, { targetType, targetKey, comment, revisionId })
      await load()
    }catch(e:any){
      if (e?.message === 'CONFLICT') {
        setError('Conflict: application changed, please refresh')
      } else {
        setError('Failed to create feedback')
      }
    }
  }

  async function handleRequestInfo(){
    try{
      await requestInformation(appId, 'officer')
      await load()
    }catch(e:any){
      if (e?.message === 'CONFLICT') {
        setError('Conflict: cannot request resubmission')
      } else {
        setError('Failed to request information')
      }
    }
  }

  async function handleCompare(from:number, to:number){
    try{
      setCompareResult(await compareRevisions(appId, from, to))
    }catch(e:any){
      setError('Failed to compare')
    }
  }

  async function handleResolve(feedbackId:string){
    try{
      await resolveFeedback(feedbackId,'officer')
      await load()
    }catch(e:any){
      if (e?.message === 'CONFLICT') {
        setError('Conflict resolving feedback')
      } else {
        setError('Failed to resolve')
      }
    }
  }

  if (loading) return <Loading />
  if (error) return <ErrorMessage message={error} />
  if (!data) return <div>No application loaded</div>

  return (
    <section>
      <h2>Application {data.referenceNumber}</h2>
      <div>Status: <strong>{data.officerStatusLabel}</strong></div>
      <div style={{display:'flex',gap:20,marginTop:12}}>
        <div style={{flex:2}}>
          <h3>Form data (Revision {data.latestRevision?.revisionNumber ?? '—'})</h3>
          {data.latestRevision?.fields.map(f => (
            <div key={f.key} style={{borderBottom:'1px solid #eee',padding:6}}>
              <label><strong>{f.key}</strong></label>
              <div>{f.value}</div>
              <FeedbackInline keyName={f.key} onCreate={handleCreateFeedback} templates={templates} revisionId={data.latestRevision?.id}/>
            </div>
          ))}

          <h3>Documents</h3>
          {data.latestRevision?.documents.map(d => (
            <div key={d.id} style={{borderBottom:'1px solid #eee',padding:6}}>
              <div><strong>{d.key}</strong> — {d.filename}</div>
              {d.aiResult && !d.aiResult.passed && <div style={{color:'orange'}}>AI flagged: {d.aiResult.details}</div>}
              <FeedbackInline keyName={d.key} targetType="DOCUMENT" onCreate={handleCreateFeedback} templates={templates} revisionId={data.latestRevision?.id} />
            </div>
          ))}

          <h3>Request information</h3>
          <button onClick={handleRequestInfo}>Request pre-site resubmission</button>

          <h3>Revisions</h3>
          <RevisionSelector revisions={revisions} onCompare={handleCompare} />
          {compareResult && <RevisionComparison result={compareResult} />}

          <h3>Audit history</h3>
          <AuditHistory entries={data.auditEntries} />
        </div>
        <aside style={{flex:1}}>
          <h3>Notifications</h3>
          <Notifications items={data.notifications} />

          <h3>Feedback</h3>
          <div>
            <h4>Open</h4>
            {data.feedback.filter(f => f.status === 'OPEN').map(f=> (
              <div key={f.id} style={{border:'1px solid #ddd',padding:8,marginBottom:8}}>
                <div><strong>{f.targetKey}</strong> ({f.targetType})</div>
                <div>{f.comment}</div>
                <div><button onClick={()=>handleResolve(f.id)}>Resolve</button></div>
              </div>
            ))}
            <h4>Resolved</h4>
            {data.feedback.filter(f => f.status !== 'OPEN').map(f=> (
              <div key={f.id} style={{border:'1px solid #eee',padding:8,marginBottom:8,opacity:0.7}}>
                <div><strong>{f.targetKey}</strong> — {f.status}</div>
                <div>{f.comment}</div>
              </div>
            ))}
          </div>
        </aside>
      </div>
    </section>
  )
}

function FeedbackInline({keyName,onCreate,templates, targetType='FIELD', revisionId}:{keyName:string, onCreate:(t:string,k:string,c:string,r?:string)=>void, templates:CommentTemplateDTO[], targetType?:string, revisionId?:string}){
  const [open,setOpen] = React.useState(false)
  const [comment,setComment] = React.useState('')
  const [selectedTemplate,setSelectedTemplate] = React.useState<string>('')
  return (
    <div>
      {!open && <button onClick={()=>setOpen(true)}>Add feedback</button>}
      {open && (
        <div>
          <label>Template
            <select value={selectedTemplate} onChange={e=>{ setSelectedTemplate(e.target.value); const t=templates.find(x=>x.id===e.target.value); if(t) setComment(t.text) }}>
              <option value="">(none)</option>
              {templates.map(t=> <option value={t.id} key={t.id}>{t.title}</option>)}
            </select>
          </label>
          <label>Comment
            <textarea value={comment} onChange={e=>setComment(e.target.value)} aria-label="feedback comment" />
          </label>
          <div>
            <button onClick={()=>{ onCreate(targetType, keyName, comment, revisionId); setOpen(false); setComment('') }}>Submit</button>
            <button onClick={()=>setOpen(false)}>Cancel</button>
          </div>
        </div>
      )}
    </div>
  )
}

function RevisionSelector({revisions,onCompare}:{revisions:RevisionDTO[], onCompare:(from:number,to:number)=>void}){
  const [from,setFrom]=React.useState<number>(1)
  const [to,setTo]=React.useState<number>(1)
  return (
    <div>
      <label>From
        <select value={from} onChange={e=>setFrom(Number(e.target.value))}>
          {revisions.map(r=> <option key={r.id} value={r.revisionNumber}>{r.revisionNumber}</option>)}
        </select>
      </label>
      <label>To
        <select value={to} onChange={e=>setTo(Number(e.target.value))}>
          {revisions.map(r=> <option key={r.id} value={r.revisionNumber}>{r.revisionNumber}</option>)}
        </select>
      </label>
      <button onClick={()=>onCompare(from,to)}>Compare</button>
    </div>
  )
}

function RevisionComparison({result}:{result:RevisionComparisonDTO}){
  return (
    <div>
      <h4>Modified Fields</h4>
      {result.modifiedFields.map(m => <div key={m.key}>{m.key}: {m.previousValue} → {m.currentValue}</div>)}
      <h4>Modified Documents</h4>
      {result.modifiedDocuments.map(m => <div key={m.key}>{m.key}: {m.previousValue} → {m.currentValue}</div>)}
    </div>
  )
}

function AuditHistory({entries}:{entries: any[]}){
  return (<div>{entries.map((e:any)=> <div key={e.id}><strong>{e.actor}</strong> {e.action} — {e.details}</div>)}</div>)
}

function Notifications({items}:{items:any[]}){
  return (<div>{items.map(i=> <div key={i.id}>{i.message}</div>)}</div>)
}
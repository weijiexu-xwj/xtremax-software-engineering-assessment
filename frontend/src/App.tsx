import React, { useEffect, useMemo, useState } from 'react'
import OfficerReview from './pages/OfficerReview'
import OperatorReview from './pages/OperatorReview'
import ApplicationSelector from './components/ApplicationSelector'

export function getRoute(pathname = window.location.pathname): 'officer' | 'operator' {
  if (pathname.startsWith('/operator')) return 'operator'
  return 'officer'
}

export function extractAppIdFromPath(pathname = window.location.pathname): string {
  const match = pathname.match(/\/(officer|operator)\/([a-f0-9-]+)/)
  return match?.[2] || ''
}

export default function App(){
  const [route, setRoute] = useState<'officer' | 'operator'>(() => getRoute())
  const [appId, setAppId] = useState<string>(() => extractAppIdFromPath())

  useEffect(() => {
    const handler = () => {
      setRoute(getRoute())
      setAppId(extractAppIdFromPath())
    }
    window.addEventListener('popstate', handler)
    return () => window.removeEventListener('popstate', handler)
  }, [])

  const pageTitle = useMemo(() => route === 'operator' ? 'Operator Application View' : 'Officer Application Review', [route])

  const navigate = (next: 'officer' | 'operator', selectedAppId?: string) => {
    const id = selectedAppId || appId
    if (!id) return
    const path = next === 'operator' ? `/operator/${id}` : `/officer/${id}`
    window.history.pushState({}, '', path)
    setRoute(next)
    setAppId(id)
  }

  const handleSelectApplication = (selectedAppId: string) => {
    setAppId(selectedAppId)
    const path = `/${route}/${selectedAppId}`
    window.history.pushState({}, '', path)
  }

  const appIdExists = appId && appId.trim().length > 0

  return (
    <div>
      <header style={{padding:12,borderBottom:'1px solid #ddd'}}>
        <h1>{pageTitle}</h1>
        <ApplicationSelector onSelect={handleSelectApplication} />
        <nav style={{marginTop:10, display:'flex', gap:8}}>
          <button type="button" onClick={() => navigate('officer')}>Officer</button>
          <button type="button" onClick={() => navigate('operator')}>Operator</button>
        </nav>
      </header>
      <main style={{padding:12}}>
        {appIdExists && (route === 'operator' ? <OperatorReview appId={appId} /> : <OfficerReview appId={appId} />)}
        {!appIdExists && <div>Loading application…</div>}
      </main>
    </div>
  )
}

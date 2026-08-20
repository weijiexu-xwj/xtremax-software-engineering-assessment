import React, { useEffect, useMemo, useState } from 'react'
import OfficerReview from './pages/OfficerReview'
import OperatorReview from './pages/OperatorReview'

export function getRoute(pathname = window.location.pathname): 'officer' | 'operator' {
  if (pathname.startsWith('/operator')) return 'operator'
  return 'officer'
}

export default function App(){
  const [route, setRoute] = useState<'officer' | 'operator'>(() => getRoute())

  useEffect(() => {
    const handler = () => setRoute(getRoute())
    window.addEventListener('popstate', handler)
    return () => window.removeEventListener('popstate', handler)
  }, [])

  const pageTitle = useMemo(() => route === 'operator' ? 'Operator Application View' : 'Officer Application Review', [route])

  const navigate = (next: 'officer' | 'operator') => {
    const path = next === 'operator' ? '/operator' : '/officer'
    window.history.pushState({}, '', path)
    setRoute(next)
  }

  return (
    <div>
      <header style={{padding:12,borderBottom:'1px solid #ddd'}}>
        <h1>{pageTitle}</h1>
        <nav style={{marginTop:10, display:'flex', gap:8}}>
          <button type="button" onClick={() => navigate('officer')}>Officer</button>
          <button type="button" onClick={() => navigate('operator')}>Operator</button>
        </nav>
      </header>
      <main style={{padding:12}}>
        {route === 'operator' ? <OperatorReview /> : <OfficerReview />}
      </main>
    </div>
  )
}

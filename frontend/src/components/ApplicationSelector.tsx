import React, { useEffect, useState } from 'react'
import { listApplications } from '../api/client'
import { ApplicationListItemDTO } from '../types'

function Loading() { return <div>Loading applications…</div> }
function ErrorMessage({ message }: { message: string }) { return <div role="alert" style={{color:'crimson'}}>{message}</div> }
function EmptyState() { return <div>No applications available</div> }

interface ApplicationSelectorProps {
  onSelect: (appId: string) => void
}

export default function ApplicationSelector({ onSelect }: ApplicationSelectorProps) {
  const [apps, setApps] = useState<ApplicationListItemDTO[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    void load()
  }, [])

  async function load() {
    setLoading(true)
    setError(null)
    try {
      const result = await listApplications()
      setApps(result)
      if (result.length > 0) {
        onSelect(result[0].id)
      }
    } catch (e: any) {
      setError('Failed to load applications')
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <Loading />
  if (error) return <ErrorMessage message={error} />
  if (apps.length === 0) return <EmptyState />

  return (
    <div style={{marginBottom: 12}}>
      <label htmlFor="app-selector" style={{marginRight: 8}}>Select Application: </label>
      <select id="app-selector" onChange={(e) => onSelect(e.target.value)} defaultValue={apps[0].id}>
        {apps.map(app => (
          <option key={app.id} value={app.id}>
            {app.referenceNumber} ({app.id.substring(0, 8)})
          </option>
        ))}
      </select>
    </div>
  )
}

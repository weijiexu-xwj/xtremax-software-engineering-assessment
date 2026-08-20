import React from 'react'
import { DocumentDTO } from '../types'

export default function DocumentView({doc}:{doc:DocumentDTO}){
  return (
    <div>
      <div><strong>{doc.key}</strong></div>
      <div>{doc.filename}</div>
      {doc.aiResult && (
        <div>
          <div>AI check: {doc.aiResult.passed ? 'Passed' : 'Issue'}</div>
          {doc.aiResult.details && <div>{doc.aiResult.details}</div>}
        </div>
      )}
    </div>
  )
}

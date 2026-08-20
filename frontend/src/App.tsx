import React from 'react'
import OfficerReview from './pages/OfficerReview'

export default function App(){
  return (
    <div>
      <header style={{padding:12,borderBottom:'1px solid #ddd'}}>
        <h1>Officer Application Review</h1>
      </header>
      <main style={{padding:12}}>
        <OfficerReview />
      </main>
    </div>
  )
}

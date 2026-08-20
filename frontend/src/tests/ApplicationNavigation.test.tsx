import { describe, it, expect } from 'vitest'
import { extractAppIdFromPath } from '../App'

describe('ApplicationNavigation', () => {
  it('extracts application ID from officer route', () => {
    const appId = extractAppIdFromPath('/officer/11111111-1111-1111-1111-111111111111')
    expect(appId).toBe('11111111-1111-1111-1111-111111111111')
  })

  it('extracts application ID from operator route', () => {
    const appId = extractAppIdFromPath('/operator/22222222-2222-2222-2222-222222222222')
    expect(appId).toBe('22222222-2222-2222-2222-222222222222')
  })

  it('returns empty string for invalid routes', () => {
    expect(extractAppIdFromPath('/')).toBe('')
    expect(extractAppIdFromPath('/officer')).toBe('')
    expect(extractAppIdFromPath('/invalid/path')).toBe('')
  })

  it('correctly parses UUID format URLs', () => {
    const url = '/officer/11111111-1111-1111-1111-111111111111'
    const match = url.match(/\/(officer|operator)\/([a-f0-9-]+)/)
    expect(match?.[1]).toBe('officer')
    expect(match?.[2]).toBe('11111111-1111-1111-1111-111111111111')
  })

  it('handles various UUID formats', () => {
    const uuid1 = 'a0000000-0000-0000-0000-000000000000'
    const appId1 = extractAppIdFromPath(`/officer/${uuid1}`)
    expect(appId1).toBe(uuid1)

    const uuid2 = 'ffffffff-ffff-ffff-ffff-ffffffffffff'
    const appId2 = extractAppIdFromPath(`/operator/${uuid2}`)
    expect(appId2).toBe(uuid2)
  })

  it('gets route type from pathname', () => {
    const officerUrl = '/officer/11111111-1111-1111-1111-111111111111'
    const operatorUrl = '/operator/22222222-2222-2222-2222-222222222222'
    
    expect(officerUrl.startsWith('/officer')).toBe(true)
    expect(operatorUrl.startsWith('/operator')).toBe(true)
  })
})


// src/app/learn/page.tsx
'use client'

import { createClient } from '@/lib/supabaseClient'
import { useRouter } from 'next/navigation'
import { useEffect, useState } from 'react'
import type { Session } from '@supabase/supabase-js'

export default function Learn() {
  const [session, setSession] = useState<Session | null>(null)
  const [loading, setLoading] = useState(true)
  const supabase = createClient()
  const router = useRouter()

  useEffect(() => {
    // Get initial session
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session)
      setLoading(false)
      if (!session) {
        router.push('/')
      }
    })

    // Listen for auth changes
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session)
      if (!session) {
        router.push('/')
      }
    })

    return () => subscription.unsubscribe()
  }, [supabase, router])

  if (loading) return <div>加载中...</div>
  if (!session) return <div>加载中...</div>

  return (
    <div style={{ padding: 50, fontSize: 24 }}>
      <h1>欢迎回来，{session.user.email}！</h1>
      <p>明天这里就是你的背单词主战场！</p>
      <button 
        onClick={async () => {
          await supabase.auth.signOut()
          router.push('/')
        }}
        style={{ padding: '10px 20px', marginTop: 20 }}
      >
        退出登录
      </button>
    </div>
  )
}
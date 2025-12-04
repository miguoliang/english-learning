// src/app/operator/page.tsx   ← 完整终极版
'use client'

import { useEffect, useState } from 'react'
import { createClient } from '@/lib/supabaseClient'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { User } from '@supabase/supabase-js'

export default function OperatorDashboard() {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)   // ← 加一个 loading 状态
  const router = useRouter()
  const supabase = createClient()

  useEffect(() => {
    const checkOperator = async () => {
      const { data, error } = await supabase.auth.getUser()

      // 关键：加 error 判断 + 再次确认 session
      if (error || !data?.user) {
        router.replace('/learn')
        return
      }

      if (data.user.user_metadata?.role !== 'operator') {
        router.replace('/learn')
        return
      }

      setUser(data.user)
      setLoading(false)
    }

    checkOperator()

    // 监听登录状态变化（防止刷新后 user 丢失）
    const { data: listener } = supabase.auth.onAuthStateChange((event, session) => {
      if (event === 'SIGNED_IN' || event === 'TOKEN_REFRESHED') {
        if (session?.user?.user_metadata?.role === 'operator') {
          setUser(session.user)
          setLoading(false)
        } else {
          router.replace('/learn')
        }
      }
      if (event === 'SIGNED_OUT') {
        router.replace('/')
      }
    })

    return () => listener.subscription.unsubscribe()
  }, [router, supabase])

  // 加载中或无权限
  if (loading || !user) {
    return (
      <div className="min-h-screen bg-linear-to-br from-purple-600 to-indigo-700 flex items-center justify-center">
        <div className="text-white text-3xl font-medium">校验权限中…</div>
      </div>
    )
  }

  const handleSignOut = async () => {
    await supabase.auth.signOut()
    router.push('/')
  }

  return (
    <div className="min-h-screen bg-linear-to-br from-gray-900 via-gray-800 to-blue-950">
      <div className="min-h-screen bg-black/30">
        {/* 顶部栏 */}
        <div className="flex justify-between items-center p-8">
          <h1 className="text-5xl font-bold text-white">运营后台</h1>
          <div className="flex items-center gap-8">
            <div className="text-white/90 text-lg">
              {user.email} · <span className="text-cyan-400 font-bold">operator</span>
            </div>
            <button onClick={handleSignOut} className="px-8 py-3 bg-white/20 hover:bg-white/30 text-white rounded-xl transition backdrop-blur">
              退出登录
            </button>
          </div>
        </div>
  
        {/* 卡片区（改个小颜色更搭） */}
        <div className="max-w-6xl mx-auto px-8 pb-20">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
            <Link href="/import" className="group bg-white/10 backdrop-blur-lg border border-white/20 rounded-3xl p-12 text-center hover:scale-105 hover:bg-white/20 transition-all duration-300 shadow-2xl">
              <div className="text-9xl mb-6 group-hover:animate-bounce">Upload</div>
              <h2 className="text-4xl font-bold text-white mb-4">导入词库</h2>
              <p className="text-cyan-300 text-lg">四六级 · 考研 · 高考 · 雅思</p>
            </Link>
            {/* 其他卡片同理，保持 bg-white/10 + border-white/20 */}
          </div>
        </div>
      </div>
    </div>
  )
}
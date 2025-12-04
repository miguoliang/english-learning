// src/app/page.tsx - Sign In Page
'use client'

import { useState } from 'react'
import { createClient } from '@/lib/supabaseClient'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

export default function SignIn() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const supabase = createClient()
  const router = useRouter()

  const handleLogin = async () => {
    setLoading(true)
    const { error } = await supabase.auth.signInWithPassword({ email, password })
    if (error) alert(error.message)
    else router.push('/learn')
    setLoading(false)
  }

  return (
    <div className="max-w-md md:max-w-lg lg:max-w-xl w-full mx-auto p-5 md:p-8 lg:p-10 min-h-screen flex flex-col justify-center text-center box-border">
      <h1 className="text-5xl font-bold mb-6">背它一辈子</h1>
      <h2 className="mb-6 md:mb-8 lg:mb-10 text-gray-600 text-lg sm:text-xl md:text-2xl lg:text-3xl">登录</h2>
      <input
        type="email"
        placeholder="邮箱"
        value={email}
        onChange={e => setEmail(e.target.value)}
        className="w-full py-3.5 md:py-4 lg:py-5 px-4 md:px-5 my-2.5 md:my-3 rounded-lg border border-gray-300 text-base md:text-lg box-border appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
      />
      <input
        type="password"
        placeholder="密码"
        value={password}
        onChange={e => setPassword(e.target.value)}
        className="w-full py-3.5 md:py-4 lg:py-5 px-4 md:px-5 my-2.5 md:my-3 rounded-lg border border-gray-300 text-base md:text-lg box-border appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
      />
      <div className="my-6 md:my-8">
        <button 
          onClick={handleLogin} 
          disabled={loading} 
          className={`w-full py-3.5 md:py-4 lg:py-5 px-6 md:px-8 bg-blue-600 text-white border-none rounded-lg text-base md:text-lg font-semibold min-h-[48px] md:min-h-[52px] touch-manipulation transition-colors ${
            loading ? 'cursor-not-allowed opacity-70' : 'cursor-pointer hover:bg-blue-700 active:bg-blue-800'
          }`}
        >
          {loading ? '登录中...' : '登录'}
        </button>
      </div>
      <div className="mt-5 md:mt-6 text-gray-600 text-sm md:text-base">
        还没有账号？{' '}
        <Link href="/signup" className="text-blue-600 no-underline font-medium hover:underline">
          立即注册
        </Link>
      </div>
    </div>
  )
}

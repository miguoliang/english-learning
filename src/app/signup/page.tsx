// src/app/signup/page.tsx - Sign Up Page
'use client'

import { useState } from 'react'
import { createClient } from '@/lib/supabaseClient'
import { useRouter } from 'next/navigation'
import Link from 'next/link'

export default function SignUp() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const supabase = createClient()
  const router = useRouter()

  const handleSignup = async () => {
    setLoading(true)
    const { error } = await supabase.auth.signUp({ email, password })
    if (error) {
      alert(error.message)
    } else {
      alert('注册成功！请查收邮件激活（开发环境会直接登录）')
      router.push('/learn')
    }
    setLoading(false)
  }

  return (
    <div className="min-h-screen bg-white dark:bg-gray-900 flex flex-col items-center justify-center">
      <div className="max-w-md md:max-w-lg lg:max-w-xl w-full mx-auto p-5 md:p-8 lg:p-10 text-center box-border">
        <h1 className="text-2xl sm:text-3xl md:text-4xl lg:text-5xl font-bold mb-2.5 md:mb-4 text-gray-900 dark:text-white">英语学习工具</h1>
      <h2 className="mb-6 md:mb-8 lg:mb-10 text-gray-600 dark:text-gray-400 text-lg sm:text-xl md:text-2xl lg:text-3xl">注册</h2>
      <input
        type="email"
        placeholder="邮箱"
        value={email}
        onChange={e => setEmail(e.target.value)}
        className="w-full py-3.5 md:py-4 lg:py-5 px-4 md:px-5 my-2.5 md:my-3 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 text-base md:text-lg box-border appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400 focus:border-transparent"
      />
      <input
        type="password"
        placeholder="密码"
        value={password}
        onChange={e => setPassword(e.target.value)}
        className="w-full py-3.5 md:py-4 lg:py-5 px-4 md:px-5 my-2.5 md:my-3 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 text-base md:text-lg box-border appearance-none focus:outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400 focus:border-transparent"
      />
      <div className="my-6 md:my-8">
        <button 
          onClick={handleSignup} 
          disabled={loading} 
          className={`w-full py-3.5 md:py-4 lg:py-5 px-6 md:px-8 bg-blue-600 dark:bg-blue-700 text-white border-none rounded-lg text-base md:text-lg font-semibold min-h-[48px] md:min-h-[52px] touch-manipulation transition-colors ${
            loading ? 'cursor-not-allowed opacity-70' : 'cursor-pointer hover:bg-blue-700 dark:hover:bg-blue-600 active:bg-blue-800 dark:active:bg-blue-500'
          }`}
        >
          {loading ? '注册中...' : '注册'}
        </button>
      </div>
      <div className="mt-5 md:mt-6 text-gray-600 dark:text-gray-400 text-sm md:text-base">
        已有账号？{' '}
        <Link href="/" className="text-blue-600 dark:text-blue-400 no-underline font-medium hover:underline">
          立即登录
        </Link>
      </div>
      </div>
    </div>
  )
}


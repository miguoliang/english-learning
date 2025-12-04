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
    <div style={{ 
      maxWidth: 400, 
      width: '100%',
      margin: '0 auto',
      padding: '20px',
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      textAlign: 'center',
      boxSizing: 'border-box'
    }}>
      <h1 style={{ fontSize: 'clamp(24px, 5vw, 32px)', marginBottom: '10px', fontWeight: 'bold' }}>英语学习工具</h1>
      <h2 style={{ marginBottom: '30px', color: '#666', fontSize: 'clamp(18px, 4vw, 24px)' }}>注册</h2>
      <input
        type="email"
        placeholder="邮箱"
        value={email}
        onChange={e => setEmail(e.target.value)}
        style={{ 
          width: '100%', 
          padding: '14px 16px', 
          margin: '10px 0', 
          borderRadius: '8px', 
          border: '1px solid #ddd',
          fontSize: '16px',
          boxSizing: 'border-box',
          WebkitAppearance: 'none'
        }}
      />
      <input
        type="password"
        placeholder="密码"
        value={password}
        onChange={e => setPassword(e.target.value)}
        style={{ 
          width: '100%', 
          padding: '14px 16px', 
          margin: '10px 0', 
          borderRadius: '8px', 
          border: '1px solid #ddd',
          fontSize: '16px',
          boxSizing: 'border-box',
          WebkitAppearance: 'none'
        }}
      />
      <div style={{ margin: '24px 0' }}>
        <button 
          onClick={handleSignup} 
          disabled={loading} 
          style={{ 
            width: '100%',
            padding: '14px 24px', 
            backgroundColor: '#0070f3',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: loading ? 'not-allowed' : 'pointer',
            fontSize: '16px',
            fontWeight: '600',
            minHeight: '48px',
            touchAction: 'manipulation'
          }}
        >
          {loading ? '注册中...' : '注册'}
        </button>
      </div>
      <div style={{ marginTop: '20px', color: '#666', fontSize: '14px' }}>
        已有账号？{' '}
        <Link href="/" style={{ color: '#0070f3', textDecoration: 'none', fontWeight: '500' }}>
          立即登录
        </Link>
      </div>
    </div>
  )
}


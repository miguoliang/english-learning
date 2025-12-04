// src/app/api/knowledge/route.ts
import { createRouteHandlerClient } from '@/lib/supabaseServer'
import { NextResponse } from 'next/server'

export async function GET() {
  const supabase = await createRouteHandlerClient()

  const { data: { user } } = await supabase.auth.getUser()
  if (!user) return NextResponse.json({ error: '未登录' }, { status: 401 })

  // Check if user is operator
  if (user.user_metadata?.role !== 'operator') {
    return NextResponse.json({ error: '权限不足' }, { status: 403 })
  }

  const { data, error } = await supabase
    .from('knowledge')
    .select('code, name, description, metadata, created_at, updated_at')
    .order('created_at', { ascending: false })

  if (error) return NextResponse.json({ error: error.message }, { status: 500 })

  return NextResponse.json(data)
}


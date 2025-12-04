import { createMiddlewareClient } from '@/lib/supabaseServer'
import { NextRequest } from 'next/server'

export async function proxy(req: NextRequest) {
  const { supabase, res } = createMiddlewareClient(req)

  // Refresh session if expired - required for Server Components
  await supabase.auth.getSession()

  return res
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    '/((?!_next/static|_next/image|favicon.ico).*)',
  ],
}


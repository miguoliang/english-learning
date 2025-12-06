import { createMiddlewareClient } from '@/lib/supabaseServer'
import { NextRequest, NextResponse } from 'next/server'

export async function proxy(req: NextRequest) {
  try {
    const { supabase, res } = createMiddlewareClient(req)

    // Refresh session if expired - required for Server Components
    await supabase.auth.getSession()

    // Add CORS headers for Supabase API calls
    res.headers.set('Access-Control-Allow-Origin', '*')
    res.headers.set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    res.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization')

    return res
  } catch (error) {
    // If there's an error (e.g., during prerendering), just continue
    // This prevents crashes during static generation
    console.error(error)
    const res = NextResponse.next({ request: req })
    // Add CORS headers even on error
    res.headers.set('Access-Control-Allow-Origin', '*')
    res.headers.set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    res.headers.set('Access-Control-Allow-Headers', 'Content-Type, Authorization')
    return res
  }
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - _not-found (Next.js not-found page)
     * - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|_not-found|favicon.ico).*)',
  ],
}


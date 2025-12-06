import { createRouteHandlerClient } from "@/lib/supabaseServer";
import { NextRequest, NextResponse } from "next/server";

export async function GET(req: NextRequest) {
  const requestUrl = new URL(req.url);
  const code = requestUrl.searchParams.get("code");
  const supabase = await createRouteHandlerClient();

  if (code) {
    const { data, error } = await supabase.auth.exchangeCodeForSession(code);

    if (error) {
      return NextResponse.redirect(`${requestUrl.origin}/?error=${encodeURIComponent(error.message)}`);
    }

    // 根据用户角色跳转
    if (data.user?.user_metadata?.role === "operator") {
      return NextResponse.redirect(`${requestUrl.origin}/operator`);
    } else {
      return NextResponse.redirect(`${requestUrl.origin}/learn`);
    }
  }

  return NextResponse.redirect(`${requestUrl.origin}/`);
}


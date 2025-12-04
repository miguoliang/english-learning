// src/app/layout.tsx
import './globals.css'
import Providers from './providers'
import { Analytics } from "@vercel/analytics/next"
import { NavigationFooter } from './components/NavigationFooter'

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="zh" className="dark">
      <body>
        <Providers>{children}</Providers>
        <NavigationFooter />
        <Analytics />
      </body>
    </html>
  )
}

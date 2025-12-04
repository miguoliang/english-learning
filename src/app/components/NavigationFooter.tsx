"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

export function NavigationFooter() {
  const pathname = usePathname();

  return (
    <footer className="fixed bottom-0 left-0 right-0 bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700">
      <div className="flex justify-around py-4">
        <Link
          href="/learn"
          className={`text-2xl ${
            pathname === "/learn"
              ? "font-bold text-indigo-600 dark:text-indigo-400"
              : "text-gray-700 dark:text-gray-300 hover:text-indigo-600 dark:hover:text-indigo-400"
          }`}
        >
          Learn
        </Link>
        <Link
          href="/stats"
          className={`text-2xl ${
            pathname === "/stats"
              ? "font-bold text-indigo-600 dark:text-indigo-400"
              : "text-gray-700 dark:text-gray-300 hover:text-indigo-600 dark:hover:text-indigo-400"
          }`}
        >
          Stats
        </Link>
      </div>
    </footer>
  );
}


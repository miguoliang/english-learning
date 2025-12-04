interface DueCountBadgeProps {
  count: number;
}

// Heroicons Speaker Wave icon (outline version)
// Source: https://heroicons.com/ (MIT License)
function SpeakerIcon() {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
      strokeWidth={1.5}
      stroke="currentColor"
      className="w-12 h-12 md:w-14 md:h-14 text-gray-700 dark:text-gray-300"
      aria-hidden="true"
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M19.114 5.636a9 9 0 0 1 0 12.728M16.463 8.288a5.25 5.25 0 0 1 0 7.424M6.75 8.25l4.72-4.72a.75.75 0 0 1 .53-.22H15a.75.75 0 0 1 .75.75v15a.75.75 0 0 1-.75.75h-3.25a.75.75 0 0 1-.53-.22l-4.72-4.72H4.5a.75.75 0 0 1-.75-.75V9a.75.75 0 0 1 .75-.75h2.25Z"
      />
    </svg>
  );
}

export function DueCountBadge({ count }: DueCountBadgeProps) {
  if (count === 0) return null;

  return (
    <div className="fixed top-4 right-4 z-50">
      <div className="relative">
        <SpeakerIcon />
        <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold rounded-full w-6 h-6 flex items-center justify-center animate-pulse shadow-lg">
          {count}
        </span>
      </div>
    </div>
  );
}


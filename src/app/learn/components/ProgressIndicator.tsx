interface ProgressIndicatorProps {
  current: number;
  total: number;
}

export function ProgressIndicator({ current, total }: ProgressIndicatorProps) {
  return (
    <div className="w-full max-w-2xl text-center mb-8">
      <p className="text-2xl font-bold text-gray-700 dark:text-gray-300">
        {current} / {total}
      </p>
    </div>
  );
}


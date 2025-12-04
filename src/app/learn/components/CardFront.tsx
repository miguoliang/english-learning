import type { Knowledge } from "../types";

interface CardFrontProps {
  knowledge: Knowledge;
  onSpeak: (text: string, lang: "en-US" | "en-GB") => void;
}

export function CardFront({ knowledge, onSpeak }: CardFrontProps) {
  return (
    <div className="absolute inset-0 flex flex-col items-center justify-center backface-hidden">
      <h2 className="text-8xl font-bold text-gray-800 dark:text-white mb-8">
        {knowledge.name}
      </h2>

      {knowledge.metadata?.phonetic && (
        <p className="text-4xl text-indigo-600 dark:text-indigo-400 font-medium mb-8">
          {knowledge.metadata.phonetic}
        </p>
      )}

      <div className="flex items-center justify-center gap-4 mb-8">
        <button
          onClick={(e) => {
            e.stopPropagation();
            onSpeak(knowledge.name, "en-US");
          }}
          className="px-4 md:px-6 py-2 md:py-3 rounded-lg bg-indigo-100 dark:bg-indigo-900 text-indigo-800 dark:text-indigo-200 hover:scale-110 transition text-sm md:text-base font-medium"
        >
          US Speaker
        </button>
        <button
          onClick={(e) => {
            e.stopPropagation();
            onSpeak(knowledge.name, "en-GB");
          }}
          className="px-4 md:px-6 py-2 md:py-3 rounded-lg bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 hover:scale-110 transition text-sm md:text-base font-medium"
        >
          UK Speaker
        </button>
      </div>

      <p className="text-2xl text-gray-500 dark:text-gray-400">
        点击或滑动显示答案
      </p>
    </div>
  );
}


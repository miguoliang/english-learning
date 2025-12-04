interface RatingButtonsProps {
  onRate: (quality: number) => void;
}

export function RatingButtons({ onRate }: RatingButtonsProps) {
  const getButtonLabel = (q: number) => {
    if (q === 0) return "完全忘记";
    if (q === 5) return "完美";
    return q.toString();
  };

  const getButtonColor = (q: number) => {
    if (q < 3) return "bg-red-500 hover:bg-red-600";
    if (q < 5) return "bg-yellow-500 hover:bg-yellow-600";
    return "bg-green-500 hover:bg-green-600";
  };

  return (
    <div className="mt-12 grid grid-cols-3 gap-4 w-full max-w-2xl">
      {[0, 1, 2, 3, 4, 5].map((q) => (
        <button
          key={q}
          onClick={() => onRate(q)}
          className={`py-8 text-4xl font-bold rounded-2xl transition transform hover:scale-110 ${getButtonColor(
            q
          )} text-white shadow-xl`}
        >
          {getButtonLabel(q)}
        </button>
      ))}
    </div>
  );
}


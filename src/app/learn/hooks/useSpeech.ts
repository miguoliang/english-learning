export function useSpeech() {
  const speak = (text: string, lang: "en-US" | "en-GB" = "en-US") => {
    if ("speechSynthesis" in window) {
      window.speechSynthesis.cancel();
      const utter = new SpeechSynthesisUtterance(text);
      utter.lang = lang;
      utter.rate = 0.8;
      window.speechSynthesis.speak(utter);
    }
  };

  return { speak };
}


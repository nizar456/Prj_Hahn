import { useState, useCallback } from "react";
import { translations } from "./translations";
import { LanguageContext } from "./context";

export const LanguageProvider = ({ children }) => {
  const [language, setLanguage] = useState(() => {
    // Try to get saved language from localStorage
    const saved = localStorage.getItem("language");
    return saved && translations[saved] ? saved : "fr";
  });

  const toggleLanguage = useCallback(() => {
    setLanguage((prev) => {
      const newLang = prev === "fr" ? "en" : "fr";
      localStorage.setItem("language", newLang);
      return newLang;
    });
  }, []);

  const setLang = useCallback((lang) => {
    if (translations[lang]) {
      localStorage.setItem("language", lang);
      setLanguage(lang);
    }
  }, []);

  const t = useCallback(
    (key) => {
      return translations[language]?.[key] || translations.fr[key] || key;
    },
    [language]
  );

  return (
    <LanguageContext.Provider
      value={{ language, setLanguage: setLang, toggleLanguage, t }}
    >
      {children}
    </LanguageContext.Provider>
  );
};

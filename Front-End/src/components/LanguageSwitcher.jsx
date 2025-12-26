import { useLanguage } from "../i18n/useLanguage";

const LanguageSwitcher = () => {
  const { language, toggleLanguage } = useLanguage();

  return (
    <button
      type="button"
      onClick={toggleLanguage}
      className="fixed top-4 right-4 z-50 flex items-center gap-2 rounded-full border border-slate-700 
      bg-slate-900/90 px-4 py-2 text-xs font-semibold text-slate-200 shadow-lg backdrop-blur-sm 
      transition hover:border-sky-400 hover:text-white"
      title={language === "fr" ? "Switch to English" : "Passer en Français"}
    >
      <span>{language === "fr" ? "FR" : "EN"}</span>
    </button>
  );
};

export default LanguageSwitcher;

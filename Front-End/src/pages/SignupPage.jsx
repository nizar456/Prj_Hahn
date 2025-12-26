import { useState } from "react";
import { useLanguage } from "../i18n/useLanguage";

const SignupPage = ({ onSignup, onGoLogin }) => {
  const { t } = useLanguage();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  return (
    <div className="grid gap-10 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-sky-900/30 lg:grid-cols-[1fr,1.1fr]">
      <div className="space-y-4">
        <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
          {t("joinSpace")}
        </p>
        <h1 className="text-3xl font-semibold text-white">
          {t("createAccountTitle")}
        </h1>
        <p className="text-sm text-slate-300">{t("signupDescription")}</p>
        <button
          type="button"
          onClick={onGoLogin}
          className="text-xs font-semibold text-slate-200 underline decoration-dotted"
        >
          {t("alreadyAccount")}
        </button>
      </div>

      <div className="space-y-6 rounded-2xl border border-slate-800 bg-slate-950/60 p-6">
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">
            {t("email")}
          </label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-sky-400"
          />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">
            {t("password")}
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-sky-400"
          />
        </div>
        <button
          type="button"
          onClick={() => onSignup({ email, password })}
          className="w-full rounded-xl bg-sky-500 px-4 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-sky-500/30 transition hover:bg-sky-400"
        >
          {t("signup")}
        </button>
      </div>
    </div>
  );
};

export default SignupPage;

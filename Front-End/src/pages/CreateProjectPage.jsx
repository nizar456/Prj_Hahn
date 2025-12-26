import { useState } from "react";
import { useLanguage } from "../i18n/LanguageContext";

const CreateProjectPage = ({
  mode = "create",
  initialProject,
  onSubmit,
  onCancel,
}) => {
  const { t } = useLanguage();
  const [title, setTitle] = useState(initialProject?.title ?? t("newProject"));
  const [description, setDescription] = useState(
    initialProject?.description ?? t("describeProject")
  );

  const handleSubmit = () => {
    if (!title.trim()) return;
    onSubmit({ title, description });
  };

  return (
    <div
      className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 
    shadow-2xl shadow-sky-900/30"
    >
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            {t("project")}
          </p>
          <h1 className="text-3xl font-semibold text-white">
            {mode === "edit" ? t("updateAProject") : t("createAProject")}
          </h1>
          <p className="text-sm text-slate-300">
            {t("projectFormDescription")}
          </p>
        </div>
      </div>

      <div className="space-y-4 rounded-2xl border border-slate-800 bg-slate-950/60 p-6">
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">
            {t("title")}
          </label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 
            text-sm text-white outline-none focus:border-sky-400"
          />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">
            {t("description")}
          </label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm 
            text-white outline-none focus:border-sky-400"
          />
        </div>
        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={handleSubmit}
            className="rounded-xl bg-sky-500 px-4 py-3 text-sm font-semibold text-slate-950 shadow-lg 
            shadow-sky-500/30 transition hover:bg-sky-400"
          >
            {mode === "edit" ? t("updateTheProject") : t("createTheProject")}
          </button>
          <button
            type="button"
            onClick={onCancel}
            className="rounded-xl border border-slate-700 px-4 py-3 text-sm font-semibold text-slate-200 
            hover:border-slate-500"
          >
            {t("cancel")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateProjectPage;

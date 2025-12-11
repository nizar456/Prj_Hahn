import { useState } from "react";

const CreateProjectPage = ({ currentUser, onCreate, onCancel }) => {
  const [title, setTitle] = useState("Nouveau projet");
  const [description, setDescription] = useState(
    "Décrivez rapidement ce projet..."
  );

  const handleSubmit = () => {
    if (!title.trim()) return;
    onCreate({
      title,
      description,
      user_id: currentUser ? currentUser.id : "u1",
    });
  };

  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-sky-900/30">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            Projects
          </p>
          <h1 className="text-3xl font-semibold text-white">Créer un projet</h1>
          <p className="text-sm text-slate-300">
            Renseignez un titre et une courte description.
          </p>
        </div>
        <div className="flex gap-2 text-xs font-semibold text-slate-200">
          <span className="rounded-full bg-slate-800 px-3 py-1">POST</span>
        </div>
      </div>

      <div className="space-y-4 rounded-2xl border border-slate-800 bg-slate-950/60 p-6">
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">Titre</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-sky-400"
          />
        </div>
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">
            Description
          </label>
          <textarea
            rows={3}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-sky-400"
          />
        </div>
        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={handleSubmit}
            className="rounded-xl bg-sky-500 px-4 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-sky-500/30 transition hover:bg-sky-400"
          >
            Créer le projet
          </button>
          <button
            type="button"
            onClick={onCancel}
            className="rounded-xl border border-slate-700 px-4 py-3 text-sm font-semibold text-slate-200 hover:border-slate-500"
          >
            Annuler
          </button>
        </div>
      </div>
    </div>
  );
};

export default CreateProjectPage;

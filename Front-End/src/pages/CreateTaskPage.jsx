import { useState } from "react";

const CreateTaskPage = ({
  project,
  mode = "create",
  initialTask,
  onSubmit,
  onCancel,
}) => {
  const today = new Date().toISOString().slice(0, 10);
  const [title, setTitle] = useState(initialTask?.title ?? "Nouvelle tâche");
  const [description, setDescription] = useState(
    initialTask?.description ?? "Décrivez la tâche..."
  );
  const [dueDate, setDueDate] = useState(initialTask?.dueDate ?? today);
  const [completed, setCompleted] = useState(initialTask?.completed ?? false);

  const handleSubmit = () => {
    if (!title.trim()) return;
    onSubmit({ title, description, dueDate, completed });
  };

  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-amber-900/30">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            Tâche
          </p>
          <h1 className="text-3xl font-semibold text-white">
            {mode === "edit" ? "Mettre à jour" : "Ajouter"} une tâche
          </h1>
          <p className="text-sm text-slate-300">Projet: {project.title}</p>
        </div>
      </div>

      <div className="space-y-4 rounded-2xl border border-slate-800 bg-slate-950/60 p-6">
        <div className="space-y-2">
          <label className="text-sm font-semibold text-slate-200">Titre</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-amber-400"
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
            className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-amber-400"
          />
        </div>
        <div className="grid gap-4 md:grid-cols-2">
          <div className="space-y-2">
            <label className="text-sm font-semibold text-slate-200">
              Due date
            </label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full rounded-xl border border-slate-800 bg-slate-900/80 px-3 py-2 text-sm text-white outline-none focus:border-amber-400"
            />
          </div>
          <label className="mt-6 inline-flex items-center gap-2 text-sm font-semibold text-slate-200">
            <input
              type="checkbox"
              checked={completed}
              onChange={(e) => setCompleted(e.target.checked)}
              className="h-4 w-4 rounded border-slate-700 bg-slate-900 text-amber-400 focus:ring-amber-400"
            />
            Marquer comme terminée
          </label>
        </div>
        <div className="flex flex-wrap gap-3">
          <button
            type="button"
            onClick={handleSubmit}
            className="rounded-xl bg-amber-400 px-4 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-amber-400/30 transition hover:bg-amber-300"
          >
            {mode === "edit" ? "Mettre à jour" : "Ajouter la tâche"}
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

export default CreateTaskPage;

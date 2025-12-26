import { useLanguage } from "../i18n/LanguageContext";

const TasksPage = ({
  project,
  tasks,
  onBack,
  onAddTask,
  onEditTask,
  onDeleteTask,
  onToggleTask,
  onRefreshTasks,
}) => {
  const { t } = useLanguage();

  const progress = (() => {
    if (!tasks.length) return 0;
    const done = tasks.filter((task) => task.completed).length;
    return Math.round((done / tasks.length) * 100);
  })();

  return (
    <div className="space-y-6 rounded-3xl border border-slate-800 bg-slate-900/70 p-8 shadow-2xl shadow-amber-900/30">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-amber-200">
            {t("tasks")}
          </p>
          <h1 className="text-3xl font-semibold text-white">{project.title}</h1>
          <p className="text-sm text-slate-300">{t("tasksDescription")}</p>
        </div>
        <div className="flex flex-wrap gap-2 text-xs font-semibold text-slate-200">
          <button
            type="button"
            onClick={onAddTask}
            className="rounded-full bg-amber-400 px-4 py-2 text-slate-950 shadow-sm shadow-amber-400/30 transition hover:bg-amber-300"
          >
            {t("addTask")}
          </button>
          <button
            type="button"
            onClick={onRefreshTasks}
            className="rounded-full border border-slate-700 px-4 py-2 text-slate-200 hover:border-amber-300"
          >
            {t("refresh")}
          </button>
          <button
            type="button"
            onClick={onBack}
            className="rounded-full border border-slate-700 px-4 py-2 text-slate-200 hover:border-sky-400"
          >
            {t("backToProjects")}
          </button>
        </div>
      </div>

      <div className="w-full max-w-xl">
        <div className="flex items-center justify-between text-xs font-semibold text-slate-200">
          <span>{t("progress")}</span>
          <span>{progress}%</span>
        </div>
        <div className="mt-2 h-3 rounded-full bg-slate-800">
          <div
            className="h-3 rounded-full bg-gradient-to-r from-amber-300 via-amber-400 to-amber-200"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>

      <div className="grid gap-3 lg:grid-cols-[2fr,1fr]">
        <div className="space-y-3">
          {tasks.map((task) => (
            <div
              key={task.id}
              className="flex flex-col gap-3 rounded-2xl border border-slate-800 bg-slate-950/60 p-4"
            >
              <div className="flex items-start justify-between gap-3">
                <div className="space-y-1">
                  <p className="text-sm font-semibold text-white">
                    {task.title}
                  </p>
                  <p className="text-xs text-slate-400">{task.description}</p>
                  <p className="text-[11px] text-slate-500">
                    {t("dueDate")}: {task.dueDate}
                  </p>
                </div>
                <span
                  className={`rounded-full px-3 py-1 text-[11px] font-semibold ${
                    task.completed
                      ? "bg-emerald-900/40 text-emerald-200"
                      : "bg-slate-800 text-slate-200"
                  }`}
                >
                  {task.completed ? t("completed") : t("inProgress")}
                </span>
              </div>
              <div className="flex flex-wrap gap-2 text-xs font-semibold text-slate-200">
                <button
                  type="button"
                  onClick={() => onToggleTask(task)}
                  className="rounded-full bg-emerald-500 px-3 py-2 text-emerald-950 shadow-sm shadow-emerald-500/30 transition hover:bg-emerald-400"
                >
                  {task.completed ? t("markAsInProgress") : t("markAsDone")}
                </button>
                <button
                  type="button"
                  onClick={() => onEditTask(task)}
                  className="rounded-full border border-slate-700 px-3 py-2 text-slate-200 hover:border-amber-300"
                >
                  {t("edit")}
                </button>
                <button
                  type="button"
                  onClick={() => onDeleteTask(task.id)}
                  className="rounded-full border border-red-900/70 px-3 py-2 text-red-200 hover:border-red-500 hover:text-red-100"
                >
                  {t("delete")}
                </button>
              </div>
            </div>
          ))}
          {!tasks.length && (
            <div className="rounded-2xl border border-dashed border-slate-700 bg-slate-950/40 p-6 text-sm text-slate-300">
              {t("noTasksYet")}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TasksPage;

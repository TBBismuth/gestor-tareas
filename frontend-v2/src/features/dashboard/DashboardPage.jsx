import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskList from "./components/TaskList.jsx";
import { getMyTasks } from "./api/tasksApi.js";
import { mapTaskResponsesToCardTasks } from "./mappers/taskMapper.js";

const VIEW_MINE = "mine";

export default function DashboardPage() {
  const [focusArea, setFocusArea] = useState("filter");
  const [activeView, setActiveView] = useState(VIEW_MINE);
  const tasksQuery = useQuery({
    queryKey: ["tasks", "mine"],
    queryFn: getMyTasks,
    select: mapTaskResponsesToCardTasks,
    enabled: activeView === VIEW_MINE,
  });

  const tasks = tasksQuery.data ?? [];

  return (
    <AppShell
      focusArea={focusArea}
      topBar={<MegaFilterBar onFocus={() => setFocusArea("filter")} />}
      secondaryBar={
        <ViewActionsBar
          activeView={activeView}
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
          onViewChange={setActiveView}
        />
      }
      sidebar={
        <RightSidebar
          activeView={activeView}
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
          onViewChange={setActiveView}
        />
      }
    >
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-muted">
            Pantalla principal
          </p>
          <h1 className="mt-1 text-2xl font-semibold text-primary">Tareas</h1>
        </div>
      </div>
      {activeView === VIEW_MINE ? (
        <>
          {tasksQuery.isLoading && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Cargando tareas...
            </p>
          )}
          {tasksQuery.isError && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron cargar tus tareas.
            </p>
          )}
          {tasksQuery.isSuccess && tasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Todavia no tienes tareas.
            </p>
          )}
          {tasks.length > 0 && <TaskList tasks={tasks} />}
        </>
      ) : (
        <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
          Esta vista se conectara en un bloque posterior.
        </p>
      )}
    </AppShell>
  );
}

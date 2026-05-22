import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskList from "./components/TaskList.jsx";
import { completeTask, getMyTasks } from "./api/tasksApi.js";
import { mapTaskResponsesToCardTasks } from "./mappers/taskMapper.js";

const VIEW_MINE = "mine";
const MY_TASKS_QUERY_KEY = ["tasks", "mine"];

function getTaskActionErrorMessage(error) {
  return error?.response?.data?.error || "No se pudo completar la tarea.";
}

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const [focusArea, setFocusArea] = useState("filter");
  const [activeView, setActiveView] = useState(VIEW_MINE);
  const tasksQuery = useQuery({
    queryKey: MY_TASKS_QUERY_KEY,
    queryFn: getMyTasks,
    select: mapTaskResponsesToCardTasks,
    enabled: activeView === VIEW_MINE,
  });
  const completeTaskMutation = useMutation({
    mutationFn: (task) => completeTask(task.idTarea),
    onSuccess: () => {
      toast.success("Tarea completada.");
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(getTaskActionErrorMessage(error));
    },
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
          {tasks.length > 0 && (
            <TaskList
              completingTaskId={completeTaskMutation.variables?.idTarea}
              isCompleting={completeTaskMutation.isPending}
              onCompleteTask={(task) => completeTaskMutation.mutate(task)}
              tasks={tasks}
            />
          )}
        </>
      ) : (
        <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
          Esta vista se conectara en un bloque posterior.
        </p>
      )}
    </AppShell>
  );
}

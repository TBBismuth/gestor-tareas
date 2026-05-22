import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Plus } from "lucide-react";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import Button from "../../components/ui/Button.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskFormModal from "./components/TaskFormModal.jsx";
import TaskList from "./components/TaskList.jsx";
import { getMyCategories } from "./api/categoriesApi.js";
import { completeTask, createTask, getMyTasks } from "./api/tasksApi.js";
import { mapTaskResponsesToCardTasks } from "./mappers/taskMapper.js";
import { sortMyTasks } from "./utils/taskSorting.js";

const VIEW_MINE = "mine";
const VIEW_CATEGORIES = "categories";
const MY_TASKS_QUERY_KEY = ["tasks", "mine"];
const CATEGORIES_QUERY_KEY = ["categories", "mine"];

function getTaskActionErrorMessage(error) {
  return error?.response?.data?.error || "No se pudo completar la tarea.";
}

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const [taskModalOpen, setTaskModalOpen] = useState(false);
  const [focusArea, setFocusArea] = useState("filter");
  const [activeView, setActiveView] = useState(VIEW_MINE);
  const categoriesQuery = useQuery({
    queryKey: CATEGORIES_QUERY_KEY,
    queryFn: getMyCategories,
    enabled: taskModalOpen,
  });
  const tasksQuery = useQuery({
    queryKey: MY_TASKS_QUERY_KEY,
    queryFn: getMyTasks,
    select: (data) => sortMyTasks(mapTaskResponsesToCardTasks(data)),
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
  const createTaskMutation = useMutation({
    mutationFn: createTask,
    onSuccess: () => {
      toast.success("Tarea creada.");
      setTaskModalOpen(false);
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear la tarea.");
    },
  });

  const tasks = tasksQuery.data ?? [];
  const headerActionLabel =
    activeView === VIEW_CATEGORIES ? "Nueva categoria" : "Nueva tarea";

  function handleHeaderAction() {
    if (activeView === VIEW_CATEGORIES) {
      toast.info("Crear categoria se conectara en un bloque posterior.");
      return;
    }

    setTaskModalOpen(true);
  }

  return (
    <AppShell
      focusArea={focusArea}
      topBar={
        <MegaFilterBar onFocus={() => setFocusArea("filter")} />
      }
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
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h1 className="mt-1 text-2xl font-semibold text-primary">Tareas</h1>
        </div>
        <Button className="w-full sm:w-auto" onClick={handleHeaderAction}>
          <Plus size={17} />
          {headerActionLabel}
        </Button>
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
      <TaskFormModal
        categories={categoriesQuery.data ?? []}
        categoriesError={categoriesQuery.isError}
        categoriesLoading={categoriesQuery.isLoading}
        creating={createTaskMutation.isPending}
        onClose={() => setTaskModalOpen(false)}
        onSubmit={(payload) => createTaskMutation.mutateAsync(payload)}
        open={taskModalOpen}
      />
    </AppShell>
  );
}

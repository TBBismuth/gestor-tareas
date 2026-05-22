import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Plus } from "lucide-react";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import Button from "../../components/ui/Button.jsx";
import CategoryFormModal from "./components/CategoryFormModal.jsx";
import CategoryGrid from "./components/CategoryGrid.jsx";
import DeleteCategoryModal from "./components/DeleteCategoryModal.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskFormModal from "./components/TaskFormModal.jsx";
import TaskList from "./components/TaskList.jsx";
import { createCategory, getMyCategories } from "./api/categoriesApi.js";
import { completeTask, createTask, getMyTasks } from "./api/tasksApi.js";
import { mapTaskResponsesToCardTasks } from "./mappers/taskMapper.js";
import { sortMyTasks } from "./utils/taskSorting.js";

const VIEW_MINE = "mine";
const VIEW_CATEGORIES = "categories";
const VIEW_GROUPS = "groups";
const VIEW_SMART = "smart";
const MY_TASKS_QUERY_KEY = ["tasks", "mine"];
const CATEGORIES_QUERY_KEY = ["categories", "mine"];

const viewTitles = {
  [VIEW_MINE]: "Tareas",
  [VIEW_CATEGORIES]: "Categorías",
  [VIEW_GROUPS]: "Grupos",
  [VIEW_SMART]: "Inteligente",
};

function getTaskActionErrorMessage(error) {
  return error?.response?.data?.error || "No se pudo completar la tarea.";
}

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const [taskModalOpen, setTaskModalOpen] = useState(false);
  const [categoryModalOpen, setCategoryModalOpen] = useState(false);
  const [categoryModalSource, setCategoryModalSource] = useState(null);
  const [categoryToSelectInTask, setCategoryToSelectInTask] = useState(null);
  const [categoryToDelete, setCategoryToDelete] = useState(null);
  const [focusArea, setFocusArea] = useState("filter");
  const [activeView, setActiveView] = useState(VIEW_MINE);
  const categoriesQuery = useQuery({
    queryKey: CATEGORIES_QUERY_KEY,
    queryFn: getMyCategories,
    enabled: taskModalOpen || activeView === VIEW_CATEGORIES,
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
      setCategoryToSelectInTask(null);
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear la tarea.");
    },
  });
  const createCategoryMutation = useMutation({
    mutationFn: createCategory,
    onSuccess: (createdCategory) => {
      toast.success("Categoría creada.");
      queryClient.setQueryData(CATEGORIES_QUERY_KEY, (current = []) => {
        if (current.some((category) => category.idCategoria === createdCategory.idCategoria)) {
          return current;
        }

        return [...current, createdCategory];
      });
      queryClient.invalidateQueries({ queryKey: CATEGORIES_QUERY_KEY });

      if (categoryModalSource === "task") {
        setCategoryToSelectInTask(createdCategory);
      }

      setCategoryModalOpen(false);
      setCategoryModalSource(null);
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear la categoría.");
    },
  });

  const tasks = tasksQuery.data ?? [];
  const categories = categoriesQuery.data ?? [];
  const headerActionLabel =
    activeView === VIEW_CATEGORIES ? "Nueva categoría" : "Nueva tarea";
  const pageTitle = viewTitles[activeView] || "Tareas";

  function handleHeaderAction() {
    if (activeView === VIEW_CATEGORIES) {
      setCategoryModalSource("categories");
      setCategoryModalOpen(true);
      return;
    }

    setCategoryToSelectInTask(null);
    setTaskModalOpen(true);
  }

  function handleOpenCategoryFromTask() {
    setCategoryModalSource("task");
    setCategoryModalOpen(true);
  }

  function handleCloseCategoryModal() {
    if (createCategoryMutation.isPending) {
      return;
    }

    setCategoryModalOpen(false);
    setCategoryModalSource(null);
  }

  function handleEditCategory() {
    toast.info("Editar categoría se conectará en un bloque posterior.");
  }

  function handleDeleteCategoryPlaceholder() {
    toast.info("Eliminar categoría se conectará en un bloque posterior.");
    setCategoryToDelete(null);
  }

  function handleDeleteAndCreateCategoryPlaceholder() {
    toast.info("Eliminar categoría se conectará en un bloque posterior.");
    toast.info("Crear categoría se conectará en un bloque posterior.");
    setCategoryToDelete(null);
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
          <h1 className="mt-1 text-2xl font-semibold text-primary">{pageTitle}</h1>
        </div>
        <Button className="w-full sm:w-auto" onClick={handleHeaderAction}>
          <Plus size={17} />
          {headerActionLabel}
        </Button>
      </div>
      {activeView === VIEW_MINE && (
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
      )}
      {activeView === VIEW_CATEGORIES && (
        <>
          {categoriesQuery.isLoading && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Cargando categorías...
            </p>
          )}
          {categoriesQuery.isError && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron cargar las categorías.
            </p>
          )}
          {categoriesQuery.isSuccess && categories.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Todavía no tienes categorías.
            </p>
          )}
          {categories.length > 0 && (
            <CategoryGrid
              categories={categories}
              onDelete={setCategoryToDelete}
              onEdit={handleEditCategory}
            />
          )}
        </>
      )}
      {activeView !== VIEW_MINE && activeView !== VIEW_CATEGORIES && (
        <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
          Esta vista se conectara en un bloque posterior.
        </p>
      )}
      <TaskFormModal
        categories={categoriesQuery.data ?? []}
        categoriesError={categoriesQuery.isError}
        categoriesLoading={categoriesQuery.isLoading}
        categoryToSelect={categoryToSelectInTask}
        creating={createTaskMutation.isPending}
        onClose={() => {
          setTaskModalOpen(false);
          setCategoryToSelectInTask(null);
        }}
        onCreateCategory={handleOpenCategoryFromTask}
        onSubmit={(payload) => createTaskMutation.mutateAsync(payload)}
        open={taskModalOpen}
      />
      <CategoryFormModal
        creating={createCategoryMutation.isPending}
        onClose={handleCloseCategoryModal}
        onSubmit={(payload) => createCategoryMutation.mutateAsync(payload)}
        open={categoryModalOpen}
        zIndexClass={taskModalOpen ? "z-[60]" : "z-50"}
      />
      <DeleteCategoryModal
        category={categoryToDelete}
        onClose={() => setCategoryToDelete(null)}
        onConfirm={handleDeleteCategoryPlaceholder}
        onConfirmAndCreate={handleDeleteAndCreateCategoryPlaceholder}
        open={Boolean(categoryToDelete)}
      />
    </AppShell>
  );
}

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
import DeleteTaskModal from "./components/DeleteTaskModal.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import TaskFormModal from "./components/TaskFormModal.jsx";
import TaskList from "./components/TaskList.jsx";
import {
  createCategory,
  deleteCategory,
  getMyCategories,
  updateCategory,
} from "./api/categoriesApi.js";
import { completeTask, createTask, deleteTask, getMyTasks, updateTask } from "./api/tasksApi.js";
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

function getActionErrorMessage(error, fallback) {
  return error?.response?.data?.error || error?.response?.data?.message || fallback;
}

function getCategoryActionErrorMessage(error, fallback) {
  return error?.response?.data?.error || error?.response?.data?.message || fallback;
}

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const [taskModalOpen, setTaskModalOpen] = useState(false);
  const [taskModalMode, setTaskModalMode] = useState("create");
  const [taskToEdit, setTaskToEdit] = useState(null);
  const [taskToDelete, setTaskToDelete] = useState(null);
  const [categoryModalOpen, setCategoryModalOpen] = useState(false);
  const [categoryModalMode, setCategoryModalMode] = useState("create");
  const [categoryModalSource, setCategoryModalSource] = useState(null);
  const [categoryToEdit, setCategoryToEdit] = useState(null);
  const [categoryToSelectInTask, setCategoryToSelectInTask] = useState(null);
  const [categoryToDelete, setCategoryToDelete] = useState(null);
  const [deleteShouldOpenCreate, setDeleteShouldOpenCreate] = useState(false);
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
      toast.error(getActionErrorMessage(error, "No se pudo completar la tarea."));
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
  const updateTaskMutation = useMutation({
    mutationFn: ({ id, payload }) => updateTask(id, payload),
    onSuccess: () => {
      toast.success("Tarea actualizada.");
      setTaskModalOpen(false);
      setTaskModalMode("create");
      setTaskToEdit(null);
      setCategoryToSelectInTask(null);
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(getActionErrorMessage(error, "No se pudo actualizar la tarea."));
    },
  });
  const deleteTaskMutation = useMutation({
    mutationFn: ({ task }) => deleteTask(task.idTarea),
    onSuccess: () => {
      toast.success("Tarea eliminada.");
      setTaskToDelete(null);
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: () => {
      toast.error("No se pudo eliminar la tarea.");
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
      setCategoryModalMode("create");
      setCategoryModalSource(null);
      setCategoryToEdit(null);
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear la categoría.");
    },
  });

  const updateCategoryMutation = useMutation({
    mutationFn: ({ id, payload }) => updateCategory(id, payload),
    onSuccess: () => {
      toast.success("Categoría actualizada.");
      setCategoryModalOpen(false);
      setCategoryModalMode("create");
      setCategoryModalSource(null);
      setCategoryToEdit(null);
      queryClient.invalidateQueries({ queryKey: CATEGORIES_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(getCategoryActionErrorMessage(error, "No se pudo actualizar la categoría."));
    },
  });
  const deleteCategoryMutation = useMutation({
    mutationFn: ({ category }) => deleteCategory(category.idCategoria),
    onSuccess: (_data, variables) => {
      toast.success("Categoría eliminada.");
      setCategoryToDelete(null);
      queryClient.invalidateQueries({ queryKey: CATEGORIES_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });

      if (variables.openCreateAfter) {
        setCategoryModalMode("create");
        setCategoryModalSource("categories");
        setCategoryToEdit(null);
        setCategoryModalOpen(true);
      }

      setDeleteShouldOpenCreate(false);
    },
    onError: (error) => {
      toast.error(getCategoryActionErrorMessage(error, "No se pudo eliminar la categoría."));
      setDeleteShouldOpenCreate(false);
    },
  });

  const tasks = tasksQuery.data ?? [];
  const categories = categoriesQuery.data ?? [];
  const headerActionLabel =
    activeView === VIEW_CATEGORIES ? "Nueva categoría" : "Nueva tarea";
  const pageTitle = viewTitles[activeView] || "Tareas";

  function handleHeaderAction() {
    if (activeView === VIEW_CATEGORIES) {
      setCategoryModalMode("create");
      setCategoryModalSource("categories");
      setCategoryToEdit(null);
      setCategoryModalOpen(true);
      return;
    }

    setCategoryToSelectInTask(null);
    setTaskModalMode("create");
    setTaskToEdit(null);
    setTaskModalOpen(true);
  }

  function handleOpenCategoryFromTask() {
    setCategoryModalMode("create");
    setCategoryModalSource("task");
    setCategoryToEdit(null);
    setCategoryModalOpen(true);
  }

  function handleCloseCategoryModal() {
    if (createCategoryMutation.isPending || updateCategoryMutation.isPending) {
      return;
    }

    setCategoryModalOpen(false);
    setCategoryModalMode("create");
    setCategoryModalSource(null);
    setCategoryToEdit(null);
  }

  function handleCloseTaskModal() {
    if (createTaskMutation.isPending || updateTaskMutation.isPending) {
      return;
    }

    setTaskModalOpen(false);
    setTaskModalMode("create");
    setTaskToEdit(null);
    setCategoryToSelectInTask(null);
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

  function handleEditCategoryReal(category) {
    if (category.protegida) {
      return;
    }

    setCategoryModalMode("edit");
    setCategoryModalSource("categories");
    setCategoryToEdit(category);
    setCategoryModalOpen(true);
  }

  function handleDeleteCategoryReal() {
    if (!categoryToDelete || categoryToDelete.protegida) {
      return;
    }

    setDeleteShouldOpenCreate(false);
    deleteCategoryMutation.mutate({
      category: categoryToDelete,
      openCreateAfter: false,
    });
  }

  function handleDeleteAndCreateCategoryReal() {
    if (!categoryToDelete || categoryToDelete.protegida) {
      return;
    }

    setDeleteShouldOpenCreate(true);
    deleteCategoryMutation.mutate({
      category: categoryToDelete,
      openCreateAfter: true,
    });
  }

  function handleSubmitCategory(payload) {
    if (categoryModalMode === "edit" && categoryToEdit) {
      return updateCategoryMutation.mutateAsync({
        id: categoryToEdit.idCategoria,
        payload,
      });
    }

    return createCategoryMutation.mutateAsync(payload);
  }

  function isTaskCompleted(task) {
    return (
      task?.completada === true ||
      task?.estado === "COMPLETADA" ||
      task?.estado === "COMPLETADA_CON_RETRASO"
    );
  }

  function handleEditTask(task) {
    setTaskModalMode("edit");
    setTaskToEdit(task);
    setCategoryToSelectInTask(null);
    setTaskModalOpen(true);
  }

  function handleDeleteTask(task) {
    if (deleteTaskMutation.isPending) {
      return;
    }

    if (isTaskCompleted(task)) {
      deleteTaskMutation.mutate({ task });
      return;
    }

    setTaskToDelete(task);
  }

  function handleConfirmDeleteTask() {
    if (!taskToDelete || deleteTaskMutation.isPending) {
      return;
    }

    deleteTaskMutation.mutate({ task: taskToDelete });
  }

  function handleSubmitTask(payload) {
    if (taskModalMode === "edit" && taskToEdit) {
      const completed = isTaskCompleted(taskToEdit);

      return updateTaskMutation.mutateAsync({
        id: taskToEdit.idTarea,
        payload: {
          ...payload,
          completada: completed,
          fechaCompletada: completed ? taskToEdit.fechaCompletada : null,
        },
      });
    }

    return createTaskMutation.mutateAsync(payload);
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
              deletingTaskId={deleteTaskMutation.variables?.task?.idTarea}
              isDeleting={deleteTaskMutation.isPending}
              isCompleting={completeTaskMutation.isPending}
              onCompleteTask={(task) => completeTaskMutation.mutate(task)}
              onDeleteTask={handleDeleteTask}
              onEditTask={handleEditTask}
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
              onEdit={handleEditCategoryReal}
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
        creating={createTaskMutation.isPending || updateTaskMutation.isPending}
        initialTask={taskModalMode === "edit" ? taskToEdit : null}
        mode={taskModalMode}
        onClose={handleCloseTaskModal}
        onCreateCategory={handleOpenCategoryFromTask}
        onSubmit={handleSubmitTask}
        open={taskModalOpen}
      />
      <CategoryFormModal
        creating={createCategoryMutation.isPending || updateCategoryMutation.isPending}
        initialCategory={categoryModalMode === "edit" ? categoryToEdit : null}
        mode={categoryModalMode}
        onClose={handleCloseCategoryModal}
        onSubmit={handleSubmitCategory}
        open={categoryModalOpen}
        zIndexClass={taskModalOpen ? "z-[60]" : "z-50"}
      />
      <DeleteCategoryModal
        category={categoryToDelete}
        deleting={deleteCategoryMutation.isPending}
        onClose={() => {
          if (!deleteCategoryMutation.isPending) {
            setCategoryToDelete(null);
            setDeleteShouldOpenCreate(false);
          }
        }}
        onConfirm={handleDeleteCategoryReal}
        onConfirmAndCreate={handleDeleteAndCreateCategoryReal}
        open={Boolean(categoryToDelete)}
      />
      <DeleteTaskModal
        deleting={deleteTaskMutation.isPending}
        onClose={() => {
          if (!deleteTaskMutation.isPending) {
            setTaskToDelete(null);
          }
        }}
        onConfirm={handleConfirmDeleteTask}
        open={Boolean(taskToDelete)}
        task={taskToDelete}
      />
    </AppShell>
  );
}

import { useCallback, useEffect, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Plus, UserPlus } from "lucide-react";
import AppShell from "../../components/layout/AppShell.jsx";
import RightSidebar from "../../components/layout/RightSidebar.jsx";
import ViewActionsBar from "../../components/layout/ViewActionsBar.jsx";
import Button from "../../components/ui/Button.jsx";
import CategoryFormModal from "./components/CategoryFormModal.jsx";
import CategoryGrid from "./components/CategoryGrid.jsx";
import DeleteCategoryModal from "./components/DeleteCategoryModal.jsx";
import DeleteGroupModal from "./components/DeleteGroupModal.jsx";
import DeleteTaskModal from "./components/DeleteTaskModal.jsx";
import GroupFormModal from "./components/GroupFormModal.jsx";
import GroupGrid from "./components/GroupGrid.jsx";
import GroupAssignmentsModal from "./components/GroupAssignmentsModal.jsx";
import GroupMembersModal from "./components/GroupMembersModal.jsx";
import InvitationCodeModal from "./components/InvitationCodeModal.jsx";
import JoinGroupModal from "./components/JoinGroupModal.jsx";
import LeaveGroupModal from "./components/LeaveGroupModal.jsx";
import MegaFilterBar from "./components/MegaFilterBar.jsx";
import QuickTaskSearch from "./components/QuickTaskSearch.jsx";
import TaskFormModal from "./components/TaskFormModal.jsx";
import TaskList from "./components/TaskList.jsx";
import { useAuth } from "../auth/AuthContext.jsx";
import {
  createCategory,
  deleteCategory,
  getMyCategories,
  updateCategory,
} from "./api/categoriesApi.js";
import {
  createGroup,
  deleteGroup,
  getMyGroups,
  joinGroup,
  leaveGroup,
  toggleGroupActive,
  updateGroup,
} from "./api/groupsApi.js";
import {
  completeTask,
  createTask,
  deleteTask,
  filterCombinedTasks,
  getAssignedGroupTasks,
  getMyTasks,
  getRecommendedTasks,
  getSavedAdvancedFilter,
  saveAdvancedFilter,
  updateTask,
} from "./api/tasksApi.js";
import {
  mapRecommendedTaskResponsesToCardTasks,
  mapTaskResponsesToCardTasks,
} from "./mappers/taskMapper.js";
import { filterTasksByQuickSearch } from "./utils/taskSearch.js";
import { sortMyTasks } from "./utils/taskSorting.js";

const VIEW_MINE = "mine";
const VIEW_CATEGORIES = "categories";
const VIEW_GROUPS = "groups";
const VIEW_SMART = "smart";
const MY_TASKS_QUERY_KEY = ["tasks", "mine"];
const RECOMMENDED_TASKS_QUERY_KEY = ["tasks", "recommended"];
const CATEGORIES_QUERY_KEY = ["categories", "mine"];
const GROUPS_QUERY_KEY = ["groups", "mine"];

const DEFAULT_MEGA_FILTERS = {
  origen: "TODAS",
  idGrupo: "",
  prioridades: [],
  estados: [],
  idCategoria: "",
  palabrasClave: "",
  tiempoMax: "",
  fechaEntregaExacta: "",
  fechaEntregaHasta: "",
  criterioOrdenActivo: "FECHA_AGREGADO",
};

function getDefaultMegaFilters() {
  return {
    ...DEFAULT_MEGA_FILTERS,
    prioridades: [],
    estados: [],
  };
}

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

function buildCombinedFilterPayload(filters) {
  const payload = {
    origen: filters.origen || "TODAS",
    criterioOrdenActivo: filters.criterioOrdenActivo || "FECHA_AGREGADO",
  };

  if (filters.idGrupo) {
    payload.idGrupo = Number(filters.idGrupo);
  }

  if (filters.idCategoria) {
    payload.idCategoria = Number(filters.idCategoria);
  }

  if (filters.prioridades.length > 0) {
    payload.prioridades = filters.prioridades;
  }

  if (filters.estados.length > 0) {
    payload.estados = filters.estados;
  }

  const palabrasClave = filters.palabrasClave.trim();
  if (palabrasClave) {
    payload.palabrasClave = palabrasClave;
  }

  const tiempoMax = Number(filters.tiempoMax);
  if (Number.isFinite(tiempoMax) && tiempoMax > 0) {
    payload.tiempoMax = tiempoMax;
  }

  if (filters.fechaEntregaExacta) {
    payload.fechaEntregaExacta = filters.fechaEntregaExacta;
  } else if (filters.fechaEntregaHasta) {
    payload.fechaEntregaHasta = filters.fechaEntregaHasta;
  }

  return payload;
}

function buildSavedAdvancedFilterPayload(filters) {
  return {
    ...buildCombinedFilterPayload(filters),
    prioridades: filters.prioridades,
    estados: filters.estados,
  };
}

function normalizeSavedEnumList(savedList, legacyValue) {
  if (Array.isArray(savedList)) {
    return savedList.filter(Boolean);
  }

  return legacyValue ? [legacyValue] : [];
}

function normalizeSavedMegaFilters(savedFilter) {
  if (!savedFilter || typeof savedFilter !== "object") {
    return getDefaultMegaFilters();
  }

  return {
    origen: savedFilter.origen || "TODAS",
    idGrupo: savedFilter.idGrupo == null ? "" : String(savedFilter.idGrupo),
    prioridades: normalizeSavedEnumList(savedFilter.prioridades, savedFilter.prioridad),
    estados: normalizeSavedEnumList(savedFilter.estados, savedFilter.estado),
    idCategoria: savedFilter.idCategoria == null ? "" : String(savedFilter.idCategoria),
    palabrasClave: savedFilter.palabrasClave || "",
    tiempoMax: savedFilter.tiempoMax == null ? "" : String(savedFilter.tiempoMax),
    fechaEntregaExacta: savedFilter.fechaEntregaExacta || "",
    fechaEntregaHasta: savedFilter.fechaEntregaHasta || "",
    criterioOrdenActivo: savedFilter.criterioOrdenActivo || "FECHA_AGREGADO",
  };
}

export default function DashboardPage() {
  const queryClient = useQueryClient();
  const { user } = useAuth();
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
  const [groupModalOpen, setGroupModalOpen] = useState(false);
  const [groupModalMode, setGroupModalMode] = useState("create");
  const [groupToEdit, setGroupToEdit] = useState(null);
  const [groupToDelete, setGroupToDelete] = useState(null);
  const [groupToLeave, setGroupToLeave] = useState(null);
  const [groupToInvite, setGroupToInvite] = useState(null);
  const [groupToViewAssignments, setGroupToViewAssignments] = useState(null);
  const [groupAssignmentsMode, setGroupAssignmentsMode] = useState("member");
  const [joinGroupModalOpen, setJoinGroupModalOpen] = useState(false);
  const [groupToViewMembers, setGroupToViewMembers] = useState(null);
  const [groupRoles, setGroupRoles] = useState({});
  const [focusArea, setFocusArea] = useState("filter");
  const [activeView, setActiveView] = useState(VIEW_MINE);
  const [quickSearch, setQuickSearch] = useState("");
  const [megaFilters, setMegaFilters] = useState(() => getDefaultMegaFilters());
  const [isMegaFilterActive, setIsMegaFilterActive] = useState(false);
  const [appliedMegaFilters, setAppliedMegaFilters] = useState(null);
  const [megaFilterRunId, setMegaFilterRunId] = useState(0);
  const categoriesQuery = useQuery({
    queryKey: CATEGORIES_QUERY_KEY,
    queryFn: getMyCategories,
    enabled: true,
  });
  const tasksQuery = useQuery({
    queryKey: MY_TASKS_QUERY_KEY,
    queryFn: getMyTasks,
    enabled: activeView === VIEW_MINE,
  });
  const assignedGroupTasksQuery = useQuery({
    queryKey: ["tasks", "assigned-group"],
    queryFn: () => getAssignedGroupTasks(),
    enabled: activeView === VIEW_MINE,
  });
  const recommendedTasksQuery = useQuery({
    queryKey: RECOMMENDED_TASKS_QUERY_KEY,
    queryFn: getRecommendedTasks,
    enabled: activeView === VIEW_SMART,
  });
  const groupsQuery = useQuery({
    queryKey: GROUPS_QUERY_KEY,
    queryFn: getMyGroups,
    enabled: true,
  });
  const savedAdvancedFilterQuery = useQuery({
    queryKey: ["tasks", "saved-advanced-filter"],
    queryFn: getSavedAdvancedFilter,
    retry: false,
  });
  const combinedFilterQuery = useQuery({
    queryKey: ["tasks", "combined-filter", appliedMegaFilters, megaFilterRunId],
    queryFn: () => filterCombinedTasks(appliedMegaFilters),
    enabled: isMegaFilterActive && Boolean(appliedMegaFilters),
  });
  const saveAdvancedFilterMutation = useMutation({
    mutationFn: saveAdvancedFilter,
    onError: () => {
      toast.error("No se pudo guardar el filtro.");
    },
  });
  const completeTaskMutation = useMutation({
    mutationFn: (task) => completeTask(task.idTarea),
    onSuccess: (_data, task) => {
      const isGroupTask = task?.origenTarea === "GRUPO";
      toast.success(isGroupTask ? "Tarea entregada." : "Tarea completada.");
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: RECOMMENDED_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["tasks", "assigned-group"] });
      queryClient.invalidateQueries({ queryKey: ["tasks", "combined-filter"] });
      if (isGroupTask && task.idGrupoOrigen) {
        queryClient.invalidateQueries({
          queryKey: ["groups", task.idGrupoOrigen, "assigned-tasks"],
        });
        queryClient.invalidateQueries({
          queryKey: ["groups", task.idGrupoOrigen, "assignments"],
        });
        queryClient.invalidateQueries({
          queryKey: ["groups", task.idGrupoOrigen, "assignments"],
          exact: false,
        });
      }
    },
    onError: (error, task) => {
      const isGroupTask = task?.origenTarea === "GRUPO";
      toast.error(
        getActionErrorMessage(
          error,
          isGroupTask ? "No se pudo entregar la tarea." : "No se pudo completar la tarea."
        )
      );
    },
  });
  const createTaskMutation = useMutation({
    mutationFn: createTask,
    onSuccess: () => {
      toast.success("Tarea creada.");
      setTaskModalOpen(false);
      setCategoryToSelectInTask(null);
      queryClient.invalidateQueries({ queryKey: MY_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: RECOMMENDED_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["tasks", "combined-filter"] });
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
      queryClient.invalidateQueries({ queryKey: RECOMMENDED_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["tasks", "combined-filter"] });
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
      queryClient.invalidateQueries({ queryKey: RECOMMENDED_TASKS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["tasks", "combined-filter"] });
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
  const createGroupMutation = useMutation({
    mutationFn: createGroup,
    onSuccess: () => {
      toast.success("Grupo creado.");
      setGroupModalOpen(false);
      setGroupModalMode("create");
      setGroupToEdit(null);
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo crear el grupo.");
    },
  });
  const updateGroupMutation = useMutation({
    mutationFn: ({ id, payload }) => updateGroup(id, payload),
    onSuccess: () => {
      toast.success("Grupo actualizado.");
      setGroupModalOpen(false);
      setGroupModalMode("create");
      setGroupToEdit(null);
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo actualizar el grupo.");
    },
  });
  const toggleGroupActiveMutation = useMutation({
    mutationFn: ({ group }) => toggleGroupActive(group.idGrupo, !group.activo),
    onSuccess: (_data, variables) => {
      toast.success(variables.group.activo ? "Grupo inactivado." : "Grupo activado.");
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo cambiar el estado del grupo.");
    },
  });
  const deleteGroupMutation = useMutation({
    mutationFn: ({ group }) => deleteGroup(group.idGrupo),
    onSuccess: () => {
      toast.success("Grupo eliminado.");
      setGroupToDelete(null);
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo eliminar el grupo.");
    },
  });
  const leaveGroupMutation = useMutation({
    mutationFn: ({ group }) => leaveGroup(group.idGrupo),
    onSuccess: () => {
      toast.success("Has salido del grupo.");
      setGroupToLeave(null);
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo salir del grupo.");
    },
  });
  const joinGroupMutation = useMutation({
    mutationFn: joinGroup,
    onSuccess: () => {
      toast.success("Te has unido al grupo.");
      setJoinGroupModalOpen(false);
      queryClient.invalidateQueries({ queryKey: GROUPS_QUERY_KEY });
    },
    onError: (error) => {
      toast.error(error?.response?.data?.error || "No se pudo unir al grupo.");
    },
  });

  const categories = categoriesQuery.data ?? [];
  const tasks = sortMyTasks(
    mapTaskResponsesToCardTasks(
      tasksQuery.data ?? [],
      categories,
      assignedGroupTasksQuery.data ?? []
    )
  );
  const recommendedTasks = mapRecommendedTaskResponsesToCardTasks(
    recommendedTasksQuery.data ?? [],
    categories
  );
  const megaFilteredTasks = mapRecommendedTaskResponsesToCardTasks(
    combinedFilterQuery.data ?? [],
    categories
  );
  const filteredTasks = filterTasksByQuickSearch(tasks, quickSearch);
  const filteredRecommendedTasks = filterTasksByQuickSearch(recommendedTasks, quickSearch);
  const filteredMegaTasks = filterTasksByQuickSearch(megaFilteredTasks, quickSearch);
  const groups = groupsQuery.data ?? [];
  const showQuickTaskSearch =
    isMegaFilterActive || activeView === VIEW_MINE || activeView === VIEW_SMART;
  const visibleActiveView = isMegaFilterActive ? null : activeView;
  const headerActionLabel = isMegaFilterActive
    ? "Nueva tarea"
    : activeView === VIEW_CATEGORIES
      ? "Nueva categoría"
      : activeView === VIEW_GROUPS
        ? "Nuevo grupo"
        : "Nueva tarea";
  const pageTitle = isMegaFilterActive
    ? "Resultados filtrados"
    : viewTitles[activeView] || "Tareas";

  useEffect(() => {
    if (!savedAdvancedFilterQuery.isSuccess) {
      return;
    }

    setMegaFilters(normalizeSavedMegaFilters(savedAdvancedFilterQuery.data));
  }, [savedAdvancedFilterQuery.data, savedAdvancedFilterQuery.isSuccess]);

  function handleApplyMegaFilters() {
    const filterPayload = buildCombinedFilterPayload(megaFilters);

    setAppliedMegaFilters(filterPayload);
    setIsMegaFilterActive(true);
    setFocusArea("filter");
    setMegaFilterRunId((current) => current + 1);
    saveAdvancedFilterMutation.mutate(buildSavedAdvancedFilterPayload(megaFilters));
  }

  function handleClearMegaFilters() {
    const defaultFilters = getDefaultMegaFilters();

    setMegaFilters(defaultFilters);
    setAppliedMegaFilters(null);
    setIsMegaFilterActive(false);
    setActiveView(VIEW_MINE);
    saveAdvancedFilterMutation.mutate(buildSavedAdvancedFilterPayload(defaultFilters));
  }

  function handleViewChange(view) {
    setIsMegaFilterActive(false);
    setActiveView(view);
  }

  function handleHeaderAction() {
    if (isMegaFilterActive) {
      setCategoryToSelectInTask(null);
      setTaskModalMode("create");
      setTaskToEdit(null);
      setTaskModalOpen(true);
      return;
    }

    if (activeView === VIEW_CATEGORIES) {
      setCategoryModalMode("create");
      setCategoryModalSource("categories");
      setCategoryToEdit(null);
      setCategoryModalOpen(true);
      return;
    }

    if (activeView === VIEW_GROUPS) {
      setGroupModalMode("create");
      setGroupToEdit(null);
      setGroupModalOpen(true);
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

  function handleCloseGroupModal() {
    if (createGroupMutation.isPending || updateGroupMutation.isPending) {
      return;
    }

    setGroupModalOpen(false);
    setGroupModalMode("create");
    setGroupToEdit(null);
  }

  function handleCloseJoinGroupModal() {
    if (joinGroupMutation.isPending) {
      return;
    }

    setJoinGroupModalOpen(false);
  }

  function handleSubmitGroup(payload) {
    if (groupModalMode === "edit" && groupToEdit) {
      return updateGroupMutation.mutateAsync({
        id: groupToEdit.idGrupo,
        payload,
      });
    }

    return createGroupMutation.mutateAsync(payload);
  }

  function handleSubmitJoinGroup(payload) {
    return joinGroupMutation.mutateAsync(payload);
  }

  function handleViewGroupMembers(group) {
    setGroupToViewMembers(group);
  }

  function handleOpenGroupAssignments(group, mode) {
    setGroupToViewAssignments(group);
    setGroupAssignmentsMode(mode);
  }

  function handleEditGroup(group) {
    setGroupModalMode("edit");
    setGroupToEdit(group);
    setGroupModalOpen(true);
  }

  function handleToggleGroupActive(group) {
    if (toggleGroupActiveMutation.isPending) {
      return;
    }

    toggleGroupActiveMutation.mutate({ group });
  }

  function handleConfirmDeleteGroup() {
    if (!groupToDelete || deleteGroupMutation.isPending) {
      return;
    }

    deleteGroupMutation.mutate({ group: groupToDelete });
  }

  function handleConfirmLeaveGroup() {
    if (!groupToLeave || leaveGroupMutation.isPending) {
      return;
    }

    leaveGroupMutation.mutate({ group: groupToLeave });
  }

  const handleMembersLoaded = useCallback((groupId, members) => {
    const currentMembership = members.find(
      (member) => String(member.idUsuario) === String(user?.idUsuario)
    );

    if (!currentMembership?.rol) {
      return;
    }

    setGroupRoles((current) => {
      if (current[groupId] === currentMembership.rol) {
        return current;
      }

      return {
        ...current,
        [groupId]: currentMembership.rol,
      };
    });
  }, [user?.idUsuario]);

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
        <MegaFilterBar
          categories={categories}
          categoriesLoading={categoriesQuery.isLoading}
          filters={megaFilters}
          groups={groups}
          groupsLoading={groupsQuery.isLoading}
          isApplying={combinedFilterQuery.isFetching}
          onApply={handleApplyMegaFilters}
          onClear={handleClearMegaFilters}
          onFiltersChange={setMegaFilters}
          onFocus={() => setFocusArea("filter")}
        />
      }
      secondaryBar={
        <ViewActionsBar
          activeView={visibleActiveView}
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
          onViewChange={handleViewChange}
        />
      }
      sidebar={
        <RightSidebar
          activeView={visibleActiveView}
          dimmed={focusArea === "filter"}
          onFocus={() => setFocusArea("sidebar")}
          onViewChange={handleViewChange}
        />
      }
    >
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h1 className="mt-1 text-2xl font-semibold text-primary">{pageTitle}</h1>
        </div>
        <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row">
          {!isMegaFilterActive && activeView === VIEW_GROUPS && (
            <Button
              className="w-full sm:w-auto"
              onClick={() => setJoinGroupModalOpen(true)}
              variant="secondary"
            >
              <UserPlus size={17} />
              Unirse por código
            </Button>
          )}
          {showQuickTaskSearch && (
            <QuickTaskSearch value={quickSearch} onChange={setQuickSearch} />
          )}
          <Button className="w-full sm:w-auto" onClick={handleHeaderAction}>
            <Plus size={17} />
            {headerActionLabel}
          </Button>
        </div>
      </div>
      {isMegaFilterActive && (
        <>
          {combinedFilterQuery.isFetching && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Aplicando filtros...
            </p>
          )}
          {combinedFilterQuery.isError && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron aplicar los filtros.
            </p>
          )}
          {combinedFilterQuery.isSuccess && megaFilteredTasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              No hay tareas que coincidan con los filtros.
            </p>
          )}
          {megaFilteredTasks.length > 0 && filteredMegaTasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              No hay tareas que coincidan con la bÃºsqueda.
            </p>
          )}
          {filteredMegaTasks.length > 0 && (
            <TaskList
              completingTaskId={completeTaskMutation.variables?.idTarea}
              deletingTaskId={deleteTaskMutation.variables?.task?.idTarea}
              isCompleting={completeTaskMutation.isPending}
              isDeleting={deleteTaskMutation.isPending}
              onCompleteTask={(task) => completeTaskMutation.mutate(task)}
              onDeleteTask={handleDeleteTask}
              onEditTask={handleEditTask}
              tasks={filteredMegaTasks}
            />
          )}
        </>
      )}
      {!isMegaFilterActive && activeView === VIEW_MINE && (
        <>
          {(tasksQuery.isLoading || assignedGroupTasksQuery.isLoading) && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Cargando tareas...
            </p>
          )}
          {(tasksQuery.isError || assignedGroupTasksQuery.isError) && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron cargar tus tareas.
            </p>
          )}
          {tasksQuery.isSuccess && assignedGroupTasksQuery.isSuccess && tasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Todavia no tienes tareas.
            </p>
          )}
          {tasks.length > 0 && filteredTasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              No hay tareas que coincidan con la búsqueda.
            </p>
          )}
          {filteredTasks.length > 0 && (
            <TaskList
              completingTaskId={completeTaskMutation.variables?.idTarea}
              deletingTaskId={deleteTaskMutation.variables?.task?.idTarea}
              isDeleting={deleteTaskMutation.isPending}
              isCompleting={completeTaskMutation.isPending}
              onCompleteTask={(task) => completeTaskMutation.mutate(task)}
              onDeleteTask={handleDeleteTask}
              onEditTask={handleEditTask}
              tasks={filteredTasks}
            />
          )}
        </>
      )}
      {!isMegaFilterActive && activeView === VIEW_CATEGORIES && (
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
      {!isMegaFilterActive && activeView === VIEW_GROUPS && (
        <>
          {groupsQuery.isLoading && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Cargando grupos...
            </p>
          )}
          {groupsQuery.isError && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron cargar los grupos.
            </p>
          )}
          {groupsQuery.isSuccess && groups.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Todavía no perteneces a ningún grupo.
            </p>
          )}
          {groups.length > 0 && (
            <GroupGrid
              currentUserId={user?.idUsuario}
              groupRoles={groupRoles}
              groups={groups}
              onDelete={setGroupToDelete}
              onEdit={handleEditGroup}
              onInvite={setGroupToInvite}
              onLeave={setGroupToLeave}
              onOpenAssignments={handleOpenGroupAssignments}
              onToggleActive={handleToggleGroupActive}
              onViewMembers={handleViewGroupMembers}
            />
          )}
        </>
      )}
      {!isMegaFilterActive && activeView === VIEW_SMART && (
        <>
          {recommendedTasksQuery.isLoading && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Cargando recomendaciones...
            </p>
          )}
          {recommendedTasksQuery.isError && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--state-danger-bg)] px-4 py-3 text-sm text-[color:var(--state-danger-text)]">
              No se pudieron cargar las recomendaciones.
            </p>
          )}
          {recommendedTasksQuery.isSuccess && recommendedTasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              Todavía no hay tareas recomendadas.
            </p>
          )}
          {recommendedTasks.length > 0 && filteredRecommendedTasks.length === 0 && (
            <p className="mt-5 rounded-control border border-app bg-[color:var(--color-surface-card-muted)] px-4 py-3 text-sm text-secondary">
              No hay tareas que coincidan con la búsqueda.
            </p>
          )}
          {filteredRecommendedTasks.length > 0 && (
            <TaskList
              completingTaskId={completeTaskMutation.variables?.idTarea}
              deletingTaskId={deleteTaskMutation.variables?.task?.idTarea}
              isCompleting={completeTaskMutation.isPending}
              isDeleting={deleteTaskMutation.isPending}
              onCompleteTask={(task) => completeTaskMutation.mutate(task)}
              onDeleteTask={handleDeleteTask}
              onEditTask={handleEditTask}
              tasks={filteredRecommendedTasks}
            />
          )}
        </>
      )}
      {!isMegaFilterActive &&
        activeView !== VIEW_MINE &&
        activeView !== VIEW_CATEGORIES &&
        activeView !== VIEW_GROUPS &&
        activeView !== VIEW_SMART && (
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
      <GroupFormModal
        creating={createGroupMutation.isPending || updateGroupMutation.isPending}
        initialGroup={groupModalMode === "edit" ? groupToEdit : null}
        mode={groupModalMode}
        onClose={handleCloseGroupModal}
        onSubmit={handleSubmitGroup}
        open={groupModalOpen}
      />
      <JoinGroupModal
        joining={joinGroupMutation.isPending}
        onClose={handleCloseJoinGroupModal}
        onSubmit={handleSubmitJoinGroup}
        open={joinGroupModalOpen}
      />
      <GroupMembersModal
        currentUserId={user?.idUsuario}
        group={groupToViewMembers}
        onClose={() => setGroupToViewMembers(null)}
        onMembersLoaded={handleMembersLoaded}
        open={Boolean(groupToViewMembers)}
      />
      <GroupAssignmentsModal
        group={groupToViewAssignments}
        mode={groupAssignmentsMode}
        onClose={() => setGroupToViewAssignments(null)}
        open={Boolean(groupToViewAssignments)}
      />
      <InvitationCodeModal
        canRegenerate={
          groupToInvite?.creadorActual === true ||
          (groupToInvite?.idCreador != null &&
            user?.idUsuario != null &&
            String(groupToInvite.idCreador) === String(user.idUsuario))
        }
        group={groupToInvite}
        onClose={() => setGroupToInvite(null)}
        open={Boolean(groupToInvite)}
      />
      <DeleteGroupModal
        deleting={deleteGroupMutation.isPending}
        group={groupToDelete}
        onClose={() => {
          if (!deleteGroupMutation.isPending) {
            setGroupToDelete(null);
          }
        }}
        onConfirm={handleConfirmDeleteGroup}
        open={Boolean(groupToDelete)}
      />
      <LeaveGroupModal
        group={groupToLeave}
        leaving={leaveGroupMutation.isPending}
        onClose={() => {
          if (!leaveGroupMutation.isPending) {
            setGroupToLeave(null);
          }
        }}
        onConfirm={handleConfirmLeaveGroup}
        open={Boolean(groupToLeave)}
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

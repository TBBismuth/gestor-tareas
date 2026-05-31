import { Brain, FolderKanban, Layers3, ListTodo, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { cn } from "../../lib/cn";
import { useAuth } from "../../features/auth/AuthContext.jsx";
import Button from "../ui/Button.jsx";
import IconButton from "../ui/IconButton.jsx";

const viewActions = [
  { id: "mine", label: "Mis tareas", icon: ListTodo },
  { id: "categories", label: "Mis categorias", icon: FolderKanban },
  { id: "groups", label: "Mis grupos", icon: Layers3 },
  { id: "smart", label: "Inteligente", icon: Brain },
];

export default function ViewActions({
  activeView = "mine",
  orientation = "vertical",
  className,
  onAction,
  onFocus,
  onViewChange,
}) {
  const isHorizontal = orientation === "horizontal";
  const navigate = useNavigate();
  const { logout } = useAuth();

  function handleViewClick(id) {
    onFocus?.();
    onViewChange?.(id);
    onAction?.();
  }

  function handleLogout() {
    onFocus?.();
    logout();
    onAction?.();
    toast.success("Sesion cerrada.");
    navigate("/login", { replace: true });
  }

  return (
    <div className={cn(isHorizontal ? "flex flex-wrap items-center gap-2" : "grid gap-2", className)}>
      {viewActions.map(({ id, label, icon: Icon }) => (
        <Button
          key={id}
          variant={activeView === id ? "primary" : "secondary"}
          aria-pressed={activeView === id}
          className={cn("shrink-0", isHorizontal ? "justify-center whitespace-nowrap" : "justify-start")}
          onClick={() => handleViewClick(id)}
        >
          <Icon size={17} />
          {label}
        </Button>
      ))}
      {isHorizontal ? (
        <IconButton
          label="Cerrar sesión"
          className="danger-icon-button shrink-0"
          onClick={handleLogout}
        >
          <LogOut size={17} />
        </IconButton>
      ) : (
        <Button
          aria-label="Cerrar sesión"
          className="danger-icon-button mt-auto shrink-0 justify-start"
          onClick={handleLogout}
          title="Cerrar sesión"
          variant="secondary"
        >
          <LogOut size={17} />
          Cerrar sesión
        </Button>
      )}
    </div>
  );
}

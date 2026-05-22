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
  onViewChange,
}) {
  const isHorizontal = orientation === "horizontal";
  const navigate = useNavigate();
  const { logout } = useAuth();

  function handleLogout() {
    logout();
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
          onClick={() => onViewChange?.(id)}
        >
          <Icon size={17} />
          {label}
        </Button>
      ))}
      <IconButton
        label="Cerrar sesion"
        className={cn("danger-icon-button shrink-0", !isHorizontal && "mt-auto")}
        onClick={handleLogout}
      >
        <LogOut size={17} />
      </IconButton>
    </div>
  );
}

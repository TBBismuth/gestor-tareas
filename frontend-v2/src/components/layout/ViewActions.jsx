import { Brain, FolderKanban, Layers3, ListTodo, LogOut } from "lucide-react";
import { cn } from "../../lib/cn";
import Button from "../ui/Button.jsx";
import IconButton from "../ui/IconButton.jsx";

const viewActions = [
  { label: "Mis tareas", icon: ListTodo },
  { label: "Mis categorias", icon: FolderKanban },
  { label: "Mis grupos", icon: Layers3 },
  { label: "Inteligente", icon: Brain },
];

export default function ViewActions({ orientation = "vertical", className }) {
  const isHorizontal = orientation === "horizontal";

  return (
    <div className={cn(isHorizontal ? "flex flex-wrap items-center gap-2" : "grid gap-2", className)}>
      {viewActions.map(({ label, icon: Icon }) => (
        <Button
          key={label}
          variant="secondary"
          className={cn("shrink-0", isHorizontal ? "justify-center whitespace-nowrap" : "justify-start")}
        >
          <Icon size={17} />
          {label}
        </Button>
      ))}
      <IconButton
        label="Cerrar sesion"
        className={cn("danger-icon-button shrink-0", !isHorizontal && "mt-auto")}
      >
        <LogOut size={17} />
      </IconButton>
    </div>
  );
}

import { Brain, FolderKanban, Layers3, ListTodo, LogOut } from "lucide-react";
import Button from "../ui/Button.jsx";

const navItems = [
  { label: "Mis tareas", icon: ListTodo },
  { label: "Mis categorias", icon: FolderKanban },
  { label: "Mis grupos", icon: Layers3 },
  { label: "Inteligente", icon: Brain },
];

export default function RightSidebar({ onFocus }) {
  return (
    <div
      className="flex max-h-[inherit] flex-col overflow-hidden rounded-panel border border-app bg-panel p-4 shadow-panel backdrop-blur"
      onMouseEnter={onFocus}
      onFocus={onFocus}
    >
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-muted">Vistas</p>
        <nav className="mt-4 grid gap-2">
          {navItems.map(({ label, icon: Icon }) => (
            <Button key={label} variant="secondary" className="justify-start">
              <Icon size={17} />
              {label}
            </Button>
          ))}
        </nav>
      </div>
      <Button variant="ghost" className="mt-auto justify-start">
        <LogOut size={17} />
        Cerrar sesion
      </Button>
    </div>
  );
}

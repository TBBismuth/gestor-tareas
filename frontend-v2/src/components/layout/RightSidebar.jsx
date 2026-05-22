import { Brain, FolderKanban, Layers3, ListTodo, LogOut, RotateCcw } from "lucide-react";
import { useDraggablePanel } from "../../lib/useDraggablePanel";
import { cn } from "../../lib/cn";
import Button from "../ui/Button.jsx";
import IconButton from "../ui/IconButton.jsx";

const SIDEBAR_POSITION_KEY = "gestor-tareas.frontend-v2.sidebar-position";

const navItems = [
  { label: "Mis tareas", icon: ListTodo },
  { label: "Mis categorias", icon: FolderKanban },
  { label: "Mis grupos", icon: Layers3 },
  { label: "Inteligente", icon: Brain },
];

export default function RightSidebar({ dimmed = false, onFocus }) {
  const { panelRef, position, isDragging, dragHandleProps, resetPosition } =
    useDraggablePanel(SIDEBAR_POSITION_KEY);

  return (
    <div
      ref={panelRef}
      className="floating-sidebar"
      style={
        position
          ? { left: `${position.x}px`, top: `${position.y}px` }
          : { visibility: "hidden" }
      }
      onPointerDownCapture={onFocus}
    >
      <div
        className={cn(
          "flex max-h-[inherit] flex-col overflow-hidden rounded-panel border border-app bg-panel p-4 shadow-panel backdrop-blur transition duration-200",
          dimmed && "opacity-55 saturate-50"
        )}
      >
      <div>
        <div
          className="floating-sidebar-handle flex items-center justify-between gap-2"
          data-dragging={isDragging}
          {...dragHandleProps}
        >
          <p className="text-xs font-semibold uppercase tracking-wide text-muted">Vistas</p>
          <IconButton
            label="Resetear posicion del panel"
            className="size-8"
            onPointerDown={(event) => event.stopPropagation()}
            onClick={resetPosition}
          >
            <RotateCcw size={15} />
          </IconButton>
        </div>
        {/* Mobile queda pendiente para una version tipo drawer/bottom sheet. */}
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
    </div>
  );
}

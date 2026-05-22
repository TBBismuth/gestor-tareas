import { RotateCcw } from "lucide-react";
import { useDraggablePanel } from "../../lib/useDraggablePanel";
import { cn } from "../../lib/cn";
import IconButton from "../ui/IconButton.jsx";
import ViewActions from "./ViewActions.jsx";

const SIDEBAR_POSITION_KEY = "gestor-tareas.frontend-v2.sidebar-position";

export default function RightSidebar({
  activeView = "mine",
  dimmed = false,
  onFocus,
  onViewChange,
}) {
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
          <ViewActions
            activeView={activeView}
            className="mt-4"
            onViewChange={onViewChange}
          />
        </div>
      </div>
    </div>
  );
}

import { useCallback, useEffect, useState } from "react";
import { ListTodo, RotateCcw, X } from "lucide-react";
import { useDraggablePanel } from "../../lib/useDraggablePanel";
import { cn } from "../../lib/cn";
import IconButton from "../ui/IconButton.jsx";
import ViewActions from "./ViewActions.jsx";

const MOBILE_VIEWS_POSITION_KEY = "gestor-tareas.frontend-v2.mobile-views-position";

export default function ViewActionsBar({
  activeView = "mine",
  dimmed = false,
  onFocus,
  onViewChange,
}) {
  const [open, setOpen] = useState(false);
  const getDefaultMobilePosition = useCallback((node) => {
    const rect = node?.getBoundingClientRect();
    const height = rect?.height || 56;
    const margin = 16;

    return {
      x: margin,
      y: window.innerHeight - height - 96,
    };
  }, []);
  const { panelRef, position, isDragging, dragHandleProps, resetPosition } =
    useDraggablePanel(MOBILE_VIEWS_POSITION_KEY, {
      defaultPosition: getDefaultMobilePosition,
      dragThreshold: 8,
      margin: 16,
    });

  useEffect(() => {
    if (!open) return undefined;

    function handlePointerDown(event) {
      if (isDragging || panelRef.current?.contains(event.target)) {
        return;
      }

      setOpen(false);
    }

    document.addEventListener("pointerdown", handlePointerDown);
    return () => document.removeEventListener("pointerdown", handlePointerDown);
  }, [isDragging, open, panelRef]);

  return (
    <div
      ref={panelRef}
      className="floating-mobile-views lg:hidden"
      data-open={open}
      style={
        position
          ? { left: `${position.x}px`, top: `${position.y}px` }
          : { visibility: "hidden" }
      }
    >
      {!open ? (
        <button
          type="button"
          className={cn(
            "floating-mobile-views-bubble shadow-panel",
            dimmed && "opacity-70 saturate-75"
          )}
          aria-label="Abrir vistas"
          data-dragging={isDragging}
          onClick={() => {
            onFocus?.();
            setOpen(true);
          }}
          {...dragHandleProps}
        >
          <ListTodo size={21} />
        </button>
      ) : (
        <section
          className={cn(
            "floating-mobile-views-card rounded-panel border border-app bg-panel p-3 shadow-panel",
            dimmed && "opacity-90 saturate-75"
          )}
        >
          <div
            className="floating-mobile-views-handle flex items-center justify-between gap-2"
            data-dragging={isDragging}
            {...dragHandleProps}
          >
            <p className="text-xs font-semibold uppercase tracking-wide text-muted">Vistas</p>
            <div className="flex items-center gap-1">
              <IconButton
                label="Resetear posicion del panel"
                className="size-8"
                onPointerDown={(event) => event.stopPropagation()}
                onClick={resetPosition}
              >
                <RotateCcw size={15} />
              </IconButton>
              <IconButton
                label="Cerrar panel de vistas"
                className="size-8"
                onPointerDown={(event) => event.stopPropagation()}
                onClick={() => setOpen(false)}
              >
                <X size={15} />
              </IconButton>
            </div>
          </div>
          <ViewActions
            activeView={activeView}
            className="mt-3"
            onAction={() => setOpen(false)}
            onFocus={onFocus}
            onViewChange={onViewChange}
          />
        </section>
      )}
    </div>
  );
}

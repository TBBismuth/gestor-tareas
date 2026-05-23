import { cn } from "../../lib/cn";
import ViewActions from "./ViewActions.jsx";

export default function ViewActionsBar({
  activeView = "mine",
  dimmed = false,
  onFocus,
  onViewChange,
}) {
  return (
    <section
      className={cn(
        "rounded-panel border border-app bg-panel p-3 shadow-panel transition duration-200 lg:hidden",
        dimmed && "opacity-55 saturate-50"
      )}
    >
      <div className="flex flex-wrap items-center gap-3">
        <p className="shrink-0 text-xs font-semibold uppercase tracking-wide text-muted">
          Vistas
        </p>
        <ViewActions
          activeView={activeView}
          onFocus={onFocus}
          onViewChange={onViewChange}
          orientation="horizontal"
        />
      </div>
      {/* TODO mobile: convertir esta barra en drawer/bottom sheet cuando se disene la navegacion movil completa. */}
    </section>
  );
}

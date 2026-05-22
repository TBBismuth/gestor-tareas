import { CalendarDays, Clock3, Search, SlidersHorizontal, Tag } from "lucide-react";
import Button from "../../../components/ui/Button.jsx";

const filters = [
  { label: "Prioridad", icon: SlidersHorizontal, active: true },
  { label: "Estado", icon: Tag, active: false },
  { label: "Tiempo maximo", icon: Clock3, active: false },
  { label: "Fecha", icon: CalendarDays, active: false },
];

export default function MegaFilterBar({ onFocus }) {
  return (
    <header
      className="rounded-panel border border-app bg-panel p-4 shadow-panel"
      onClick={onFocus}
    >
      <div className="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wide text-muted">
            Megafiltro
          </p>
          <h2 className="mt-1 text-lg font-semibold text-primary">
            Filtros avanzados de tareas
          </h2>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          {filters.map(({ label, icon: Icon, active }) => (
            <Button
              key={label}
              variant={active ? "primary" : "secondary"}
              size="sm"
              aria-pressed={active}
            >
              <Icon size={16} />
              {label}
            </Button>
          ))}
          <div className="flex min-h-9 min-w-64 items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary">
            <Search size={16} />
            <span className="text-sm text-muted">Buscar palabras clave...</span>
          </div>
        </div>
      </div>
    </header>
  );
}

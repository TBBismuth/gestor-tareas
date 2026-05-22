import { CalendarDays, Clock3, Moon, Search, SlidersHorizontal, Sun, Tag } from "lucide-react";
import { useTheme } from "../../../app/theme.jsx";
import IconButton from "../../../components/ui/IconButton.jsx";
import Button from "../../../components/ui/Button.jsx";

const filters = [
  { label: "Prioridad", icon: SlidersHorizontal, active: true },
  { label: "Estado", icon: Tag, active: false },
  { label: "Tiempo maximo", icon: Clock3, active: false },
  { label: "Fecha", icon: CalendarDays, active: false },
];

export default function MegaFilterBar({ onFocus }) {
  const { theme, toggleTheme } = useTheme();
  const ThemeIcon = theme === "dark" ? Sun : Moon;

  return (
    <header
      className="rounded-panel border border-app bg-panel p-4 shadow-panel"
      onClick={onFocus}
    >
      <div className="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
        <div className="flex items-start justify-between gap-3">
          <div>
          <h2 className="mt-1 text-lg font-semibold text-primary">
            Filtros avanzados de tareas
          </h2>
          </div>
          <IconButton
            label="Cambiar tema"
            className="xl:hidden"
            onClick={(event) => {
              event.stopPropagation();
              toggleTheme();
            }}
          >
            <ThemeIcon size={18} />
          </IconButton>
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
          <IconButton
            label="Cambiar tema"
            className="hidden xl:inline-grid"
            onClick={(event) => {
              event.stopPropagation();
              toggleTheme();
            }}
          >
            <ThemeIcon size={18} />
          </IconButton>
        </div>
      </div>
    </header>
  );
}

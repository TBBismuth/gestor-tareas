import { useEffect, useRef, useState } from "react";
import {
  CalendarDays,
  ChevronDown,
  Clock3,
  Filter,
  Moon,
  RotateCcw,
  Search,
  Sun,
  X,
} from "lucide-react";
import { useTheme } from "../../../app/theme.jsx";
import Button from "../../../components/ui/Button.jsx";
import IconButton from "../../../components/ui/IconButton.jsx";
import { cn } from "../../../lib/cn.js";

const ORIGIN_OPTIONS = [
  { label: "Todos", value: "TODAS" },
  { label: "Personal", value: "PERSONAL" },
  { label: "Grupo", value: "GRUPO" },
];

const PRIORITY_OPTIONS = [
  { label: "Baja", value: "BAJA" },
  { label: "Media", value: "MEDIA" },
  { label: "Alta", value: "ALTA" },
  { label: "Imprescindible", value: "IMPRESCINDIBLE" },
];

const STATE_OPTIONS = [
  { label: "En curso", value: "EN_CURSO" },
  { label: "Vencida", value: "VENCIDA" },
  { label: "Sin fecha", value: "SIN_FECHA" },
  { label: "Completada", value: "COMPLETADA" },
  { label: "Con retraso", value: "COMPLETADA_CON_RETRASO" },
];

const SORT_OPTIONS = [
  { label: "Inteligente", value: "INTELIGENTE" },
  { label: "Fecha creacion", value: "FECHA_AGREGADO" },
  { label: "Fecha entrega", value: "FECHA_ENTREGA" },
  { label: "Prioridad", value: "PRIORIDAD" },
  { label: "Tiempo", value: "TIEMPO" },
];

const activeControlClassName =
  "border-[color:var(--color-brand)] bg-[color:var(--color-brand)] text-[color:var(--color-text-inverse)]";

const inactiveControlClassName =
  "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]";

const intelligentControlClassName =
  "border-amber-400 bg-amber-100 text-amber-950 hover:bg-amber-200 dark:border-amber-300 dark:bg-amber-400 dark:text-slate-950 dark:hover:bg-amber-300";

function FieldResetButton({ label, onClick }) {
  return (
    <IconButton
      label={label}
      className="danger-icon-button size-7 shrink-0 border-transparent bg-transparent"
      onClick={onClick}
    >
      <X size={14} />
    </IconButton>
  );
}

function FieldHeader({ label, resetLabel, showReset = false, onReset }) {
  return (
    <div className="mb-1 flex min-h-7 items-center justify-start gap-1.5">
      <span className="text-xs font-semibold uppercase text-muted">{label}</span>
      {showReset && <FieldResetButton label={resetLabel} onClick={onReset} />}
    </div>
  );
}

function useCompactDropdown(open, setOpen, ref) {
  useEffect(() => {
    if (!open) return undefined;

    function handlePointerDown(event) {
      if (!ref.current?.contains(event.target)) {
        setOpen(false);
      }
    }

    function handleKeyDown(event) {
      if (event.key === "Escape") {
        setOpen(false);
      }
    }

    document.addEventListener("pointerdown", handlePointerDown);
    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("pointerdown", handlePointerDown);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [open, ref, setOpen]);
}

function DropdownShell({ children, disabled = false, label, active, intelligent = false, summary }) {
  return (
    <button
      type="button"
      disabled={disabled}
      className={cn(
        "flex min-h-9 w-full items-center justify-between gap-2 rounded-control border px-3 text-sm font-semibold transition disabled:cursor-not-allowed disabled:bg-[color:var(--state-disabled-bg)] disabled:text-[color:var(--state-disabled-text)]",
        intelligent ? intelligentControlClassName : active ? activeControlClassName : inactiveControlClassName
      )}
      aria-haspopup="listbox"
      aria-label={label}
    >
      <span className="min-w-0 truncate">{summary}</span>
      <ChevronDown size={16} className="shrink-0" />
      {children}
    </button>
  );
}

function OptionChip({ children, intelligent = false, label, selected, onClick }) {
  return (
    <button
      type="button"
      aria-pressed={selected}
      onClick={onClick}
      className={cn(
        "inline-flex min-h-8 w-auto max-w-full items-center rounded-control border px-2.5 text-xs font-semibold transition",
        intelligent ? intelligentControlClassName : selected ? activeControlClassName : inactiveControlClassName,
        intelligent && selected && "ring-2 ring-amber-500/45"
      )}
    >
      {children || label}
    </button>
  );
}

function CompactSingleDropdown({
  active,
  disabled = false,
  intelligent = false,
  label,
  options,
  panelClassName = "min-w-56",
  renderOption,
  renderSummary,
  value,
  onChange,
}) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  useCompactDropdown(open, setOpen, ref);
  const selected = options.find((option) => option.value === value);
  const summary = renderSummary ? renderSummary(selected) : selected?.label || "Todos";

  return (
    <div className="relative min-w-0" ref={ref}>
      <FieldHeader label={label} />
      <div onClick={() => !disabled && setOpen((current) => !current)}>
        <DropdownShell
          label={label}
          active={active ?? Boolean(value)}
          disabled={disabled}
          intelligent={intelligent}
          summary={summary}
        />
      </div>
      {open && (
        <div className={cn("absolute left-0 top-full z-50 mt-2 w-full rounded-panel border border-app bg-panel p-2 shadow-panel", panelClassName)}>
          <div className="flex flex-wrap items-start gap-1.5">
            {options.map((option) => (
              <OptionChip
                key={option.value}
                label={option.label}
                intelligent={option.value === "INTELIGENTE"}
                selected={option.value === value}
                onClick={() => {
                  onChange(option.value);
                  setOpen(false);
                }}
              >
                {renderOption?.(option)}
              </OptionChip>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function CompactMultiDropdown({ label, options, values, onChange, resetLabel }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  useCompactDropdown(open, setOpen, ref);
  const selectedLabels = options
    .filter((option) => values.includes(option.value))
    .map((option) => option.label);
  const summary =
    selectedLabels.length === 0
      ? "Todos"
      : selectedLabels.length <= 2
        ? selectedLabels.join(", ")
        : `${selectedLabels.slice(0, 2).join(", ")} +${selectedLabels.length - 2}`;

  function toggleValue(value) {
    onChange(
      values.includes(value)
        ? values.filter((current) => current !== value)
        : [...values, value]
    );
  }

  return (
    <div className="relative min-w-0" ref={ref}>
      <FieldHeader
        label={label}
        resetLabel={resetLabel}
        showReset={values.length > 0}
        onReset={() => onChange([])}
      />
      <div onClick={() => setOpen((current) => !current)}>
        <DropdownShell label={label} active={values.length > 0} summary={summary} />
      </div>
      {open && (
        <div className="absolute left-0 top-full z-50 mt-2 w-full min-w-64 rounded-panel border border-app bg-panel p-2 shadow-panel">
          <div className="flex flex-wrap items-start gap-1.5">
            {options.map((option) => (
              <OptionChip
                key={option.value}
                label={option.label}
                selected={values.includes(option.value)}
                onClick={() => toggleValue(option.value)}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

function ChipVisual({ item, fallback }) {
  if (!item) return fallback;

  return (
    <span className="inline-flex max-w-48 min-w-0 items-center gap-1.5 whitespace-nowrap">
      {item.color && (
        <span
          className="size-2.5 shrink-0 rounded-full border border-app"
          style={{ backgroundColor: item.color }}
          aria-hidden="true"
        />
      )}
      {item.icono && <span className="shrink-0">{item.icono}</span>}
      <span className="min-w-0 truncate">{item.nombre}</span>
    </span>
  );
}

export default function MegaFilterBar({
  categories = [],
  categoriesLoading = false,
  filters,
  groups = [],
  groupsLoading = false,
  isApplying = false,
  onApply,
  onClear,
  onFiltersChange,
  onFocus,
}) {
  const [isFilterBarExpanded, setIsFilterBarExpanded] = useState(false);
  const { theme, toggleTheme } = useTheme();
  const ThemeIcon = theme === "dark" ? Sun : Moon;
  const groupDisabled = filters.origen === "PERSONAL";
  const categoryDisabled = filters.origen === "GRUPO";
  const exactDateDisabled = Boolean(filters.fechaEntregaHasta);
  const untilDateDisabled = Boolean(filters.fechaEntregaExacta);
  const groupOptions = [
    { group: null, label: groupsLoading ? "Cargando grupos..." : "Todos los grupos", value: "" },
    ...groups.map((group) => ({
      group,
      label: group.nombre,
      value: String(group.idGrupo),
    })),
  ];
  const categoryOptions = [
    {
      category: null,
      label: categoriesLoading ? "Cargando categorias..." : "Todas las categorias",
      value: "",
    },
    ...categories.map((category) => ({
      category,
      label: category.nombre,
      value: String(category.idCategoria),
    })),
  ];

  function updateFilter(patch) {
    onFiltersChange?.({
      ...filters,
      ...patch,
    });
  }

  function handleOriginChange(origen) {
    updateFilter({
      origen,
      idGrupo: origen === "PERSONAL" ? "" : filters.idGrupo,
      idCategoria: origen === "GRUPO" ? "" : filters.idCategoria,
    });
  }

  function handleGroupChange(idGrupo) {
    updateFilter({
      idGrupo,
      origen: idGrupo ? "GRUPO" : filters.origen,
      idCategoria: idGrupo ? "" : filters.idCategoria,
    });
  }

  function handleExactDateChange(fechaEntregaExacta) {
    updateFilter({
      fechaEntregaExacta,
      fechaEntregaHasta: fechaEntregaExacta ? "" : filters.fechaEntregaHasta,
    });
  }

  function handleUntilDateChange(fechaEntregaHasta) {
    updateFilter({
      fechaEntregaHasta,
      fechaEntregaExacta: fechaEntregaHasta ? "" : filters.fechaEntregaExacta,
    });
  }

  function handleTimeMaxChange(value) {
    updateFilter({ tiempoMax: value.replace(/\D/g, "") });
  }

  function handleSubmit(event) {
    event.preventDefault();
    onApply?.();
  }

  function toggleExpanded() {
    setIsFilterBarExpanded((current) => !current);
  }

  return (
    <header
      className="relative rounded-panel border border-app bg-panel px-4 pb-6 pt-4 shadow-panel"
      onClick={onFocus}
    >
      <div
        role="button"
        tabIndex={0}
        className="flex w-full items-start justify-between gap-3 text-left"
        aria-expanded={isFilterBarExpanded}
        onClick={toggleExpanded}
        onKeyDown={(event) => {
          if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            toggleExpanded();
          }
        }}
      >
        <div>
          <h2 className="mt-1 text-lg font-semibold text-primary">
            Filtros avanzados de tareas
          </h2>
        </div>
        <IconButton
          label="Cambiar tema"
          onClick={(event) => {
            event.stopPropagation();
            toggleTheme();
          }}
        >
          <ThemeIcon size={18} />
        </IconButton>
      </div>

      {isFilterBarExpanded && (
      <form
        className="mt-4 flex flex-col gap-4 transition"
        onClick={(event) => event.stopPropagation()}
        onSubmit={handleSubmit}
      >
        <div className="grid gap-3 xl:grid-cols-[minmax(160px,0.7fr)_minmax(190px,0.8fr)_minmax(210px,0.9fr)_minmax(230px,1fr)]">
          <CompactSingleDropdown
            label="Origen"
            options={ORIGIN_OPTIONS}
            value={filters.origen}
            onChange={handleOriginChange}
          />

          <div
            className={cn(
              "min-w-0 rounded-control transition",
              groupDisabled && "cursor-not-allowed opacity-45 saturate-50"
            )}
            aria-disabled={groupDisabled}
          >
            <CompactSingleDropdown
              active={Boolean(filters.idGrupo)}
              disabled={groupDisabled}
              label="Grupo"
              options={groupOptions}
              panelClassName="min-w-72"
              renderOption={(option) => (
                <ChipVisual item={option.group} fallback={option.label} />
              )}
              renderSummary={(option) => (
                <ChipVisual item={option?.group} fallback={option?.label || "Todos los grupos"} />
              )}
              value={groupDisabled ? "" : filters.idGrupo}
              onChange={handleGroupChange}
            />
          </div>

          <CompactMultiDropdown
            label="Prioridad"
            options={PRIORITY_OPTIONS}
            values={filters.prioridades}
            resetLabel="Limpiar prioridad"
            onChange={(prioridades) => updateFilter({ prioridades })}
          />

          <CompactMultiDropdown
            label="Estado"
            options={STATE_OPTIONS}
            values={filters.estados}
            resetLabel="Limpiar estado"
            onChange={(estados) => updateFilter({ estados })}
          />
        </div>

        <div className="grid gap-3 xl:grid-cols-[minmax(190px,0.8fr)_minmax(190px,0.8fr)_minmax(190px,0.8fr)_minmax(190px,0.8fr)]">
          <div
            className={cn(
              "min-w-0 rounded-control transition",
              categoryDisabled && "cursor-not-allowed opacity-45 saturate-50"
            )}
            aria-disabled={categoryDisabled}
          >
            <CompactSingleDropdown
              active={Boolean(filters.idCategoria)}
              disabled={categoryDisabled}
              label="Categoria"
              options={categoryOptions}
              panelClassName="min-w-72"
              renderOption={(option) => (
                <ChipVisual item={option.category} fallback={option.label} />
              )}
              renderSummary={(option) => (
                <ChipVisual item={option?.category} fallback={option?.label || "Todas las categorias"} />
              )}
              value={categoryDisabled ? "" : filters.idCategoria}
              onChange={(idCategoria) => updateFilter({ idCategoria })}
            />
          </div>

          <label
            className={cn(
              "min-w-0 rounded-control transition",
              exactDateDisabled && "cursor-not-allowed opacity-45 saturate-50"
            )}
            aria-disabled={exactDateDisabled}
          >
            <FieldHeader
              label="Fecha exacta"
              resetLabel="Limpiar fecha exacta"
              showReset={Boolean(filters.fechaEntregaExacta)}
              onReset={() => updateFilter({ fechaEntregaExacta: "" })}
            />
            <span className="flex min-h-9 items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary">
              <CalendarDays size={16} className="shrink-0 text-muted" />
              <input
                className="min-w-0 flex-1 bg-transparent text-sm text-primary outline-none disabled:cursor-not-allowed disabled:text-[color:var(--state-disabled-text)]"
                disabled={exactDateDisabled}
                onChange={(event) => handleExactDateChange(event.target.value)}
                type="date"
                value={exactDateDisabled ? "" : filters.fechaEntregaExacta}
              />
            </span>
          </label>

          <label
            className={cn(
              "min-w-0 rounded-control transition",
              untilDateDisabled && "cursor-not-allowed opacity-45 saturate-50"
            )}
            aria-disabled={untilDateDisabled}
          >
            <FieldHeader
              label="Hasta fecha"
              resetLabel="Limpiar hasta fecha"
              showReset={Boolean(filters.fechaEntregaHasta)}
              onReset={() => updateFilter({ fechaEntregaHasta: "" })}
            />
            <span className="flex min-h-9 items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary">
              <CalendarDays size={16} className="shrink-0 text-muted" />
              <input
                className="min-w-0 flex-1 bg-transparent text-sm text-primary outline-none disabled:cursor-not-allowed disabled:text-[color:var(--state-disabled-text)]"
                disabled={untilDateDisabled}
                onChange={(event) => handleUntilDateChange(event.target.value)}
                type="date"
                value={untilDateDisabled ? "" : filters.fechaEntregaHasta}
              />
            </span>
          </label>

          <CompactSingleDropdown
            active
            intelligent={filters.criterioOrdenActivo === "INTELIGENTE"}
            label="Orden"
            options={SORT_OPTIONS}
            panelClassName="min-w-64"
            value={filters.criterioOrdenActivo}
            onChange={(criterioOrdenActivo) => updateFilter({ criterioOrdenActivo })}
          />
        </div>

        <div className="grid gap-3 lg:grid-cols-[minmax(220px,1fr)_minmax(140px,0.4fr)_auto_auto] lg:items-end">
          <label className="min-w-0">
            <FieldHeader label="Palabras clave" />
            <span className="flex min-h-9 items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary">
              <Search size={16} className="shrink-0 text-muted" />
              <input
                className="min-w-0 flex-1 bg-transparent text-sm text-primary outline-none placeholder:text-muted"
                onChange={(event) => updateFilter({ palabrasClave: event.target.value })}
                placeholder="Titulo o descripcion"
                type="search"
                value={filters.palabrasClave}
              />
            </span>
          </label>

          <label className="min-w-0">
            <FieldHeader
              label="Tiempo max."
              resetLabel="Limpiar tiempo maximo"
              showReset={Boolean(filters.tiempoMax)}
              onReset={() => updateFilter({ tiempoMax: "" })}
            />
            <span className="flex min-h-9 items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary">
              <Clock3 size={16} className="shrink-0 text-muted" />
              <input
                className="min-w-0 flex-1 bg-transparent text-sm text-primary outline-none placeholder:text-muted"
                inputMode="numeric"
                onChange={(event) => handleTimeMaxChange(event.target.value)}
                pattern="[0-9]*"
                placeholder="Min"
                type="text"
                value={filters.tiempoMax}
              />
            </span>
          </label>

          <Button className="w-full lg:w-auto" disabled={isApplying} size="sm" type="submit">
            <Filter size={16} />
            {isApplying ? "Aplicando..." : "Aplicar filtros"}
          </Button>

          <Button
            className="w-full lg:w-auto"
            disabled={isApplying}
            onClick={onClear}
            size="sm"
            type="button"
            variant="secondary"
          >
            <RotateCcw size={16} />
            Limpiar
          </Button>
        </div>
      </form>
      )}

      <button
        type="button"
        className="absolute bottom-1 left-1/2 -translate-x-1/2 text-muted transition hover:text-primary"
        aria-label={isFilterBarExpanded ? "Contraer filtros avanzados" : "Expandir filtros avanzados"}
        aria-expanded={isFilterBarExpanded}
        onClick={(event) => {
          event.stopPropagation();
          toggleExpanded();
        }}
      >
        <ChevronDown
          size={22}
          className={cn("transition-transform duration-200", isFilterBarExpanded && "rotate-180")}
        />
      </button>
    </header>
  );
}

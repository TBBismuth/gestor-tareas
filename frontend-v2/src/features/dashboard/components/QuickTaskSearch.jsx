import { Search } from "lucide-react";

export default function QuickTaskSearch({ value, onChange }) {
  return (
    <label className="flex min-h-10 w-full items-center gap-2 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 text-secondary sm:w-72">
      <Search size={16} className="shrink-0 text-muted" />
      <input
        className="min-w-0 flex-1 bg-transparent text-sm text-primary outline-none placeholder:text-muted"
        onChange={(event) => onChange(event.target.value)}
        placeholder="Buscar tareas..."
        type="search"
        value={value}
      />
    </label>
  );
}

import { ChevronDown } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";

const defaultValues = {
  nombre: "",
  useColor: false,
  color: "#2563eb",
  icono: "",
};

const categoryIcons = [
  "",
  "💼",
  "🧳",
  "📁",
  "📌",
  "🗂️",
  "📅",
  "📝",
  "✅",
  "📊",
  "🧭",
  "🏠",
  "🛒",
  "🧹",
  "🧺",
  "🛠️",
  "🧑‍🍳",
  "🧘",
  "🏃",
  "💊",
  "🩺",
  "📚",
  "🧠",
  "🧪",
  "🖥️",
  "🎮",
  "🎧",
  "🎬",
  "⭐",
  "🔥",
  "⚡",
  "⏳",
  "🕒",
  "🔔",
];

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

function toCategoryPayload(values) {
  return {
    nombre: values.nombre.trim(),
    color: values.useColor ? values.color : null,
    icono: values.icono?.trim() || null,
  };
}

export default function CategoryFormModal({
  creating = false,
  onClose,
  onSubmit,
  open,
  zIndexClass,
}) {
  const [iconDropdownOpen, setIconDropdownOpen] = useState(false);
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm({ defaultValues });
  const useColor = watch("useColor");
  const color = watch("color");
  const selectedIcon = watch("icono");

  useEffect(() => {
    if (!open) {
      reset(defaultValues);
      setIconDropdownOpen(false);
    }
  }, [open, reset]);

  async function submit(values) {
    try {
      await onSubmit(toCategoryPayload(values));
      reset(defaultValues);
    } catch {
      // El contenedor muestra el mensaje de error de la mutacion.
    }
  }

  return (
    <Modal
      open={open}
      title="Nueva categoría"
      onClose={creating ? undefined : onClose}
      zIndexClass={zIndexClass}
    >
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="category-name">
            Nombre *
          </label>
          <input
            id="category-name"
            className="auth-input"
            placeholder="Nombre de la categoría"
            {...register("nombre", {
              required: "El nombre es obligatorio.",
              minLength: { value: 3, message: "El nombre debe tener al menos 3 caracteres." },
              maxLength: { value: 32, message: "El nombre no puede superar 32 caracteres." },
            })}
          />
          <FieldError error={errors.nombre} />
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="category-color">
            Color
          </label>
          <div className="flex flex-wrap items-center gap-3 rounded-control border border-app bg-[color:var(--state-inactive-bg)] px-3 py-2">
            <label className="inline-flex cursor-pointer items-center gap-3 text-sm font-semibold text-secondary">
              <input className="peer sr-only" type="checkbox" {...register("useColor")} />
              <span className="category-color-switch relative h-6 w-11 rounded-full border border-app bg-[color:var(--state-disabled-bg)] transition peer-checked:border-[color:var(--color-brand)] peer-checked:bg-[color:var(--color-brand)]">
                <span className="absolute left-0.5 top-0.5 size-5 rounded-full bg-[color:var(--color-bg-elevated)] shadow-control transition" />
              </span>
              Color personalizado
            </label>
            {useColor && (
              <div className="flex flex-wrap items-center gap-2">
                <input
                  id="category-color"
                  className="h-10 w-16 rounded-control border border-app bg-[color:var(--state-inactive-bg)] p-1"
                  type="color"
                  {...register("color")}
                />
                <span className="inline-flex items-center gap-2 text-xs font-semibold text-secondary">
                  <span
                    className="size-3 rounded-full border border-app"
                    style={{ background: color }}
                    aria-hidden="true"
                  />
                  {color}
                </span>
              </div>
            )}
          </div>
        </div>

        <div className="grid gap-2">
          <p className="text-sm font-semibold text-primary" id="category-icon-label">
            Icono
          </p>
          <input type="hidden" {...register("icono")} />
          <div className="relative">
            <button
              type="button"
              aria-expanded={iconDropdownOpen}
              aria-labelledby="category-icon-label"
              className="auth-input flex items-center justify-between gap-3 text-left"
              onClick={() => setIconDropdownOpen((current) => !current)}
            >
              <span className="inline-flex items-center gap-2">
                <span className="grid size-8 place-items-center rounded-control border border-app bg-[color:var(--state-inactive-bg)] text-lg">
                  {selectedIcon || "—"}
                </span>
                <span>{selectedIcon ? "Icono seleccionado" : "Sin icono"}</span>
              </span>
              <ChevronDown
                size={17}
                className={`shrink-0 text-secondary transition ${iconDropdownOpen ? "rotate-180" : ""}`}
              />
            </button>

            {iconDropdownOpen && (
              <div
                className="absolute left-0 right-0 top-[calc(100%+0.35rem)] z-10 rounded-panel border border-app bg-panel p-3 shadow-panel"
                aria-labelledby="category-icon-label"
                role="group"
              >
                <div className="grid grid-cols-5 gap-2 sm:grid-cols-7">
                  {categoryIcons.map((icon) => {
                    const selected = selectedIcon === icon;

                    return (
                      <button
                        key={icon || "empty"}
                        type="button"
                        aria-pressed={selected}
                        className={[
                          "grid min-h-10 place-items-center rounded-control border text-lg font-semibold transition",
                          selected
                            ? "border-[color:var(--color-brand)] bg-[color:var(--state-active-bg)] text-[color:var(--state-active-text)]"
                            : "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]",
                          !icon && "px-2 text-xs",
                        ].join(" ")}
                        onClick={() => {
                          setValue("icono", icon, { shouldDirty: true });
                          setIconDropdownOpen(false);
                        }}
                      >
                        {icon || "Sin icono"}
                      </button>
                    );
                  })}
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={creating} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={creating} type="submit">
            {creating ? "Creando..." : "Crear categoría"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

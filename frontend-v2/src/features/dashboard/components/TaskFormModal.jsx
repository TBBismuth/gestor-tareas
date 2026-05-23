import { Plus } from "lucide-react";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import Button from "../../../components/ui/Button.jsx";
import Modal from "../../../components/ui/Modal.jsx";
import { getPriorityVisual } from "../../../styles/taskVisualMaps.js";

const priorities = [
  { value: "BAJA", label: "Baja" },
  { value: "MEDIA", label: "Media" },
  { value: "ALTA", label: "Alta" },
  { value: "IMPRESCINDIBLE", label: "Imprescindible" },
];

const defaultValues = {
  titulo: "",
  descripcion: "",
  prioridad: "MEDIA",
  idCategoria: "",
  fechaEntrega: "",
  tiempo: "",
};

function toDateTimeLocalValue(value) {
  if (!value) return "";

  return String(value).slice(0, 16);
}

function toFormValues(task) {
  if (!task) return defaultValues;

  return {
    titulo: task.titulo || "",
    descripcion: task.descripcion || "",
    prioridad: task.prioridad || "MEDIA",
    idCategoria: task.idCategoria ? String(task.idCategoria) : "",
    fechaEntrega: toDateTimeLocalValue(task.fechaEntrega),
    tiempo: task.tiempo ? String(task.tiempo) : "",
  };
}

function FieldError({ error }) {
  if (!error) return null;

  return <p className="auth-error">{error.message}</p>;
}

function ColorPreview({ color, label }) {
  return (
    <span className="inline-flex items-center gap-2 text-xs font-semibold text-secondary">
      <span
        className="size-3 rounded-full border border-app"
        style={{ background: color || "var(--color-text-muted)" }}
        aria-hidden="true"
      />
      {label}
    </span>
  );
}

function toTaskPayload(values) {
  return {
    titulo: values.titulo.trim(),
    descripcion: values.descripcion?.trim() || null,
    prioridad: values.prioridad,
    idCategoria: Number(values.idCategoria),
    fechaEntrega: values.fechaEntrega || null,
    tiempo: Number(values.tiempo),
  };
}

export default function TaskFormModal({
  categories = [],
  categoriesError = false,
  categoriesLoading = false,
  categoryToSelect,
  creating = false,
  initialTask = null,
  mode = "create",
  open,
  onClose,
  onCreateCategory,
  onSubmit,
}) {
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm({ defaultValues });
  const selectedPriority = watch("prioridad");
  const selectedCategoryId = watch("idCategoria");
  const selectedPriorityVisual = getPriorityVisual(selectedPriority);
  const selectedCategory = categories.find(
    (category) => String(category.idCategoria) === String(selectedCategoryId)
  );
  const isEditMode = mode === "edit";
  const busyLabel = isEditMode ? "Guardando..." : "Guardando...";
  const submitLabel = isEditMode ? "Guardar cambios" : "Guardar";

  useEffect(() => {
    if (open) {
      reset(isEditMode ? toFormValues(initialTask) : defaultValues);
      return;
    }

    reset(defaultValues);
  }, [initialTask, isEditMode, open, reset]);

  useEffect(() => {
    if (open && categoryToSelect?.idCategoria) {
      setValue("idCategoria", String(categoryToSelect.idCategoria), {
        shouldDirty: true,
        shouldValidate: true,
      });
    }
  }, [categoryToSelect, open, setValue]);

  async function submit(values) {
    try {
      await onSubmit(toTaskPayload(values));
      reset(defaultValues);
    } catch {
      // El contenedor muestra el mensaje de error de la mutacion.
    }
  }

  return (
    <Modal
      open={open}
      title={isEditMode ? "Editar tarea" : "Nueva tarea"}
      onClose={creating ? undefined : onClose}
    >
      <form className="grid gap-4" onSubmit={handleSubmit(submit)} noValidate>
        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="task-title">
            Titulo *
          </label>
          <input
            id="task-title"
            className="auth-input"
            placeholder="Titulo de la tarea"
            {...register("titulo", {
              required: "El titulo es obligatorio.",
              minLength: { value: 3, message: "El titulo debe tener al menos 3 caracteres." },
              maxLength: { value: 100, message: "El titulo no puede superar 100 caracteres." },
            })}
          />
          <FieldError error={errors.titulo} />
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="task-description">
            Descripcion
          </label>
          <textarea
            id="task-description"
            className="auth-input min-h-24 resize-y"
            placeholder="Detalles opcionales"
            {...register("descripcion", {
              maxLength: {
                value: 1000,
                message: "La descripcion no puede superar 1000 caracteres.",
              },
            })}
          />
          <FieldError error={errors.descripcion} />
        </div>

        <div className="grid gap-4 sm:grid-cols-2">
          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="task-priority">
              Prioridad *
            </label>
            <select
              id="task-priority"
              className="auth-input"
              style={{ color: selectedPriorityVisual.color }}
              {...register("prioridad", {
                required: "La prioridad es obligatoria.",
              })}
            >
              {priorities.map((priority) => (
                <option
                  key={priority.value}
                  style={{ color: getPriorityVisual(priority.value).color }}
                  value={priority.value}
                >
                  {priority.label}
                </option>
              ))}
            </select>
            <ColorPreview
              color={selectedPriorityVisual.color}
              label={selectedPriorityVisual.label}
            />
            <FieldError error={errors.prioridad} />
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-semibold text-primary" htmlFor="task-time">
              Tiempo estimado (min) *
            </label>
            <input
              id="task-time"
              className="auth-input"
              min="1"
              placeholder="45"
              type="number"
              {...register("tiempo", {
                required: "El tiempo estimado es obligatorio.",
                valueAsNumber: true,
                min: { value: 1, message: "El tiempo debe ser mayor a 0." },
              })}
            />
            <FieldError error={errors.tiempo} />
          </div>
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="task-category">
            Categoria *
          </label>
          <div className="flex flex-col gap-2 sm:flex-row">
            <select
              id="task-category"
              className="auth-input"
              disabled={categoriesLoading || categories.length === 0}
              {...register("idCategoria", {
                required: "La categoria es obligatoria.",
              })}
            >
              <option value="">
                {categoriesLoading ? "Cargando categorias..." : "Selecciona categoria"}
              </option>
              {categories.map((category) => (
                <option
                  key={category.idCategoria}
                  value={category.idCategoria}
                >
                  {category.icono ? `${category.icono} ${category.nombre}` : category.nombre}
                </option>
              ))}
            </select>
            <Button
              className="shrink-0"
              onClick={onCreateCategory}
              type="button"
              variant="secondary"
            >
              <Plus size={17} />
              Anadir
            </Button>
          </div>
          {categoriesError && (
            <p className="auth-error">No se pudieron cargar las categorias.</p>
          )}
          {selectedCategory && (
            <ColorPreview
              color={selectedCategory.color}
              label={
                selectedCategory.icono
                  ? `${selectedCategory.icono} ${selectedCategory.nombre}`
                  : selectedCategory.nombre
              }
            />
          )}
          {!categoriesLoading && categories.length === 0 && !categoriesError && (
            <p className="auth-error">No hay categorias disponibles.</p>
          )}
          <FieldError error={errors.idCategoria} />
        </div>

        <div className="grid gap-2">
          <label className="text-sm font-semibold text-primary" htmlFor="task-due-date">
            Fecha de entrega
          </label>
          <input
            id="task-due-date"
            className="auth-input"
            type="datetime-local"
            {...register("fechaEntrega")}
          />
        </div>

        <div className="mt-2 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
          <Button disabled={creating} onClick={onClose} type="button" variant="secondary">
            Cancelar
          </Button>
          <Button disabled={creating} type="submit">
            {creating ? busyLabel : submitLabel}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

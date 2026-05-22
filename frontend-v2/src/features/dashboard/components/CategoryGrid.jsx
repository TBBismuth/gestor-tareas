import { Pencil, Trash2 } from "lucide-react";
import Badge from "../../../components/ui/Badge.jsx";
import Button from "../../../components/ui/Button.jsx";

export default function CategoryGrid({ categories, onDelete, onEdit }) {
  return (
    <div className="mt-5 grid gap-3 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
      {categories.map((category) => (
        <article
          className="category-card relative overflow-hidden rounded-panel border border-app bg-card p-4 shadow-card"
          key={category.idCategoria}
        >
          {category.color && (
            <div
              className="category-card-accent"
              style={{ "--category-accent-color": category.color }}
              aria-hidden="true"
            />
          )}
          <div className="relative z-10 flex min-h-28 flex-col justify-between gap-4">
            <div className="flex items-start justify-between gap-3">
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  {category.icono && (
                    <span className="grid size-9 shrink-0 place-items-center rounded-control border border-app bg-[color:var(--state-inactive-bg)] text-lg">
                      {category.icono}
                    </span>
                  )}
                  <h2 className="truncate text-base font-semibold text-primary">
                    {category.nombre}
                  </h2>
                </div>
              </div>
            </div>

            <div className="flex flex-wrap items-center gap-2">
              {category.protegida ? (
                <Badge>Categoría base</Badge>
              ) : (
                <>
                  <Button
                    size="sm"
                    type="button"
                    variant="secondary"
                    onClick={() => onEdit(category)}
                  >
                    <Pencil size={15} />
                    Editar
                  </Button>
                  <Button
                    className="danger-action-button"
                    size="sm"
                    type="button"
                    variant="secondary"
                    onClick={() => onDelete(category)}
                  >
                    <Trash2 size={15} />
                    Borrar
                  </Button>
                </>
              )}
            </div>
          </div>
        </article>
      ))}
    </div>
  );
}

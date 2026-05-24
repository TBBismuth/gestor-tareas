import { X } from "lucide-react";
import IconButton from "./IconButton.jsx";
import { cn } from "../../lib/cn";

const sizes = {
  md: "max-w-lg",
  lg: "max-w-3xl",
  xl: "max-w-5xl",
  wide: "max-w-6xl",
};

export default function Modal({
  open,
  title,
  children,
  onClose,
  size = "md",
  zIndexClass = "z-50",
}) {
  if (!open) return null;

  return (
    <div
      className={cn(
        "fixed inset-0 grid place-items-center bg-[color:var(--color-overlay)] px-4",
        zIndexClass
      )}
    >
      <section
        className={cn(
          "max-h-[calc(100vh-2rem)] w-full overflow-hidden rounded-panel border border-app bg-panel shadow-panel",
          sizes[size] || sizes.md
        )}
      >
        <header className="flex items-center justify-between border-b border-app px-5 py-4">
          <h2 className="text-lg font-semibold text-primary">{title}</h2>
          <IconButton label="Cerrar modal" onClick={onClose}>
            <X size={18} />
          </IconButton>
        </header>
        <div className="max-h-[calc(100vh-7rem)] overflow-y-auto p-5">{children}</div>
      </section>
    </div>
  );
}

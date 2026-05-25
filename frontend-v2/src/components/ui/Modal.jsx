import { useEffect } from "react";
import { createPortal } from "react-dom";
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
  closeOnOverlayClick = false,
  size = "md",
  zIndexClass = "z-50",
}) {
  useEffect(() => {
    if (!open || typeof document === "undefined") {
      return undefined;
    }

    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";

    return () => {
      document.body.style.overflow = previousOverflow;
    };
  }, [open]);

  if (!open) return null;

  const modal = (
    <div
      className={cn(
        "pointer-events-auto fixed inset-0 grid place-items-center overscroll-contain bg-[color:var(--color-overlay)] px-4",
        zIndexClass
      )}
      onClick={closeOnOverlayClick && onClose ? onClose : undefined}
    >
      <section
        aria-modal="true"
        className={cn(
          "pointer-events-auto max-h-[calc(100vh-2rem)] w-full overflow-hidden rounded-panel border border-app bg-panel shadow-panel",
          sizes[size] || sizes.md
        )}
        onClick={(event) => event.stopPropagation()}
        role="dialog"
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

  if (typeof document === "undefined") {
    return modal;
  }

  return createPortal(modal, document.body);
}

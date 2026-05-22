import { X } from "lucide-react";
import IconButton from "./IconButton.jsx";

export default function Modal({ open, title, children, onClose }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-[color:var(--color-overlay)] px-4">
      <section className="w-full max-w-lg rounded-panel border border-app bg-panel shadow-panel">
        <header className="flex items-center justify-between border-b border-app px-5 py-4">
          <h2 className="text-lg font-semibold text-primary">{title}</h2>
          <IconButton label="Cerrar modal" onClick={onClose}>
            <X size={18} />
          </IconButton>
        </header>
        <div className="p-5">{children}</div>
      </section>
    </div>
  );
}

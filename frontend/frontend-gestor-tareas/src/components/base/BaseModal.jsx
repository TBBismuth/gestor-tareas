import { useEffect } from "react";

export default function Modal({ open, onClose, title = "", children, footer = null }) {
    useEffect(() => {
        function onEsc(e) { if (e.key === "Escape") onClose?.(); }
        if (open) document.addEventListener("keydown", onEsc);
        return () => document.removeEventListener("keydown", onEsc);
    }, [open, onClose]);

    if (!open) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
            {/* Backdrop */}
            <div className="absolute inset-0 bg-black/40" onClick={onClose} />

            {/* Dialog */}
            <div className="relative z-10 w-full max-w-lg rounded-xl bg-white shadow-xl border">
                {title && (
                    <div className="px-4 py-3 border-b">
                        <h3 className="text-lg font-semibold">{title}</h3>
                    </div>
                )}
                <div className="p-4">
                    {children}
                </div>
                {footer && (
                    <div className="px-4 py-3 border-t bg-gray-50 flex justify-end gap-2">
                        {footer}
                    </div>
                )}
            </div>
        </div>
    );
}

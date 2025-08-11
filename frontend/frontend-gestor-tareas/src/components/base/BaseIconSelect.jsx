import { useId } from "react";
import BaseField from "./BaseField";
import BaseSelect from "./BaseSelect";

/**
 * BaseIconSelect
 * Select de iconos sencillo (sin dependencias) con vista previa.
 * - value: string (emoji o nombre corto)
 * - onChange: (event) => void
 * - label: string (opcional) — si no se pasa, no pinta etiqueta extra (se usa dentro de BaseField)
 * - required: boolean
 * - options: array opcional para sobreescribir el catálogo por defecto
 */
const DEFAULT_ICONS = [
    // Trabajo/Organización
    "📁", "📌", "🗂️", "📅", "📝", "✅", "📊", "🧭",
    // Hogar/Personal
    "🏠", "🛒", "🧹", "🧺", "🛠️", "🧑‍🍳",
    // Salud
    "🧘", "🏃", "💊", "🩺",
    // Estudio
    "📚", "🧠", "🧪", "🖥️",
    // Varios
    "⭐", "🔥", "⚡", "⏳", "🕒", "🔔"
];

export default function BaseIconSelect({
    id,
    label = "Icono",
    value = "",
    onChange,
    required = false,
    options = DEFAULT_ICONS,
    className = "",
}) {
    const genId = useId();
    const selectId = id || genId;

    return (
        <BaseField id={selectId} label={label} requerido={required} className={className}>
            <div className="flex items-center gap-2">
                {/* Vista previa */}
                <div
                    aria-hidden
                    className="w-9 h-9 flex items-center justify-center rounded border bg-white text-xl"
                    title={value || "Sin icono"}
                >
                    {value || "—"}
                </div>

                {/* Select nativo */}
                <BaseSelect
                    id={selectId}
                    value={value}
                    onChange={onChange}
                    required={required}
                    className="flex-1"
                >
                    <option value="" disabled>Selecciona un icono</option>
                    {options.map((ico, idx) => (
                        <option key={idx} value={ico}>
                            {ico}
                        </option>
                    ))}
                </BaseSelect>
            </div>
        </BaseField>
    );
}

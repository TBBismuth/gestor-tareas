import Etiqueta from "./Etiqueta";
import Input from "./Input";

export default function Campo({
    id,
    label,
    requerido = false,
    type = "text",
    value,
    onChange,
    placeholder = "",
    error = "",
    className = "",
}) {
    return (
        <div className={`mb-4 ${className}`}>
            {label && <Etiqueta htmlFor={id} requerido={requerido}>{label}</Etiqueta>}
            <Input
                type={type}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                className={error ? "border-red-500 focus:ring-red-400" : ""}
                aria-invalid={!!error}
                aria-describedby={error ? `${id}-error` : undefined}
            />
            {error && (
                <p id={`${id}-error`} className="mt-1 text-sm text-red-600">
                    {error}
                </p>
            )}
        </div>
    );
}

import Etiqueta from "./BaseLabel";
import Input from "./BaseInput";

export default function Campo({
    id,
    label,
    requerido = false,
    error = "",
    className = "",
    children,          // <- si lo pasas, NO forzamos BaseInput
    ...inputProps      // <- si NO hay children, se pasan a BaseInput (type, value, onChange, placeholder, etc.)
}) {
    return (
        <div className={`mb-4 ${className}`}>
            {label && (
                <Etiqueta htmlFor={id} requerido={requerido}>
                    {label}
                </Etiqueta>
            )}

            {children ? (
                // Modo "contenedor": renderizamos lo que nos pases (select, textarea, wrappers, etc.)
                <div
                    aria-invalid={!!error}
                    aria-describedby={error ? `${id}-error` : undefined}
                >
                    {children}
                </div>
            ) : (
                // Modo "input por defecto": usamos el Input base centralizado
                <Input
                    id={id}
                    {...inputProps}
                    className={`${inputProps.className || ""} ${error ? "border-red-500 focus:ring-red-400" : ""
                        }`}
                    aria-invalid={!!error}
                    aria-describedby={error ? `${id}-error` : undefined}
                />
            )}

            {error && (
                <p id={`${id}-error`} className="mt-1 text-sm text-red-600">
                    {error}
                </p>
            )}
        </div>
    );
}

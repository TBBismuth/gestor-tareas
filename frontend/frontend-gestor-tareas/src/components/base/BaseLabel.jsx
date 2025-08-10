export default function Etiqueta({ htmlFor, children, className = "", requerido = false }) {
    return (
        <label
            htmlFor={htmlFor}
            className={`mb-1 block text-sm font-medium text-gray-700 ${className}`}
        >
            {children} {requerido && <span className="text-red-600">*</span>}
        </label>
    );
}

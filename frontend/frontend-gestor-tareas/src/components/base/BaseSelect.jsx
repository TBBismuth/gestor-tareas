export default function BaseSelect({
    id,
    name,
    value,
    onChange,
    required = false,
    disabled = false,
    className = "",
    children,
    ...props
}) {
    return (
        <select
            id={id}
            name={name || id}
            value={value}
            onChange={onChange}
            required={required}
            disabled={disabled}
            // Forzamos el aspecto nativo del select en todos los navegadores
            style={{ appearance: "auto", WebkitAppearance: "menulist", MozAppearance: "menulist" }}
            className={`border p-2 w-full cursor-pointer ${className}`}
            {...props}
        >
            {children}
        </select>
    );
}

export default function Input({
    type = "text",
    placeholder = "",
    value,
    onChange,
    className = "",
}) {
    return (
        <input
            type={type}
            placeholder={placeholder}
            value={value}
            onChange={onChange}
            className={`px-3 py-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-400 ${className}`}
        />
    );
}

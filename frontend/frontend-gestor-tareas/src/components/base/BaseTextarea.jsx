export default function Textarea({
    id,
    placeholder = "",
    value,
    onChange,
    rows = 4,
    className = "",
}) {
    return (
        <textarea
            id={id}
            placeholder={placeholder}
            value={value}
            onChange={onChange}
            rows={rows}
            className={`px-3 py-2 border rounded w-full resize-y focus:outline-none focus:ring-2 focus:ring-blue-400 ${className}`}
        />
    );
}

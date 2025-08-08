export default function Boton({ children, onClick, type = "button", className = "" }) {
    return (
        <button
            type={type}
            onClick={onClick}
            className={`px-4 py-2 rounded font-semibold transition-colors bg-blue-500 text-white hover:bg-blue-600 ${className}`}
        >
            {children}
        </button>
    );
}

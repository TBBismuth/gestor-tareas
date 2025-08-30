export default function Card({ children, className = "", style = {} }) {
    return (
        <div
            className={`rounded-lg border bg-white p-4 shadow-sm ${className}`}
            style={style}
        >
            {children}
        </div>
    );
}

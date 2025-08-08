export default function Card({ children, className = "" }) {
    return (
        <div className={`rounded-lg border bg-white p-4 shadow-sm ${className}`}>
            {children}
        </div>
    );
}

const variants = {
    primary: "bg-blue-500 text-white hover:bg-blue-600",
    secondary: "bg-gray-500 text-white hover:bg-gray-600",
    success: "bg-green-500 text-white hover:bg-green-600",
    danger: "bg-red-500 text-white hover:bg-red-600",
    outline: "border border-gray-400 text-gray-700 hover:bg-gray-100",
    ghost: "bg-transparent text-gray-700 hover:bg-gray-100",
    link: "bg-transparent text-blue-500 hover:underline p-0",
};

const sizes = {
    xs: "px-1.5 py-0.5 text-xs min-h-[22px]",
    sm: "px-2 py-1 text-sm min-h-[28px]",
    md: "px-4 py-2 text-base min-h-[36px]",
    lg: "px-6 py-3 text-lg min-h-[44px]",
};

export default function Boton({
    children,
    onClick,
    type = "button",
    className = "",
    variant = "primary",
    size = "md",
    disabled = false,
    ...props
}) {
    return (
        <button
            type={type}
            onClick={onClick}
            disabled={disabled}
            {...props}
            className={`
        rounded font-semibold transition-colors duration-200
        focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-400
        ${variants[variant] || variants.primary}
        ${sizes[size] || sizes.md}
        ${disabled ? "opacity-50 cursor-not-allowed" : "cursor-pointer"}
        ${className}
      `}
        >
            {children}
        </button>
    );
}

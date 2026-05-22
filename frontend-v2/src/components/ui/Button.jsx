import { cn } from "../../lib/cn";

const variants = {
  primary:
    "border-[color:var(--color-brand)] bg-[color:var(--color-brand)] text-[color:var(--color-text-inverse)] hover:bg-[color:var(--color-brand-strong)]",
  secondary:
    "border-app bg-[color:var(--state-inactive-bg)] text-secondary hover:bg-[color:var(--color-bg-muted)]",
  ghost:
    "border-transparent bg-transparent text-secondary hover:bg-[color:var(--state-inactive-bg)]",
};

const sizes = {
  sm: "min-h-8 px-3 text-sm",
  md: "min-h-10 px-4 text-sm",
  lg: "min-h-11 px-5 text-base",
};

export default function Button({
  children,
  className,
  variant = "primary",
  size = "md",
  type = "button",
  ...props
}) {
  return (
    <button
      type={type}
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-control border font-semibold transition disabled:cursor-not-allowed disabled:bg-[color:var(--state-disabled-bg)] disabled:text-[color:var(--state-disabled-text)]",
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {children}
    </button>
  );
}

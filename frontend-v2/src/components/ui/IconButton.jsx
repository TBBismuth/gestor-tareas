import { cn } from "../../lib/cn";

export default function IconButton({ label, children, className, type = "button", ...props }) {
  return (
    <button
      type={type}
      aria-label={label}
      title={label}
      className={cn(
        "inline-grid size-10 place-items-center rounded-control border border-app bg-[color:var(--state-inactive-bg)] text-secondary transition hover:bg-[color:var(--color-bg-muted)] disabled:cursor-not-allowed disabled:bg-[color:var(--state-disabled-bg)] disabled:text-[color:var(--state-disabled-text)]",
        className
      )}
      {...props}
    >
      {children}
    </button>
  );
}

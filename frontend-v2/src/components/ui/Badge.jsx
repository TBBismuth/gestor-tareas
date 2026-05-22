import { cn } from "../../lib/cn";

export default function Badge({ children, className, toneColor }) {
  return (
    <span
      className={cn(
        "inline-flex min-h-7 items-center rounded-full border px-2.5 text-xs font-semibold",
        className
      )}
      style={{
        borderColor: toneColor || "var(--color-border)",
        color: toneColor || "var(--color-text-secondary)",
        background: "color-mix(in srgb, currentColor 10%, white)",
      }}
    >
      {children}
    </span>
  );
}

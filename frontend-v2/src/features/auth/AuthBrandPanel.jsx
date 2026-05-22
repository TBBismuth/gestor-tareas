import { CheckCircle2 } from "lucide-react";
import { cn } from "../../lib/cn";

const bullets = ["Organiza tus tareas", "Prioriza lo importante", "Alcanza tus metas"];

export default function AuthBrandPanel({ centerContent = false }) {
  return (
    <section className={cn("auth-brand-panel", centerContent && "auth-brand-panel-centered")}>
      <div className="auth-orb auth-orb-primary" aria-hidden="true" />
      <div className="auth-orb auth-orb-secondary" aria-hidden="true" />

      <div className="relative z-10">
        <div className="grid gap-4">
          <img
            src="/branding/app-icon.png"
            alt=""
            className="size-16 rounded-panel shadow-control"
            aria-hidden="true"
          />
          <div>
            <p className="text-xs font-semibold uppercase tracking-wide text-muted">
              Productividad personal
            </p>
            <h1 className="mt-2 text-2xl font-semibold leading-tight text-primary">
              Gestor de tareas
            </h1>
          </div>
        </div>

        <p className="mt-5 text-sm leading-6 text-secondary">
          Organiza tu dia con una experiencia clara, rapida y enfocada.
        </p>

        <div className="mt-7 grid gap-3">
          {bullets.map((item) => (
            <div
              key={item}
              className="flex items-center gap-3 rounded-control bg-[color:var(--auth-bullet-bg)] px-3 py-2 text-secondary"
            >
              <CheckCircle2 size={18} className="text-[color:var(--color-brand)]" />
              <span className="whitespace-nowrap text-sm font-medium">{item}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

import { CheckCircle2 } from "lucide-react";
import { useState } from "react";
import Modal from "../../components/ui/Modal.jsx";
import { cn } from "../../lib/cn";

const bullets = ["Organiza tus tareas", "Prioriza lo importante", "Alcanza tus metas"];

export default function AuthBrandPanel({ centerContent = false }) {
  const [logoClickCount, setLogoClickCount] = useState(0);
  const [easterEggOpen, setEasterEggOpen] = useState(false);

  function handleLogoClick() {
    setLogoClickCount((current) => {
      const nextCount = current + 1;

      if (nextCount >= 10) {
        setEasterEggOpen(true);
        return 0;
      }

      return nextCount;
    });
  }

  return (
    <>
      <section className={cn("auth-brand-panel", centerContent && "auth-brand-panel-centered")}>
        <div className="auth-orb auth-orb-primary" aria-hidden="true" />
        <div className="auth-orb auth-orb-secondary" aria-hidden="true" />

        <div className="relative z-10">
          <div className="auth-brand-heading grid gap-4">
            <button
              type="button"
              className="auth-logo-easter-button"
              aria-label="Logo de Gestor de tareas"
              onClick={handleLogoClick}
            >
              <img
                src="/branding/app-icon.png"
                alt=""
                className="size-16 rounded-panel shadow-control"
                aria-hidden="true"
              />
            </button>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-muted">
                Productividad personal
              </p>
              <h1 className="mt-2 text-2xl font-semibold leading-tight text-primary">
                Gestor de tareas
              </h1>
            </div>
          </div>

          <p className="auth-brand-copy mt-5 text-sm leading-6 text-secondary">
            Organiza tu dia con una experiencia clara, rapida y enfocada.
          </p>

          <div className="auth-brand-benefits mt-7 grid gap-3">
            {bullets.map((item) => (
              <div
                key={item}
                className="auth-brand-benefit flex items-center gap-3 rounded-control bg-[color:var(--auth-bullet-bg)] px-3 py-2 text-secondary"
              >
                <CheckCircle2 size={18} className="text-[color:var(--color-brand)]" />
                <span className="whitespace-nowrap text-sm font-medium">{item}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <Modal
        open={easterEggOpen}
        title="Has encontrado el rincón oculto 👀"
        onClose={() => setEasterEggOpen(false)}
      >
        <div className="grid gap-4 text-sm leading-6 text-secondary">
          <p>
            Esta app ha sido desarrollada por Miguel Guerrero Murillo como proyecto final de
            Desarrollo de Aplicaciones Multiplataforma.
          </p>
          <p>
            Gestor de Tareas combina tareas personales, grupos, filtros inteligentes,
            recordatorios y notificaciones PWA en una sola aplicación.
          </p>
          <p>Gracias por echarle un vistazo.</p>
          <p>Puedes encontrar el código fuente de este proyecto bajo licencia MIT en mi GitHub.</p>
          <a
            className="font-semibold text-[color:var(--color-brand)] hover:underline"
            href="https://github.com/TBBismuth"
            target="_blank"
            rel="noreferrer"
          >
            Ir a mi GitHub
          </a>
        </div>
      </Modal>
    </>
  );
}

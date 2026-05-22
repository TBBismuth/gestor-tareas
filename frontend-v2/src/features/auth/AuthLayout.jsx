import { Moon, Sun } from "lucide-react";
import { useTheme } from "../../app/theme.jsx";
import IconButton from "../../components/ui/IconButton.jsx";
import AuthBrandPanel from "./AuthBrandPanel.jsx";

function AuthVisualPanel() {
  return (
    <section className="auth-visual-panel" aria-hidden="true">
      <div className="auth-visual-glow" />
      <img
        src="/branding/login-illustration.png"
        alt=""
        className="auth-visual-image"
      />
    </section>
  );
}

export default function AuthLayout({ children, variant = "login" }) {
  const { theme, toggleTheme } = useTheme();
  const ThemeIcon = theme === "dark" ? Sun : Moon;

  return (
    <main className="auth-page">
      <IconButton
        label="Cambiar tema"
        className="absolute right-4 top-4 z-20"
        onClick={toggleTheme}
      >
        <ThemeIcon size={18} />
      </IconButton>

      <div className="auth-shell" data-auth-variant={variant}>
        <AuthBrandPanel centerContent={variant === "register"} />
        <AuthVisualPanel />
        <section className="auth-form-panel">
          {children}
        </section>
      </div>
    </main>
  );
}

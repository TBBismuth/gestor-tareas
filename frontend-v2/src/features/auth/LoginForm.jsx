import { LogIn } from "lucide-react";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import Button from "../../components/ui/Button.jsx";
import PasswordField from "./PasswordField.jsx";
import TextField from "./TextField.jsx";

export default function LoginForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: {
      email: "",
      password: "",
    },
  });

  function onSubmit(values) {
    toast.info("Login pendiente de integrar con backend", {
      description: values.email,
    });
  }

  return (
    <div className="auth-card">
      <div>
        <p className="text-xs font-semibold uppercase tracking-wide text-muted">Acceso</p>
        <h2 className="mt-2 text-3xl font-semibold text-primary">Iniciar sesion</h2>
        <p className="mt-2 text-sm leading-6 text-secondary">
          Bienvenido de nuevo.
        </p>
      </div>

      <form className="mt-8 grid gap-5" onSubmit={handleSubmit(onSubmit)} noValidate>
        <TextField
          id="email"
          label="Email"
          type="email"
          autoComplete="email"
          placeholder="tu@email.com"
          error={errors.email}
          register={register("email", {
            required: "El email es obligatorio.",
          })}
        />

        <PasswordField
          id="password"
          label="Contrasena"
          autoComplete="current-password"
          error={errors.password}
          register={register("password", {
            required: "La contrasena es obligatoria.",
          })}
        />

        <Button type="submit" size="lg" className="w-full">
          <LogIn size={18} />
          Entrar
        </Button>
      </form>

      <p className="mt-6 text-center text-sm text-secondary">
        No tienes cuenta?{" "}
        <Link className="font-semibold text-[color:var(--color-brand)] hover:underline" to="/register">
          Crear cuenta
        </Link>
      </p>
    </div>
  );
}

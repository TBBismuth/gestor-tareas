import { Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import IconButton from "../../components/ui/IconButton.jsx";

export default function PasswordField({
  id,
  label,
  register,
  error,
  autoComplete,
  placeholder = "Introduce tu contrasena",
}) {
  const [visible, setVisible] = useState(false);

  return (
    <div className="grid gap-2">
      <label htmlFor={id} className="text-sm font-semibold text-primary">
        {label}
      </label>
      <div className="relative">
        <input
          id={id}
          type={visible ? "text" : "password"}
          autoComplete={autoComplete}
          placeholder={placeholder}
          className="auth-input pr-12"
          aria-invalid={!!error}
          aria-describedby={error ? `${id}-error` : undefined}
          {...register}
        />
        <IconButton
          label={visible ? "Ocultar contrasena" : "Mostrar contrasena"}
          className="absolute right-1 top-1/2 size-9 -translate-y-1/2 border-transparent bg-transparent"
          onClick={() => setVisible((current) => !current)}
        >
          {visible ? <EyeOff size={17} /> : <Eye size={17} />}
        </IconButton>
      </div>
      {error && (
        <p id={`${id}-error`} className="auth-error">
          {error.message}
        </p>
      )}
    </div>
  );
}

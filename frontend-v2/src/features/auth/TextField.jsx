export default function TextField({
  id,
  label,
  type = "text",
  register,
  error,
  autoComplete,
  placeholder,
}) {
  return (
    <div className="grid gap-2">
      <label htmlFor={id} className="text-sm font-semibold text-primary">
        {label}
      </label>
      <input
        id={id}
        type={type}
        autoComplete={autoComplete}
        placeholder={placeholder}
        className="auth-input"
        aria-invalid={!!error}
        aria-describedby={error ? `${id}-error` : undefined}
        {...register}
      />
      {error && (
        <p id={`${id}-error`} className="auth-error">
          {error.message}
        </p>
      )}
    </div>
  );
}

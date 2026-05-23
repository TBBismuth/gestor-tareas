export default function CategoryLabel({ color, icon, name }) {
  const hasIcon = Boolean(icon);
  const hasColor = Boolean(color);

  return (
    <span className="category-label">
      {hasIcon && (
        <span className="category-label-icon" aria-hidden="true">
          {icon}
        </span>
      )}
      <span className="category-label-name">{name || "Sin categoria"}</span>
      {hasColor && (
        <span
          className="category-label-dot"
          style={{ backgroundColor: color }}
          aria-hidden="true"
        />
      )}
    </span>
  );
}

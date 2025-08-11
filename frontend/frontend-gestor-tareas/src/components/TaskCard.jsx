import BaseCard from "./base/BaseCard";
import { PRIORIDAD_BG, ESTADO_BG, pick } from "../styles/themeColors";

export default function CardTarea({ titulo, descripcion, fechaEntrega, categoria, prioridad, estado }) {
  const colorPrioridad = pick(PRIORIDAD_BG, prioridad);
  const colorEstado = pick(ESTADO_BG, estado);

  return (
    <div className="relative overflow-hidden rounded-lg border shadow-sm">
      {/* Fondo diagonal */}
      <div
        className="absolute inset-0"
        style={{
          background: `linear-gradient(135deg, ${colorPrioridad} 50%, ${colorEstado} 50%)`
        }}
      ></div>

      {/* Contenido encima */}
      <div className="relative p-4 bg-white/80">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-lg font-semibold text-gray-800">{titulo}</h3>
            {descripcion && <p className="text-sm text-gray-600 mt-1">{descripcion}</p>}
            {categoria && (
              <span className="inline-block mt-2 px-2 py-1 text-xs rounded bg-blue-100 text-blue-700">
                {categoria}
              </span>
            )}
          </div>
          {fechaEntrega && (
            <span className="text-xs text-gray-500">
              {new Date(fechaEntrega).toLocaleDateString()}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

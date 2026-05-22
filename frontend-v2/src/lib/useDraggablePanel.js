import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
import { readStorage, writeStorage } from "./storage";

const DEFAULT_MARGIN = 24;

function parsePosition(value) {
  if (!value) return null;

  try {
    const parsed = JSON.parse(value);
    if (Number.isFinite(parsed?.x) && Number.isFinite(parsed?.y)) {
      return parsed;
    }
  } catch {
    return null;
  }

  return null;
}

function getInitialPosition(node) {
  const rect = node?.getBoundingClientRect();
  const width = rect?.width || 256;
  const height = rect?.height || 320;

  return {
    x: window.innerWidth - width - 120,
    y: Math.max(DEFAULT_MARGIN, (window.innerHeight - height) * 0.44),
  };
}

function clampPosition(position, node) {
  const rect = node?.getBoundingClientRect();
  const width = rect?.width || 256;
  const height = rect?.height || 320;
  const maxX = Math.max(DEFAULT_MARGIN, window.innerWidth - width - DEFAULT_MARGIN);
  const maxY = Math.max(DEFAULT_MARGIN, window.innerHeight - height - DEFAULT_MARGIN);

  return {
    x: Math.min(Math.max(position.x, DEFAULT_MARGIN), maxX),
    y: Math.min(Math.max(position.y, DEFAULT_MARGIN), maxY),
  };
}

function isSamePosition(a, b) {
  return a?.x === b?.x && a?.y === b?.y;
}

export function useDraggablePanel(storageKey) {
  const panelRef = useRef(null);
  const dragRef = useRef(null);
  const [position, setPosition] = useState(null);
  const [isDragging, setIsDragging] = useState(false);

  const persistPosition = useCallback(
    (nextPosition) => {
      writeStorage(storageKey, JSON.stringify(nextPosition));
    },
    [storageKey]
  );

  const resetPosition = useCallback(() => {
    if (!panelRef.current) return;
    const nextPosition = clampPosition(getInitialPosition(panelRef.current), panelRef.current);
    setPosition(nextPosition);
    persistPosition(nextPosition);
  }, [persistPosition]);

  useLayoutEffect(() => {
    if (!panelRef.current || position) return;

    const savedPosition = parsePosition(readStorage(storageKey));
    const basePosition = savedPosition || getInitialPosition(panelRef.current);
    const nextPosition = clampPosition(basePosition, panelRef.current);
    setPosition(nextPosition);

    if (!savedPosition || !isSamePosition(savedPosition, nextPosition)) {
      persistPosition(nextPosition);
    }
  }, [persistPosition, position, storageKey]);

  useEffect(() => {
    function handleResize() {
      if (!panelRef.current) return;

      setPosition((current) => {
        if (!current) return current;
        const nextPosition = clampPosition(current, panelRef.current);
        persistPosition(nextPosition);
        return nextPosition;
      });
    }

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [persistPosition]);

  useEffect(() => {
    function handlePointerMove(event) {
      if (!dragRef.current || !panelRef.current) return;

      const nextPosition = clampPosition(
        {
          x: event.clientX - dragRef.current.offsetX,
          y: event.clientY - dragRef.current.offsetY,
        },
        panelRef.current
      );

      setPosition(nextPosition);
    }

    function handlePointerUp() {
      if (!dragRef.current) return;

      setIsDragging(false);
      dragRef.current = null;
      document.body.style.userSelect = "";

      setPosition((current) => {
        if (current) persistPosition(current);
        return current;
      });
    }

    if (isDragging) {
      window.addEventListener("pointermove", handlePointerMove);
      window.addEventListener("pointerup", handlePointerUp);
      window.addEventListener("pointercancel", handlePointerUp);
    }

    return () => {
      window.removeEventListener("pointermove", handlePointerMove);
      window.removeEventListener("pointerup", handlePointerUp);
      window.removeEventListener("pointercancel", handlePointerUp);
    };
  }, [isDragging, persistPosition]);

  const dragHandleProps = {
    onPointerDown: (event) => {
      if (event.pointerType !== "mouse" || event.button !== 0 || !panelRef.current) return;

      const rect = panelRef.current.getBoundingClientRect();
      dragRef.current = {
        offsetX: event.clientX - rect.left,
        offsetY: event.clientY - rect.top,
      };
      setIsDragging(true);
      document.body.style.userSelect = "none";
      event.currentTarget.setPointerCapture?.(event.pointerId);
    },
  };

  return {
    panelRef,
    position,
    isDragging,
    dragHandleProps,
    resetPosition,
  };
}

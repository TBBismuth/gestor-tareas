import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
import { readStorage, writeStorage } from "./storage";

const DEFAULT_MARGIN = 24;
const DEFAULT_DRAG_THRESHOLD = 6;

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

function resolveDefaultPosition(defaultPosition, node) {
  if (typeof defaultPosition === "function") {
    return defaultPosition(node);
  }

  return defaultPosition || null;
}

function getInitialPosition(node, defaultPosition) {
  const resolvedPosition = resolveDefaultPosition(defaultPosition, node);
  if (resolvedPosition) {
    return resolvedPosition;
  }

  const rect = node?.getBoundingClientRect();
  const width = rect?.width || 256;
  const height = rect?.height || 320;

  return {
    x: window.innerWidth - width - 120,
    y: Math.max(DEFAULT_MARGIN, (window.innerHeight - height) * 0.44),
  };
}

function clampPosition(position, node, margin = DEFAULT_MARGIN) {
  const rect = node?.getBoundingClientRect();
  const width = rect?.width || 256;
  const height = rect?.height || 320;
  const maxX = Math.max(margin, window.innerWidth - width - margin);
  const maxY = Math.max(margin, window.innerHeight - height - margin);

  return {
    x: Math.min(Math.max(position.x, margin), maxX),
    y: Math.min(Math.max(position.y, margin), maxY),
  };
}

function isSamePosition(a, b) {
  return a?.x === b?.x && a?.y === b?.y;
}

export function useDraggablePanel(storageKey, options = {}) {
  const {
    defaultPosition = null,
    dragThreshold = DEFAULT_DRAG_THRESHOLD,
    margin = DEFAULT_MARGIN,
  } = options;
  const panelRef = useRef(null);
  const dragRef = useRef(null);
  const suppressClickRef = useRef(false);
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
    const nextPosition = clampPosition(
      getInitialPosition(panelRef.current, defaultPosition),
      panelRef.current,
      margin
    );
    setPosition(nextPosition);
    persistPosition(nextPosition);
  }, [defaultPosition, margin, persistPosition]);

  useLayoutEffect(() => {
    if (!panelRef.current || position) return;

    const savedPosition = parsePosition(readStorage(storageKey));
    const basePosition = savedPosition || getInitialPosition(panelRef.current, defaultPosition);
    const nextPosition = clampPosition(basePosition, panelRef.current, margin);
    setPosition(nextPosition);

    if (!savedPosition || !isSamePosition(savedPosition, nextPosition)) {
      persistPosition(nextPosition);
    }
  }, [defaultPosition, margin, persistPosition, position, storageKey]);

  useEffect(() => {
    function handleResize() {
      if (!panelRef.current) return;

      setPosition((current) => {
        if (!current) return current;
        const nextPosition = clampPosition(current, panelRef.current, margin);
        persistPosition(nextPosition);
        return nextPosition;
      });
    }

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [margin, persistPosition]);

  useEffect(() => {
    if (!panelRef.current || typeof ResizeObserver === "undefined") return undefined;

    const observer = new ResizeObserver(() => {
      setPosition((current) => {
        if (!current || !panelRef.current) return current;
        const nextPosition = clampPosition(current, panelRef.current, margin);
        if (!isSamePosition(current, nextPosition)) {
          persistPosition(nextPosition);
        }
        return nextPosition;
      });
    });

    observer.observe(panelRef.current);
    return () => observer.disconnect();
  }, [margin, persistPosition]);

  useEffect(() => {
    function handlePointerMove(event) {
      if (!dragRef.current || !panelRef.current) return;

      const deltaX = event.clientX - dragRef.current.startX;
      const deltaY = event.clientY - dragRef.current.startY;
      const distance = Math.hypot(deltaX, deltaY);

      if (!dragRef.current.moved && distance < dragThreshold) {
        return;
      }

      dragRef.current.moved = true;
      const nextPosition = clampPosition(
        {
          x: event.clientX - dragRef.current.offsetX,
          y: event.clientY - dragRef.current.offsetY,
        },
        panelRef.current,
        margin
      );

      setPosition(nextPosition);
    }

    function handlePointerUp() {
      if (!dragRef.current) return;

      suppressClickRef.current = dragRef.current.moved;
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
  }, [dragThreshold, isDragging, margin, persistPosition]);

  const dragHandleProps = {
    onPointerDown: (event) => {
      if ((event.pointerType === "mouse" && event.button !== 0) || !panelRef.current) return;

      const rect = panelRef.current.getBoundingClientRect();
      dragRef.current = {
        offsetX: event.clientX - rect.left,
        offsetY: event.clientY - rect.top,
        startX: event.clientX,
        startY: event.clientY,
        moved: false,
      };
      setIsDragging(true);
      document.body.style.userSelect = "none";
      event.currentTarget.setPointerCapture?.(event.pointerId);
    },
    onClickCapture: (event) => {
      if (!suppressClickRef.current) return;

      suppressClickRef.current = false;
      event.preventDefault();
      event.stopPropagation();
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

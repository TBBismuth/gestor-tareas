import { cn } from "../../lib/cn";

export default function AppShell({ topBar, sidebar, children, focusArea = "filter" }) {
  const sidebarDimmed = focusArea === "filter";
  const topBarDimmed = focusArea === "sidebar";

  return (
    <div className="min-h-screen bg-app p-4 lg:p-6">
      <div className="relative mx-auto min-h-[calc(100vh-2rem)] max-w-[1680px] lg:min-h-[calc(100vh-3rem)]">
        <main className="flex min-w-0 flex-1 flex-col gap-4">
          <div
            className={cn(
              "transition duration-200",
              topBarDimmed && "opacity-55 saturate-50"
            )}
          >
            {topBar}
          </div>
          <section className="min-h-0 flex-1 rounded-panel border border-app bg-panel p-4 shadow-panel">
            {children}
          </section>
        </main>
        <aside
          className={cn(
            "floating-sidebar hidden transition duration-200 lg:block",
            sidebarDimmed && "opacity-55 saturate-50"
          )}
        >
          {sidebar}
        </aside>
      </div>
    </div>
  );
}

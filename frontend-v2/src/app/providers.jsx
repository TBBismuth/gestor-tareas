import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { RouterProvider } from "react-router-dom";
import { Toaster } from "sonner";
import { router } from "./router.jsx";
import { ThemeProvider, useTheme } from "./theme.jsx";
import { AuthProvider } from "../features/auth/AuthContext.jsx";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function AppToaster() {
  const { theme } = useTheme();

  return <Toaster richColors position="top-right" theme={theme} />;
}

export function AppProviders() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
        <AppToaster />
      </ThemeProvider>
    </QueryClientProvider>
  );
}

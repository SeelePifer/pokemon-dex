import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { AppShell } from './components/app-shell'
import CatalogPage from './pages/catalog-page'
import DetailPage from './pages/detail-page'
import LocalDetailPage from './pages/local-detail-page'
import LocalPage from './pages/local-page'
import LoginPage from './pages/login-page'
import RegisterPage from './pages/register-page'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route element={<AppShell />}>
            <Route index element={<CatalogPage />} />
            <Route path="pokemon/:name" element={<DetailPage />} />
            <Route path="local" element={<LocalPage />} />
            <Route path="local/:id" element={<LocalDetailPage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  )
}

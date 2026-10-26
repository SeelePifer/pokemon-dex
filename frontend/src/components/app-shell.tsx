import { Link, NavLink, Outlet } from 'react-router-dom'
import { useAuthStore } from '../stores/auth-store'

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `rounded-full px-3 py-1.5 text-sm ${isActive ? 'bg-amber text-ink' : 'text-mist hover:bg-line'}`

export function AppShell() {
  const user = useAuthStore((state) => state.user)
  const clear = useAuthStore((state) => state.clear)

  return (
    <div className="mx-auto min-h-screen max-w-6xl px-4 py-6">
      <header className="mb-8 flex flex-col gap-4 border-b border-line pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <Link to="/" className="text-2xl font-semibold tracking-tight text-white">
            Pokemon Dex
          </Link>
          <p className="text-sm text-slate-400">Live PokeAPI browse, with a local lab for proprietary fields.</p>
        </div>
        <nav className="flex flex-wrap items-center gap-2">
          <NavLink to="/" className={linkClass} end>
            Browse
          </NavLink>
          <NavLink to="/local" className={linkClass}>
            Local lab
          </NavLink>
          {user ? (
            <button type="button" className="rounded-full px-3 py-1.5 text-sm text-mist hover:bg-line" onClick={clear}>
              Sign out {user.displayName}
            </button>
          ) : (
            <NavLink to="/login" className={linkClass}>
              Sign in
            </NavLink>
          )}
        </nav>
      </header>
      <Outlet />
    </div>
  )
}

export function Field({
  label,
  value,
  onChange,
  type = 'text',
}: {
  label: string
  value: string
  onChange: (value: string) => void
  type?: string
}) {
  return (
    <label className="block text-sm">
      <span className="mb-1 block text-slate-300">{label}</span>
      <input
        type={type}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-lg border border-line bg-ink px-3 py-2 text-mist outline-none ring-amber focus:ring-2"
      />
    </label>
  )
}

export function FormError({ message }: { message: string | null }) {
  if (!message) {
    return null
  }
  return <p className="rounded-lg bg-red-950 px-3 py-2 text-sm text-red-200">{message}</p>
}

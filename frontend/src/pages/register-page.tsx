import { FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Field, FormError } from '../components/app-shell'
import { api, ApiError } from '../lib/api'
import { AuthUser, LoginResponse } from '../lib/types'
import { useAuthStore } from '../stores/auth-store'

export default function RegisterPage() {
  const navigate = useNavigate()
  const setSession = useAuthStore((state) => state.setSession)
  const [displayName, setDisplayName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    try {
      await api<AuthUser>('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify({ email, password, displayName }),
      })
      const session = await api<LoginResponse>('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      })
      setSession(session.accessToken, session.user)
      navigate('/')
    } catch (reason) {
      setError(reason instanceof ApiError ? reason.message : 'Registration failed')
    }
  }

  return (
    <form onSubmit={onSubmit} className="mx-auto max-w-md space-y-4 rounded-3xl bg-panel p-6">
      <h1 className="text-2xl text-white">Create account</h1>
      <Field label="Display name" value={displayName} onChange={setDisplayName} />
      <Field label="Email" type="email" value={email} onChange={setEmail} />
      <Field label="Password" type="password" value={password} onChange={setPassword} />
      <FormError message={error} />
      <button type="submit" className="rounded-full bg-amber px-4 py-2 font-medium text-ink">
        Register
      </button>
      <p className="text-sm">
        Already registered? <Link to="/login" className="text-amber">Sign in</Link>
      </p>
    </form>
  )
}

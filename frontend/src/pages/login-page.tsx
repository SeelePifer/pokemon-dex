import { FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Field, FormError } from '../components/app-shell'
import { api, ApiError } from '../lib/api'
import { LoginResponse } from '../lib/types'
import { useAuthStore } from '../stores/auth-store'

export default function LoginPage() {
  const navigate = useNavigate()
  const setSession = useAuthStore((state) => state.setSession)
  const [email, setEmail] = useState('demo@pokemon.local')
  const [password, setPassword] = useState('Demo1234!')
  const [error, setError] = useState<string | null>(null)

  async function onSubmit(event: FormEvent) {
    event.preventDefault()
    try {
      const result = await api<LoginResponse>('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password }),
      })
      setSession(result.accessToken, result.user)
      navigate('/')
    } catch (reason) {
      setError(reason instanceof ApiError ? reason.message : 'Sign in failed')
    }
  }

  return (
    <form onSubmit={onSubmit} className="mx-auto max-w-md space-y-4 rounded-3xl bg-panel p-6">
      <h1 className="text-2xl text-white">Sign in</h1>
      <p className="text-sm text-slate-400">Demo trainer: demo@pokemon.local / Demo1234!</p>
      <Field label="Email" type="email" value={email} onChange={setEmail} />
      <Field label="Password" type="password" value={password} onChange={setPassword} />
      <FormError message={error} />
      <button type="submit" className="rounded-full bg-amber px-4 py-2 font-medium text-ink">
        Sign in
      </button>
      <p className="text-sm">
        New here? <Link to="/register" className="text-amber">Create an account</Link>
      </p>
    </form>
  )
}

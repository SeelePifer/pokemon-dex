import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { api, ApiError } from '../lib/api'
import { PokemonDetail } from '../lib/types'
import { useAuthStore } from '../stores/auth-store'

export default function DetailPage() {
  const { name = '' } = useParams()
  const token = useAuthStore((state) => state.token)
  const queryClient = useQueryClient()
  const query = useQuery({
    queryKey: ['pokemon', name],
    queryFn: () => api<PokemonDetail>(`/api/v1/pokemon/${name}`),
  })
  const sync = useMutation({
    mutationFn: () => api(`/api/v1/local-pokemon/sync/${name}`, { method: 'POST' }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['local-pokemon'] }),
  })

  if (query.isLoading) {
    return <p>Loading details...</p>
  }
  if (query.isError || !query.data) {
    return <p className="text-red-300">That Pokemon could not be loaded.</p>
  }

  const pokemon = query.data
  const maxStat = Math.max(...pokemon.stats.map((stat) => stat.value), 1)

  return (
    <article className="grid gap-6 lg:grid-cols-[280px_1fr]">
      <img src={pokemon.imageUrl} alt={pokemon.name} className="w-full rounded-3xl bg-panel p-4" />
      <div>
        <h1 className="text-3xl capitalize text-white">{pokemon.name}</h1>
        <p className="mt-2 text-slate-300">{pokemon.description}</p>
        <p className="mt-3 text-sm text-slate-400">
          {pokemon.types.join(' / ')} · {pokemon.massKg} kg · {pokemon.skills.join(', ')}
        </p>
        <div className="mt-6 space-y-3">
          {pokemon.stats.map((stat) => (
            <div key={stat.name}>
              <div className="mb-1 flex justify-between text-sm capitalize">
                <span>{stat.name}</span>
                <span>{stat.value}</span>
              </div>
              <div className="h-2 rounded-full bg-ink">
                <div className="h-2 rounded-full bg-amber" style={{ width: `${(stat.value / maxStat) * 100}%` }} />
              </div>
            </div>
          ))}
        </div>
        <h2 className="mt-8 text-lg text-white">Evolution</h2>
        <div className="mt-3 flex flex-wrap gap-3">
          {pokemon.evolutions.map((stage) => (
            <Link key={stage.nationalId} to={`/pokemon/${stage.name}`} className="rounded-xl bg-panel px-3 py-2 text-center">
              <img src={stage.spriteUrl} alt="" className="mx-auto h-16 w-16" />
              <span className="text-sm capitalize">{stage.name}</span>
            </Link>
          ))}
        </div>
        <div className="mt-8">
          {token ? (
            <button
              type="button"
              className="rounded-full bg-amber px-4 py-2 font-medium text-ink disabled:opacity-60"
              disabled={sync.isPending}
              onClick={() => sync.mutate()}
            >
              {sync.isPending ? 'Saving...' : 'Save to local lab'}
            </button>
          ) : (
            <Link to="/login" className="text-amber">
              Sign in to save this Pokemon locally
            </Link>
          )}
          {sync.isError && (
            <p className="mt-2 text-sm text-red-300">{sync.error instanceof ApiError ? sync.error.message : 'Sync failed'}</p>
          )}
          {sync.isSuccess && <p className="mt-2 text-sm text-emerald-300">Saved. Proprietary fields stay intact on the next sync.</p>}
        </div>
      </div>
    </article>
  )
}

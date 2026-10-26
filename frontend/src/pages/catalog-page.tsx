import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { PokemonCard } from '../components/pokemon-card'
import { api } from '../lib/api'
import { Page, PokemonSummary } from '../lib/types'

export default function CatalogPage() {
  const [page, setPage] = useState(0)
  const query = useQuery({
    queryKey: ['pokemon', page],
    queryFn: () => api<Page<PokemonSummary>>(`/api/v1/pokemon?page=${page}&size=12`),
  })

  return (
    <section>
      <h1 className="mb-4 text-xl text-white">Browse</h1>
      {query.isLoading && <p>Loading Pokemon...</p>}
      {query.isError && <p className="text-red-300">The PokeAPI catalog is unavailable right now.</p>}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {query.data?.content.map((pokemon) => (
          <PokemonCard key={pokemon.nationalId} pokemon={pokemon} to={`/pokemon/${pokemon.name}`} />
        ))}
      </div>
      <div className="mt-6 flex items-center justify-between">
        <button
          type="button"
          className="rounded-full border border-line px-4 py-2 disabled:opacity-40"
          disabled={page === 0}
          onClick={() => setPage((current) => current - 1)}
        >
          Previous
        </button>
        <span className="text-sm text-slate-400">
          Page {page + 1} of {query.data?.totalPages ?? 1}
        </span>
        <button
          type="button"
          className="rounded-full border border-line px-4 py-2 disabled:opacity-40"
          disabled={!query.data || page + 1 >= query.data.totalPages}
          onClick={() => setPage((current) => current + 1)}
        >
          Next
        </button>
      </div>
    </section>
  )
}

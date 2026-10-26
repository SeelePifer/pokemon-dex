import { useQuery } from '@tanstack/react-query'
import { PokemonCard } from '../components/pokemon-card'
import { api } from '../lib/api'
import { LocalPokemon, Page, PokemonSummary } from '../lib/types'

function toSummary(pokemon: LocalPokemon): PokemonSummary {
  return {
    nationalId: pokemon.nationalId,
    name: pokemon.localizedName,
    spriteUrl: pokemon.spriteUrl,
    types: pokemon.types,
    massKg: pokemon.massKg,
    skills: pokemon.skills,
  }
}

export default function LocalPage() {
  const query = useQuery({
    queryKey: ['local-pokemon'],
    queryFn: () => api<Page<LocalPokemon>>('/api/v1/local-pokemon?page=0&size=20'),
  })

  return (
    <section>
      <h1 className="mb-2 text-xl text-white">Local lab</h1>
      <p className="mb-4 text-sm text-slate-400">Seeded starters plus anything you sync. Open a card to edit region, tags, and notes.</p>
      {query.isLoading && <p>Loading the local catalog...</p>}
      {query.isError && <p className="text-red-300">The local catalog could not be loaded.</p>}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {query.data?.content.map((pokemon) => (
          <div key={pokemon.id} className="space-y-2">
            <PokemonCard pokemon={toSummary(pokemon)} to={`/local/${pokemon.id}`} />
            <p className="px-2 text-xs text-slate-400">
              {pokemon.region} · {pokemon.tags.join(', ')}
            </p>
          </div>
        ))}
      </div>
    </section>
  )
}

import { Link } from 'react-router-dom'
import { PokemonSummary } from '../lib/types'

export function PokemonCard({ pokemon, to }: { pokemon: PokemonSummary; to: string }) {
  return (
    <Link to={to} className="rounded-2xl border border-line bg-panel p-4 transition hover:-translate-y-0.5 hover:border-amber">
      <img src={pokemon.spriteUrl} alt="" className="mx-auto h-28 w-28 object-contain" />
      <h2 className="mt-2 text-lg capitalize text-white">{pokemon.name}</h2>
      <p className="text-sm text-slate-400">#{pokemon.nationalId} · {pokemon.massKg} kg</p>
      <div className="mt-3 flex flex-wrap gap-2">
        {pokemon.types.map((type) => (
          <span key={type} className="rounded-full bg-ink px-2 py-1 text-xs capitalize text-amber">
            {type}
          </span>
        ))}
      </div>
      <p className="mt-3 text-sm text-slate-300">Skills: {pokemon.skills.join(', ')}</p>
    </Link>
  )
}

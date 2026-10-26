export interface Page<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface PokemonSummary {
  nationalId: number
  name: string
  spriteUrl: string
  types: string[]
  massKg: number
  skills: string[]
}

export interface PokemonStat {
  name: string
  value: number
}

export interface Evolution {
  nationalId: number
  name: string
  spriteUrl: string
}

export interface PokemonDetail {
  nationalId: number
  name: string
  imageUrl: string
  spriteUrl: string
  types: string[]
  massKg: number
  skills: string[]
  stats: PokemonStat[]
  description: string
  evolutions: Evolution[]
}

export interface LocalPokemon extends PokemonDetail {
  id: string
  localizedName: string
  region: string
  tags: string[]
  notes: string
  updatedAt: string
}

export interface AuthUser {
  id: string
  email: string
  displayName: string
  role: 'USER' | 'ADMIN'
}

export interface LoginResponse {
  accessToken: string
  expiresInSeconds: number
  user: AuthUser
}

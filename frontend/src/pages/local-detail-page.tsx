import { FormEvent, useEffect, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Field, FormError } from '../components/app-shell'
import { api, ApiError } from '../lib/api'
import { LocalPokemon } from '../lib/types'
import { useAuthStore } from '../stores/auth-store'

export default function LocalDetailPage() {
  const { id = '' } = useParams()
  const navigate = useNavigate()
  const token = useAuthStore((state) => state.token)
  const queryClient = useQueryClient()
  const query = useQuery({
    queryKey: ['local-pokemon', id],
    queryFn: () => api<LocalPokemon>(`/api/v1/local-pokemon/${id}`),
  })
  const [localizedName, setLocalizedName] = useState('')
  const [region, setRegion] = useState('')
  const [tags, setTags] = useState('')
  const [notes, setNotes] = useState('')
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!query.data) {
      return
    }
    setLocalizedName(query.data.localizedName)
    setRegion(query.data.region)
    setTags(query.data.tags.join(', '))
    setNotes(query.data.notes)
  }, [query.data])

  const save = useMutation({
    mutationFn: () =>
      api(`/api/v1/local-pokemon/${id}`, {
        method: 'PUT',
        body: JSON.stringify({
          localizedName,
          region,
          tags: tags.split(',').map((tag) => tag.trim()).filter(Boolean),
          notes,
        }),
      }),
    onSuccess: async () => {
      setError(null)
      await queryClient.invalidateQueries({ queryKey: ['local-pokemon'] })
    },
    onError: (reason) => setError(reason instanceof ApiError ? reason.message : 'Update failed'),
  })

  const remove = useMutation({
    mutationFn: () => api(`/api/v1/local-pokemon/${id}`, { method: 'DELETE' }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['local-pokemon'] })
      navigate('/local')
    },
    onError: (reason) => setError(reason instanceof ApiError ? reason.message : 'Delete failed'),
  })

  if (query.isLoading) {
    return <p>Loading local record...</p>
  }
  if (query.isError || !query.data) {
    return <p className="text-red-300">That local record was not found.</p>
  }

  function onSubmit(event: FormEvent) {
    event.preventDefault()
    save.mutate()
  }

  return (
    <section className="grid gap-6 lg:grid-cols-[240px_1fr]">
      <img src={query.data.imageUrl} alt={query.data.name} className="rounded-3xl bg-panel p-4" />
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <h1 className="text-2xl capitalize text-white">{query.data.name}</h1>
          <p className="text-sm text-slate-400">{query.data.description}</p>
        </div>
        <Field label="Localized name" value={localizedName} onChange={setLocalizedName} />
        <Field label="Region" value={region} onChange={setRegion} />
        <Field label="Tags" value={tags} onChange={setTags} />
        <label className="block text-sm">
          <span className="mb-1 block text-slate-300">Notes</span>
          <textarea
            value={notes}
            onChange={(event) => setNotes(event.target.value)}
            className="min-h-28 w-full rounded-lg border border-line bg-ink px-3 py-2 text-mist outline-none ring-amber focus:ring-2"
          />
        </label>
        <FormError message={error} />
        {save.isSuccess && <p className="text-sm text-emerald-300">Saved.</p>}
        {token ? (
          <div className="flex flex-wrap gap-3">
            <button type="submit" className="rounded-full bg-amber px-4 py-2 font-medium text-ink" disabled={save.isPending}>
              Save changes
            </button>
            <button type="button" className="rounded-full border border-line px-4 py-2" onClick={() => remove.mutate()}>
              Delete local copy
            </button>
          </div>
        ) : (
          <Link to="/login" className="text-amber">
            Sign in to edit this record
          </Link>
        )}
      </form>
    </section>
  )
}

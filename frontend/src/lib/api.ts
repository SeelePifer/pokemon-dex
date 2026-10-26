import { useAuthStore } from '../stores/auth-store'

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

export async function api<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = useAuthStore.getState().token
  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  if (options.body) {
    headers.set('Content-Type', 'application/json')
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(path, { ...options, headers })
  if (!response.ok) {
    let message = response.statusText
    try {
      const body = await response.json()
      message = body.message ?? message
      if (Array.isArray(body.fieldErrors) && body.fieldErrors.length > 0) {
        message = body.fieldErrors
          .map((item: { field: string; message: string }) => `${item.field}: ${item.message}`)
          .join(', ')
      }
    } catch {
      message = response.statusText
    }
    if (response.status === 401 && token) {
      useAuthStore.getState().clear()
    }
    throw new ApiError(response.status, message)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return response.json() as Promise<T>
}

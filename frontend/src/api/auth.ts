import client from './client'

export interface LoginResponse {
  token: string
  username: string
  role: string
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const res = await client.post<LoginResponse>('/auth/login', { username, password })
  return res.data
}
const BASE_URL = 'http://localhost:8080/api';

const authHeaders = () => ({
  'Content-Type': 'application/json',
  'X-Auth-Token': localStorage.getItem('token') || ''
});

export async function login(username, password) {
  const res = await fetch(`${BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  if (!res.ok) throw new Error('Invalid credentials');
  return res.json();
}

export async function getTrees() {
  const res = await fetch(`${BASE_URL}/trees`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load trees');
  return res.json();
}

export async function createTree(name) {
  const res = await fetch(`${BASE_URL}/trees`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name })
  });
  if (!res.ok) throw new Error('Failed to create tree');
  return res.json();
}

export async function getMembers(treeId) {
  const res = await fetch(`${BASE_URL}/trees/${treeId}/members`, { headers: authHeaders() });
  if (!res.ok) throw new Error('Failed to load members');
  return res.json();
}

export async function createMember(treeId, payload) {
  const res = await fetch(`${BASE_URL}/trees/${treeId}/members`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(payload)
  });
  if (!res.ok) throw new Error('Failed to add member');
  return res.json();
}

export async function deleteMember(memberId) {
  const res = await fetch(`${BASE_URL}/trees/members/${memberId}`, {
    method: 'DELETE',
    headers: authHeaders()
  });
  if (!res.ok) throw new Error('Failed to delete member');
}

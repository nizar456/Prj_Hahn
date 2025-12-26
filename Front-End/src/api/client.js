const API_BASE =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8087/api";

async function request(path, { method = "GET", body, token } = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      "Content-Type": "application/json",
      //Authorization: token ? `Bearer ${token}` : undefined ❌
      // car Certains serveurs refusent Authorization: undefined
      // donc avec l'opérateur spread on évite d'envoyer cette clé si token est falsy
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const err = await res.json();
      message = err?.message || err?.error || message;
    } catch (_) {
      // ignore JSON parse issues
    }
    const error = new Error(message);
    error.status = res.status;
    throw error;
  }

  //No Content response "204"
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  login: (email, password) =>
    request("/auth/login", { method: "POST", body: { email, password } }),
  
  signUp: (email, password) =>
    request("/auth/sign-in", { method: "POST", body: { email, password } }),

  listProjects: (token) => request("/projects", { token }),
  
  createProject: (token, payload) =>
    request("/projects", { method: "POST", body: payload, token }),
  
  updateProject: (token, projectId, payload) =>
    request(`/projects/${projectId}`, { method: "PUT", body: payload, token }),
  
  deleteProject: (token, projectId) =>
    request(`/projects/${projectId}`, { method: "DELETE", token }),

  listTasks: (token, projectId) =>
    request(`/projects/${projectId}/tasks`, { token }),
  
  createTask: (token, projectId, payload) =>
    request(`/projects/${projectId}/tasks`, {
      method: "POST",
      body: payload,
      token,
    }),

  updateTask: (token, projectId, taskId, payload) =>
    request(`/projects/${projectId}/tasks/${taskId}`, {
      method: "PUT",
      body: payload,
      token,
    }),

  deleteTask: (token, projectId, taskId) =>
    request(`/projects/${projectId}/tasks/${taskId}`, {
      method: "DELETE",
      token,
    }),
};

import axios from "axios";
import keycloak from "./services/keycloak";

const http = axios.create({
  baseURL: `${import.meta.env.VITE_BACKEND_URL ?? ""}/api`,
  headers: { "Content-Type": "application/json" },
});

http.interceptors.request.use(async (config) => {
if (keycloak.authenticated) {
await keycloak.updateToken(30).catch(() => keycloak.login());
config.headers.Authorization = `Bearer ${keycloak.token}`;
}
return config;
});

export default http;
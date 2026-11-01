export function errorMessage(e){
const status = e.response?.status;
if (status == 401) return "Sesion expired. Login again.";
if (status == 403) return "No tienes permiso para realizar esta acción.";
return e.response?.data?.message ?? "No se pudo conectar con el servidor";
}
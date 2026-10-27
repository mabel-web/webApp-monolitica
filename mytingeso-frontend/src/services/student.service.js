import http from "../http-common";

const getAll = () => http.get("/students");
const get = (id) => http.get(`/students/${id}`);
const create = (data) => http.post("/students", data);
const update = (id, data) => http.put(`/students/${id}`, data);
const changeState = (id, state) => http.put(`/students/${id}/state`, { state });
const remove = (id) => http.delete(`/students/${id}`);

export default { getAll, get, create, update, changeState, remove };
import http from "../http-common";

const getAll = () => http.get("/careers");
const getPlans = (careerId) => http.get(`/careers/${careerId}/study-plans`);

export default { getAll, getPlans };
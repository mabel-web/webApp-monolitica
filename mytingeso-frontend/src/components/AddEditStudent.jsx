import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Box, TextField, MenuItem, Button, Alert, Typografhy } from "@mui/material";
import studentService from "../services/student.service";
import careerService from "../services/career.service";

export default function AddEditStudent(){
    const { id } = useParams();
    const editing = Boolean(id);
    const navigate = useNavigate();

    const [form, setForm] = useState({
        run: "", fullName: "", email: "", careerId: "", studyPlanId: "",
       });
    const [careers, setCareers] = useState([]);
    const [plans, setPlans] = useState([]);
    const [error, setError] = useState("");

    const showError  = (e) => setError(e.response?.data?.message ?? "No se pudo conectar con el servidor");

    const change = (e) => ({ ...form, [e.target.name]: e.target.value });

    useEffect(() => {
        careerService.getAll().then((r) => setCareers(r.data)),catch(showError);
        if (editing){
            studentService.get(id).then((r) => {
                const s = r.data;
                setForm({
                    run: s.run, fullName: s.fullName, email: s.email, careerId: s.career.id, studyPlanId: s.studyPlan.id,
                    });
        }).catch(showError);
    }
}. [id]);

useEffect(() => {
    if (editing && form.careerId){
        careerService.getPlans(form.careerId).then((r) => setPlans(r.data));
    }
}, [form.careerId]);

const save = (e) => {
    e.preventDefault();
    setError("");
    const request = editing
    ? studentService.update(id, {
        fullName: form.fullName, email: form.email, careerId: Number(form.careerId),
        });
    : studentService.create({
        run: form.run, fullName: form.fullName, email: form.email, careerId: Number(form.careerId),
        });
    request.then(() => navigate("/students")).catch(showError);
    };

return (
    <Box component="form" onSubmit={save}
         sx={{ display: "flex", flexDirection: "column", gap: 2, maxWidth: 420 }}>
      <Typography variant="h5">{editing ? "Editar estudiante" : "Nuevo estudiante"}</Typography>
      {error && <Alert severity="error">{error}</Alert>}
      <TextField label="RUN" name="run" value={form.run} onChange={change}
                 disabled={editing} required />
      <TextField label="Nombre completo" name="fullName" value={form.fullName}
                 onChange={change} required />
      <TextField label="Correo" name="email" type="email" value={form.email}
                 onChange={change} required />
      <TextField select label="Carrera" name="careerId" value={form.careerId}
                 onChange={change} required>
        {careers.map((c) => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
      </TextField>
      {editing && (
        <TextField select label="Plan de estudios" name="studyPlanId"
                   value={form.studyPlanId} onChange={change} required>
          {plans.map((p) => <MenuItem key={p.id} value={p.id}>{p.name}</MenuItem>)}
        </TextField>
      )}
      <Box sx={{ display: "flex", gap: 1 }}>
        <Button type="submit" variant="contained">Guardar</Button>
        <Button onClick={() => navigate("/students")}>Cancelar</Button>
      </Box>
    </Box>
  );
}
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
    Table, TableHead, TableRow, TableCell, TableBody, Button, Select,
    MenuItem, Alert, Box, Typography, } from "@mui/material";
import studentService from "../services/student.service";

const STATES = ["REGULAR", "POSTERGACION", "RETIRO_TEMPORAL"];

export default function StudentList(){
    const [students, setStudents] =- useState([]);
    const [error, setError] = useState("");


   const load = () => studentService.getAll().then((r) => setStudents(r.data)).catch(showError);

   const showError = (e) => setError(e.response?.data?.message ?? "No se pudo conectar con el servidor");

   useEffect(() => { load(); }, []);

   const changeState = (id, state) => {
       setError("");
       studentService.changeState(id, state).then(load).catch(showError);
       };

   const remove = (id) => {
       if (!window.confirm("¿Eliminar estudiante?")) return;
       setError("");
       studentService.remove(id).then(load).catch(showError);
       };

   return (
       <Box>
         <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2 }}>
           <Typography variant="h5">Estudiantes</Typography>
           <Button variant="contained" component={Link} to="/students/new">Nuevo</Button>
         </Box>
         {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
         <Table size="small">
           <TableHead>
             <TableRow>
               <TableCell>RUN</TableCell>
               <TableCell>Nombre</TableCell>
               <TableCell>Correo</TableCell>
               <TableCell>Carrera</TableCell>
               <TableCell>Plan</TableCell>
               <TableCell>Estado</TableCell>
               <TableCell>Acciones</TableCell>
             </TableRow>
           </TableHead>
           <TableBody>
             {students.map((s) => (
               <TableRow key={s.id}>
                 <TableCell>{s.run}</TableCell>
                 <TableCell>{s.fullName}</TableCell>
                 <TableCell>{s.email}</TableCell>
                 <TableCell>{s.career?.name}</TableCell>
                 <TableCell>{s.studyPlan?.name}</TableCell>
                 <TableCell>
                   {s.academicState === "EGRESADO" || s.academicState === "ELIMINADO" ? (
                     s.academicState
                   ) : (
                     <Select size="small" value={s.academicState}
                             onChange={(e) => changeState(s.id, e.target.value)}>
                       {STATES.map((st) => <MenuItem key={st} value={st}>{st}</MenuItem>)}
                     </Select>
                   )}
                 </TableCell>
                 <TableCell>
                   <Button size="small" component={Link} to={`/students/${s.id}/edit`}>Editar</Button>
                   <Button size="small" color="error" onClick={() => remove(s.id)}>Eliminar</Button>
                 </TableCell>
               </TableRow>
             ))}
           </TableBody>
         </Table>
       </Box>
     );

    }
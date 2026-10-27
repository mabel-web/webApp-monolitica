import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import Navbar from "./components/Navbar";
import StudentList from "./components/StudentList";
import AddEditStudent from "./components/AddEditStudent";

export default function App() {
  return (
    <Router>
      <Navbar />
      <div style={{ padding: 24 }}>
        <Routes>
          <Route path="/" element={<Navigate to="/students" />} />
          <Route path="/students" element={<StudentList />} />
          <Route path="/students/new" element={<AddEditStudent />} />
          <Route path="/students/:id/edit" element={<AddEditStudent />} />
        </Routes>
      </div>
    </Router>
  );
}
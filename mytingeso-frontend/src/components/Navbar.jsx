import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { Link } from "react-router-dom";
import { useKeycloak } from "@react-keycloak/web";

export default function Navbar() {
    const { keycloak} = useKeycloak();
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1 }}>SIGA IPT</Typography>
        <Button color="inherit" component={Link} to="/students">Estudiantes</Button>
        <Button color = "inherit"
                onClick={() => keycloak.logout({ redirectUri: window.location.origin })}>Salir</Button>
      </Toolbar>
    </AppBar>
  );
}
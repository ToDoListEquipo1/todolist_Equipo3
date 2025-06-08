package example.ToDoApp.controlador;

import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.FileStore;

@Controller
public class UsuarioAdminController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/usuarios/{id}/bloqueo")
    public String Bloqueo(@PathVariable Long id , HttpSession session) {

        UsuarioData admin = (UsuarioData) session.getAttribute("usuario");

        if (admin == null || !Boolean.TRUE.equals(admin.getEsAdministrador())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autorizado.");
        }

        usuarioService.Bloqueo(id);
        return "redirect:/registrados";
    }
}

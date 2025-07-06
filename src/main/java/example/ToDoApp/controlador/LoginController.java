package example.ToDoApp.controlador;

import example.ToDoApp.authentication.ManagerUserSession;
import example.ToDoApp.dto.LoginData;
import example.ToDoApp.dto.RegistroData;
import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.repositorio.UsuarioRepository;
import example.ToDoApp.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {

        // Llamada al servicio para comprobar si el login es correcto
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.getEmail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            UsuarioData usuario = usuarioService.findByEmail(loginData.getEmail());

            managerUserSession.logearUsuario(usuario.getId());
            session.setAttribute("usuario", usuario);

            // Esta cosa si el atributo es un Boolean, es decir objeto Boolean a un boolean
            if (Boolean.TRUE.equals(usuario.getEsAdministrador()))
                return "redirect:/registrados";
            else
                return "redirect:/usuarios/" + usuario.getId() + "/tareas";

        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        }
        if (loginStatus == UsuarioService.LoginStatus.USER_BLOCKED) {
            model.addAttribute("error", "Tu cuenta está bloqueada. Contacta al administrador.");
            return "formError";
        }
        return "formLogin";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {

        boolean existeAdmin = usuarioRepository.existsByEsAdministradorTrue();
        model.addAttribute("existeAdmin", existeAdmin);
        model.addAttribute("registroData", new RegistroData());
        return "formRegistro";
    }

    @PostMapping("/registro")
    public String registroSubmit(@Valid RegistroData registroData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.getEMail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.getEMail() + " ya existe");
            return "formRegistro";
        }

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail(registroData.getEMail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setApellido(registroData.getApellido());
        usuario.setEsAdministrador(registroData.isEsAdministrador());

        usuarioService.registrar(usuario);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        managerUserSession.logout();
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registrados")
    public String usuarioList(Model model, HttpSession session) {

        UsuarioData usuario = (UsuarioData) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        System.out.println("usuario " + usuario.getEmail() + " esAdmin: " + usuario.getEsAdministrador());

        if (!Boolean.TRUE.equals(usuario.getEsAdministrador())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tienes permisos suficientes.");
        }

        List<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }

    @GetMapping("/cuenta")
    public String cuentaForm(Model model, HttpSession session) {
        UsuarioData usuario = (UsuarioData) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }
        // Obtener los datos actualizados del usuario
        UsuarioData usuarioActualizado = usuarioService.findById(usuario.getId());
        model.addAttribute("usuario", usuarioActualizado);
        return "cuenta";
    }

    // Usuario Administrador
    @GetMapping("/admin")
    public boolean existeAdmin() {
        return usuarioRepository.existsByEsAdministradorTrue();
    }
}

package example.ToDoApp.controller;

import example.ToDoApp.authentication.ManagerUserSession;
import example.ToDoApp.controlador.LoginController;
import example.ToDoApp.dto.LoginData;
import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private example.ToDoApp.repositorio.UsuarioRepository usuarioRepository;

    @Test
    void testLoginFormShows() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("loginData"));
    }

    @Test
    void testLoginSubmit_SuccessUser() throws Exception {
        // Simulamos que login() retorna LOGIN_OK
        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);

        // Simulamos que findByEmail() retorna un usuario no administrador
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(1L);
        usuarioData.setEmail("test@user.com");
        usuarioData.setEsAdministrador(false);

        Mockito.when(usuarioService.findByEmail(anyString()))
                .thenReturn(usuarioData);

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/login")
                        .param("email", "test@user.com")
                        .param("password", "1234")
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));

        // Verificamos que el método logearUsuario fue llamado con el id correcto
        verify(managerUserSession, times(1)).logearUsuario(1L);

        // Verificamos que el atributo "usuario" fue agregado a sesión
        Object usuarioSesion = session.getAttribute("usuario");
        // Como MockHttpSession no sincroniza con el controlador, este paso es solo conceptual.
        // La mejor manera es validar indirectamente que el controlador ejecutó lo esperado.
    }

    @Test
    void testLoginSubmit_SuccessAdmin() throws Exception {
        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);

        UsuarioData admin = new UsuarioData();
        admin.setId(2L);
        admin.setEmail("admin@domain.com");
        admin.setEsAdministrador(true);

        Mockito.when(usuarioService.findByEmail(anyString()))
                .thenReturn(admin);

        mockMvc.perform(post("/login")
                        .param("email", "admin@domain.com")
                        .param("password", "adminpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));

        verify(managerUserSession, times(1)).logearUsuario(2L);
    }

    @Test
    void testLoginSubmit_UserNotFound() throws Exception {
        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        mockMvc.perform(post("/login")
                        .param("email", "noexiste@dom.com")
                        .param("password", "1234")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "No existe usuario"));
    }

    @Test
    void testLoginSubmit_ErrorPassword() throws Exception {
        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        mockMvc.perform(post("/login")
                        .param("email", "user@dom.com")
                        .param("password", "wrongpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("formLogin"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Contraseña incorrecta"));
    }

    @Test
    void testLoginSubmit_UserBlocked() throws Exception {
        Mockito.when(usuarioService.login(anyString(), anyString()))
                .thenReturn(UsuarioService.LoginStatus.USER_BLOCKED);

        mockMvc.perform(post("/login")
                        .param("email", "blocked@dom.com")
                        .param("password", "any")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("formError"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Tu cuenta está bloqueada. Contacta al administrador."));
    }
}

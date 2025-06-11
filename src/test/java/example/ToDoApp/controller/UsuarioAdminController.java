package example.ToDoApp.controller;

import example.ToDoApp.controlador.UsuarioAdminController;
import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioAdminControllerTest {

    @InjectMocks
    private UsuarioAdminController usuarioAdminController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bloqueo_usuarioAdministrador_bloqueaYRedirige() {
        Long idUsuarioABloquear = 5L;

        UsuarioData admin = new UsuarioData();
        admin.setEsAdministrador(true);

        when(session.getAttribute("usuario")).thenReturn(admin);

        String resultado = usuarioAdminController.Bloqueo(idUsuarioABloquear, session);

        verify(usuarioService).Bloqueo(idUsuarioABloquear);
        assertEquals("redirect:/registrados", resultado);
    }

    @Test
    void bloqueo_usuarioNoLogeado_lanzaUnauthorized() {
        Long idUsuarioABloquear = 5L;

        when(session.getAttribute("usuario")).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            usuarioAdminController.Bloqueo(idUsuarioABloquear, session);
        });

        assertEquals(401, ex.getStatusCode().value());
        assertEquals("No autorizado.", ex.getReason());
        verify(usuarioService, never()).Bloqueo(anyLong());
    }
}
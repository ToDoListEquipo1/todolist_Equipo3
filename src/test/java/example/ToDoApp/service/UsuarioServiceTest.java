package example.ToDoApp.service;

import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.model.Usuario;
import example.ToDoApp.repositorio.UsuarioRepository;
import example.ToDoApp.servicio.UsuarioService;
import example.ToDoApp.servicio.UsuarioServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static example.ToDoApp.servicio.UsuarioService.LoginStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    public void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        modelMapper = new ModelMapper();
        usuarioService = new UsuarioService();
        usuarioRepository = usuarioRepository;
        modelMapper = modelMapper;

    }

    @Test
    public void testLoginOk() {
        Usuario usuario = new Usuario("test@example.com");
        usuario.setPassword("123");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        assertEquals(LoginStatus.LOGIN_OK, usuarioService.login("test@example.com", "123"));
    }

    @Test
    public void testLoginUserNotFound() {
        when(usuarioRepository.findByEmail("no@user.com")).thenReturn(Optional.empty());

        assertEquals(LoginStatus.USER_NOT_FOUND, usuarioService.login("no@user.com", "123"));
    }

    @Test
    public void testLoginUserBlocked() {
        Usuario usuario = new Usuario("blocked@example.com");
        usuario.setBloqueado(true);
        usuario.setPassword("123");
        when(usuarioRepository.findByEmail("blocked@example.com")).thenReturn(Optional.of(usuario));

        assertEquals(LoginStatus.USER_BLOCKED, usuarioService.login("blocked@example.com", "123"));
    }

    @Test
    public void testLoginWrongPassword() {
        Usuario usuario = new Usuario("test@example.com");
        usuario.setPassword("123");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        assertEquals(LoginStatus.ERROR_PASSWORD, usuarioService.login("test@example.com", "wrong"));
    }

    @Test
    public void testRegistrarUsuarioCorrecto() {
        UsuarioData dto = new UsuarioData();
        dto.setEmail("new@user.com");
        dto.setPassword("pass");
        dto.setEsAdministrador(false);

        when(usuarioRepository.findByEmail("new@user.com")).thenReturn(Optional.empty());
        when(usuarioRepository.existsByEmail("new@user.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioData saved = usuarioService.registrar(dto);

        assertEquals(dto.getEmail(), saved.getEmail());
    }

    @Test
    public void testRegistrarUsuarioYaExiste() {
        UsuarioData dto = new UsuarioData();
        dto.setEmail("exists@user.com");

        when(usuarioRepository.findByEmail("exists@user.com"))
                .thenReturn(Optional.of(new Usuario("exists@user.com")));

        assertThrows(UsuarioServiceException.class, () -> usuarioService.registrar(dto));
    }

    @Test
    public void testFindByEmailFound() {
        Usuario usuario = new Usuario("email@domain.com");
        usuario.setNombre("Test");
        when(usuarioRepository.findByEmail("email@domain.com")).thenReturn(Optional.of(usuario));

        UsuarioData result = usuarioService.findByEmail("email@domain.com");
        assertEquals("email@domain.com", result.getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        when(usuarioRepository.findByEmail("missing@domain.com")).thenReturn(Optional.empty());

        assertNull(usuarioService.findByEmail("missing@domain.com"));
    }

    @Test
    public void testFindById() {
        Usuario usuario = new Usuario("id@domain.com");
        usuario.setId(1);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioData result = usuarioService.findById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testFindAll() {
        Usuario u1 = new Usuario("a@b.com");
        Usuario u2 = new Usuario("c@d.com");
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UsuarioData> usuarios = usuarioService.findAll();
        assertEquals(2, usuarios.size());
    }

    @Test
    public void testBloqueoUsuario() {
        Usuario usuario = new Usuario("block@domain.com");
        usuario.setId(5);
        usuario.setBloqueado(false);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.Bloqueo(5L);

        assertTrue(usuario.getBloqueado());
    }

    @Test
    public void testBloqueoUsuarioNoExiste() {
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UsuarioServiceException.class, () -> usuarioService.Bloqueo(999L));
    }
}

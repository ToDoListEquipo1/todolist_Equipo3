package example.ToDoApp.service;

import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.model.Usuario;
import example.ToDoApp.repositorio.UsuarioRepository;
import example.ToDoApp.servicio.UsuarioService;
import example.ToDoApp.servicio.UsuarioService.LoginStatus;
import example.ToDoApp.servicio.UsuarioServiceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static example.ToDoApp.servicio.UsuarioService.LoginStatus;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginOk() {
        Usuario u = new Usuario("test@example.com");
        u.setPassword("123");
        when(usuarioRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(u));

        assertEquals(LoginStatus.LOGIN_OK,
                usuarioService.login("test@example.com", "123"));
    }

    @Test
    void testLoginUserNotFound() {
        when(usuarioRepository.findByEmail("no@user.com"))
                .thenReturn(Optional.empty());

        assertEquals(LoginStatus.USER_NOT_FOUND,
                usuarioService.login("no@user.com", "123"));
    }

    @Test
    void testLoginUserBlocked() {
        Usuario u = new Usuario("blocked@example.com");
        u.setBloqueado(true);
        u.setPassword("123");
        when(usuarioRepository.findByEmail("blocked@example.com"))
                .thenReturn(Optional.of(u));

        assertEquals(LoginStatus.USER_BLOCKED,
                usuarioService.login("blocked@example.com", "123"));
    }

    @Test
    void testLoginWrongPassword() {
        Usuario u = new Usuario("test@example.com");
        u.setPassword("123");
        when(usuarioRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(u));

        assertEquals(LoginStatus.ERROR_PASSWORD,
                usuarioService.login("test@example.com", "wrong"));
    }

    @Test
    void testRegistrarUsuarioCorrecto() {
        UsuarioData dto = new UsuarioData();
        dto.setEmail("new@user.com");
        dto.setPassword("pass");
        dto.setEsAdministrador(false);
        when(usuarioRepository.findByEmail("new@user.com"))
                .thenReturn(Optional.empty());
        when(usuarioRepository.existsByEmail("new@user.com"))
                .thenReturn(false);
        when(usuarioRepository.existsByEsAdministradorTrue())
                .thenReturn(false);
        Usuario mockUsuarioEntity = new Usuario("new@user.com");
        when(modelMapper.map(dto, Usuario.class))
                .thenReturn(mockUsuarioEntity);
        when(usuarioRepository.save(mockUsuarioEntity))
                .thenReturn(mockUsuarioEntity);
        when(modelMapper.map(mockUsuarioEntity, UsuarioData.class))
                .thenReturn(dto);
        UsuarioData saved = usuarioService.registrar(dto);
        assertNotNull(saved);
        assertEquals("new@user.com", saved.getEmail());
    }

    @Test
    void testRegistrarUsuarioYaExiste() {
        UsuarioData dto = new UsuarioData();
        dto.setEmail("exists@user.com");

        when(usuarioRepository.findByEmail("exists@user.com"))
                .thenReturn(Optional.of(new Usuario("exists@user.com")));

        assertThrows(UsuarioServiceException.class,
                () -> usuarioService.registrar(dto));
    }

    @Test
    void testFindByEmailFound() {
        Usuario u = new Usuario("email@domain.com");
        when(usuarioRepository.findByEmail("email@domain.com"))
                .thenReturn(Optional.of(u));
        UsuarioData dto = new UsuarioData();
        when(modelMapper.map(u, UsuarioData.class)).thenReturn(dto);

        assertEquals(dto, usuarioService.findByEmail("email@domain.com"));
    }

    @Test
    void testFindByEmailNotFound() {
        when(usuarioRepository.findByEmail("missing@domain.com"))
                .thenReturn(Optional.empty());

        assertNull(usuarioService.findByEmail("missing@domain.com"));
    }

    @Test
    void testFindById() {
        Usuario u = new Usuario("id@domain.com");
        u.setId(1);
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(u));
        UsuarioData dto = new UsuarioData();
        when(modelMapper.map(u, UsuarioData.class)).thenReturn(dto);

        assertEquals(dto, usuarioService.findById(1L));
    }

    @Test
    void testFindAll() {
        Usuario u1 = new Usuario("a@b.com"), u2 = new Usuario("c@d.com");
        when(usuarioRepository.findAll())
                .thenReturn(Arrays.asList(u1, u2));
        UsuarioData d1 = new UsuarioData(), d2 = new UsuarioData();
        when(modelMapper.map(u1, UsuarioData.class)).thenReturn(d1);
        when(modelMapper.map(u2, UsuarioData.class)).thenReturn(d2);

        List<UsuarioData> list = usuarioService.findAll();
        assertEquals(2, list.size());
    }

    @Test
    void testBloqueoUsuario() {
        Usuario u = new Usuario("block@domain.com");
        u.setId(5);
        u.setBloqueado(false);
        when(usuarioRepository.findById(5L))
                .thenReturn(Optional.of(u));
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        usuarioService.Bloqueo(5L);
        assertTrue(u.getBloqueado());
    }

    @Test
    void testBloqueoUsuarioNoExiste() {
        when(usuarioRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioServiceException.class,
                () -> usuarioService.Bloqueo(999L));
    }
}
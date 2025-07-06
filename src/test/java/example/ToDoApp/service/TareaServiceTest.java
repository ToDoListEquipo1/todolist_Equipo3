package example.ToDoApp.service;

import example.ToDoApp.dto.TareaData;
import example.ToDoApp.model.Tarea;
import example.ToDoApp.model.Usuario;
import example.ToDoApp.repositorio.TareaRepository;
import example.ToDoApp.repositorio.UsuarioRepository;
import example.ToDoApp.servicio.TareaService;
import example.ToDoApp.servicio.TareaServiceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TareaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TareaRepository tareaRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TareaService tareaService;

    private Usuario usuario;
    private Tarea tarea;
    private TareaData tareaData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1);
        tarea = new Tarea(usuario, "Tarea de prueba", "Descripción de prueba");
        tarea.setId(100L);

        tareaData = new TareaData();
        tareaData.setId(100L);
        tareaData.setTitulo("Tarea de prueba");
        tareaData.setDescripcion("Descripción de prueba");
    }

    @Test
    void testNuevaTareaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);
        when(modelMapper.map(any(Tarea.class), eq(TareaData.class))).thenReturn(tareaData);

        TareaData result = tareaService.nuevaTareaUsuario(1L, "Tarea de prueba", "Descripción de prueba");
        assertEquals(100L, result.getId());
        assertEquals("Tarea de prueba", result.getTitulo());
        assertEquals("Descripción de prueba", result.getDescripcion());
    }

    @Test
    void testNuevaTareaUsuarioUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class,
                () -> tareaService.nuevaTareaUsuario(99L, "Tarea inválida", "Descripción inválida"));

        // El servicio lanza "Usuario no existe"
        assertTrue(exception.getMessage().contains("Usuario no existe"));
    }

    @Test
    void testAllTareasUsuario() {
        Tarea otra = new Tarea(usuario, "Otra tarea", "Otra descripción");
        otra.setId(5L);
        // Añadimos las tareas al conjunto del usuario
        usuario.getTareas().add(tarea);
        usuario.getTareas().add(otra);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(modelMapper.map(any(Tarea.class), eq(TareaData.class)))
                .thenAnswer(inv -> {
                    Tarea t = inv.getArgument(0);
                    TareaData d = new TareaData();
                    d.setId(t.getId());
                    d.setTitulo(t.getTitulo());
                    d.setDescripcion(t.getDescripcion());
                    return d;
                });

        List<TareaData> result = tareaService.allTareasUsuario(1L);
        assertEquals(2, result.size());
        // Orden ascendente por ID: 5L, luego 100L
        assertEquals(5L, result.get(0).getId());
        assertEquals(100L, result.get(1).getId());
    }

    @Test
    void testAllTareasUsuarioUsuarioNoExiste() {
        when(usuarioRepository.findById(42L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class,
                () -> tareaService.allTareasUsuario(42L));
        assertTrue(exception.getMessage().contains("Usuario 42 no existe"));
    }

    @Test
    void testFindById() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        when(modelMapper.map(tarea, TareaData.class)).thenReturn(tareaData);

        TareaData found = tareaService.findById(100L);
        assertNotNull(found);
        assertEquals("Tarea de prueba", found.getTitulo());
    }

    @Test
    void testFindByIdNoExiste() {
        when(tareaRepository.findById(999L)).thenReturn(Optional.empty());
        assertNull(tareaService.findById(999L));
    }

    @Test
    void testModificaTarea() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);
        when(modelMapper.map(any(Tarea.class), eq(TareaData.class))).thenReturn(tareaData);

        TareaData mod = tareaService.modificaTarea(100L, "Nuevo título", "Nueva desc");
        assertEquals(100L, mod.getId());
        verify(tareaRepository).save(any(Tarea.class));
    }

    @Test
    void testModificaTareaNoExiste() {
        when(tareaRepository.findById(404L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class,
                () -> tareaService.modificaTarea(404L, "T", "D"));
        // Servicio lanza "No existe tarea"
        assertTrue(exception.getMessage().contains("No existe tarea"));
    }

    @Test
    void testBorraTarea() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        assertDoesNotThrow(() -> tareaService.borraTarea(100L));
        verify(tareaRepository).delete(tarea);
    }

    @Test
    void testBorraTareaNoExiste() {
        when(tareaRepository.findById(500L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class,
                () -> tareaService.borraTarea(500L));
        assertTrue(exception.getMessage().contains("No existe tarea con id 500"));
    }

    @Test
    void testUsuarioContieneTarea() {
        usuario.getTareas().add(tarea);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));

        assertTrue(tareaService.usuarioContieneTarea(1L, 100L));
    }

    @Test
    void testUsuarioContieneTareaFalla() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));

        Exception exception = assertThrows(TareaServiceException.class,
                () -> tareaService.usuarioContieneTarea(1L, 100L));
        assertTrue(exception.getMessage().contains("No existe tarea o usuario id"));
    }
}

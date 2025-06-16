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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TareaServiceTest {

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
        tarea = new Tarea(usuario, "Tarea de prueba");
        tarea.setId(100L);
        tareaData = new TareaData();
        tareaData.setId(100L);
        tareaData.setTitulo("Tarea de prueba");
    }

    @Test
    void testNuevaTareaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tareaRepository.save(any(Tarea.class))).thenReturn(tarea);
        when(modelMapper.map(any(Tarea.class), eq(TareaData.class))).thenReturn(tareaData);

        TareaData result = tareaService.nuevaTareaUsuario(1L, "Tarea de prueba");
        assertEquals(100L, result.getId());
        assertEquals("Tarea de prueba", result.getTitulo());
    }

    @Test
    void testNuevaTareaUsuarioUsuarioNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class, () ->
                tareaService.nuevaTareaUsuario(99L, "Tarea inválida"));

        assertTrue(exception.getMessage().contains("Usuario 99 no existe"));
    }


    @Test
    void testFindById() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        when(modelMapper.map(tarea, TareaData.class)).thenReturn(tareaData);

        TareaData result = tareaService.findById(100L);
        assertNotNull(result);
        assertEquals("Tarea de prueba", result.getTitulo());
    }

    @Test
    void testFindByIdNoExiste() {
        when(tareaRepository.findById(999L)).thenReturn(Optional.empty());
        assertNull(tareaService.findById(999L));
    }

    @Test
    void testModificaTarea() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any())).thenReturn(tarea);
        when(modelMapper.map(any(Tarea.class), eq(TareaData.class))).thenReturn(tareaData);

        TareaData result = tareaService.modificaTarea(100L, "Nuevo título");
        assertEquals(100L, result.getId());
        verify(tareaRepository).save(any());
    }

    @Test
    void testModificaTareaNoExiste() {
        when(tareaRepository.findById(404L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class, () ->
                tareaService.modificaTarea(404L, "Título"));

        assertTrue(exception.getMessage().contains("No existe tarea con id 404"));
    }

    @Test
    void testBorraTarea() {
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));
        tareaService.borraTarea(100L);
        verify(tareaRepository).delete(tarea);
    }

    @Test
    void testBorraTareaNoExiste() {
        when(tareaRepository.findById(404L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(TareaServiceException.class, () ->
                tareaService.borraTarea(404L));

        assertTrue(exception.getMessage().contains("No existe tarea con id 404"));
    }

    @Test
    void testUsuarioContieneTarea() {
        usuario.setTareas((Set<Tarea>) List.of(tarea));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));

        boolean result = tareaService.usuarioContieneTarea(1L, 100L);
        assertTrue(result);
    }

    @Test
    void testUsuarioContieneTareaFalla() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        when(tareaRepository.findById(100L)).thenReturn(Optional.of(tarea));

        Exception exception = assertThrows(TareaServiceException.class, () ->
                tareaService.usuarioContieneTarea(1L, 100L));

        assertTrue(exception.getMessage().contains("No existe tarea o usuario"));
    }
}


package example.ToDoApp.controller;

import example.ToDoApp.controlador.TareaController;
import example.ToDoApp.dto.TareaData;
import example.ToDoApp.dto.UsuarioData;
import example.ToDoApp.servicio.TareaService;
import example.ToDoApp.servicio.UsuarioService;
import example.ToDoApp.authentication.ManagerUserSession;
import example.ToDoApp.controlador.exception.UsuarioNoLogeadoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TareaControllerTest {

    @InjectMocks
    private TareaController tareaController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private TareaService tareaService;

    @Mock
    private ManagerUserSession managerUserSession;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listadoTareas_usuarioLogeadoCorrecto_retornaLista() {
        Long idUsuario = 1L;

        // Simulamos usuario logeado con mismo idUsuario
        when(managerUserSession.usuarioLogeado()).thenReturn(idUsuario);

        // Simulamos usuario encontrado
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(idUsuario);
        usuarioData.setEmail("test@example.com");

        when(usuarioService.findById(idUsuario)).thenReturn(usuarioData);

        // Simulamos lista de tareas
        TareaData tarea1 = new TareaData();
        tarea1.setTitulo("Tarea 1");
        TareaData tarea2 = new TareaData();
        tarea2.setTitulo("Tarea 2");

        List<TareaData> tareas = List.of(tarea1, tarea2);

        when(tareaService.allTareasUsuario(idUsuario)).thenReturn(tareas);

        // Llamamos al método
        String viewName = tareaController.listadoTareas(idUsuario, model, null);

        // Verificamos que se agregaron atributos al modelo
        verify(model).addAttribute("usuario", usuarioData);
        verify(model).addAttribute("tareas", tareas);

        // Comprobamos que la vista es la esperada
        assertEquals("listaTareas", viewName);
    }
}

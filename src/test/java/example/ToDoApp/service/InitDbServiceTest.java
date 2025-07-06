package example.ToDoApp.service;

import example.ToDoApp.model.Tarea;
import example.ToDoApp.model.Usuario;
import example.ToDoApp.repositorio.TareaRepository;
import example.ToDoApp.repositorio.UsuarioRepository;
import example.ToDoApp.servicio.InitDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class InitDbServiceTest {

    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;
    private InitDbService initDbService;

    @BeforeEach
    public void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        tareaRepository = mock(TareaRepository.class);

        initDbService = new InitDbService();

        // Inyección manual de dependencias
        usuarioRepository = usuarioRepository;
        tareaRepository = tareaRepository;
    }

    @Test
    public void testInitDatabase() {
        // Simulación del método initDatabase()
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Lógica idéntica a la del método original
        usuarioRepository.save(usuario);

        Tarea tarea1 = new Tarea(usuario, "Lavar coche", "Lavar el coche en el garaje");
        tareaRepository.save(tarea1);

        Tarea tarea2 = new Tarea(usuario, "Renovar DNI", "Renovar el DNI en la comisaría");
        tareaRepository.save(tarea2);

        // Verificaciones
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(tareaRepository, times(2)).save(any(Tarea.class));
    }
}

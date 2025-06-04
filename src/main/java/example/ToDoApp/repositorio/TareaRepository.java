package example.ToDoApp.repositorio;

import example.ToDoApp.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
}

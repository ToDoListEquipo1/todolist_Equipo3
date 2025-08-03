package example.ToDoApp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;
    private String descripcion;
    @NotNull
    @ManyToOne

    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Tarea() {
    }
    public Tarea(Usuario usuario, String titulo) {
        this.usuario = usuario;
        this.titulo = titulo;
    }

    public Tarea(Usuario usuario, String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        setUsuario(usuario);
    }

    public void setUsuario(Usuario usuario) {
        // Comprueba si el usuario ya está establecido
        if (this.usuario != usuario) {
            this.usuario = usuario;
            // Añade la tarea a la lista de tareas del usuario
            usuario.addTarea(this);
        }
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Tarea))
            return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            return Objects.equals(id, tarea.id);
        return titulo.equals(tarea.titulo)
                && usuario.equals(tarea.usuario)
                && Objects.equals(descripcion, tarea.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario, descripcion);
    }
}

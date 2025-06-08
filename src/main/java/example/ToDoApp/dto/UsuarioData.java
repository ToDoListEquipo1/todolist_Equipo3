package example.ToDoApp.dto;

import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class UsuarioData {
    private Long id;
    private String email;
    private String nombre;
    private String password;
    private Date fechaNacimiento;

    private Boolean esAdministrador;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioData)) return false;
        UsuarioData that = (UsuarioData) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

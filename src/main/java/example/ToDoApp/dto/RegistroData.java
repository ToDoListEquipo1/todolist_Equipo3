package example.ToDoApp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RegistroData {

    @Email
    private String eMail;
    private String password;
    private String nombre;
    private String apellido;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date fechaNacimiento;
    private boolean esAdministrador;
}

package example.ToDoApp.servicio;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class InitDbService {

}

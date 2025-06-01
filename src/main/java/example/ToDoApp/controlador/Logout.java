package example.ToDoApp.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Logout {
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
}

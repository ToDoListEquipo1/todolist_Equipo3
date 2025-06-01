package example.ToDoApp.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("titulo", "About");
        return "about";
    }
}

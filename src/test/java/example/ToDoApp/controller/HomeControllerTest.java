package example.ToDoApp.controller;

import example.ToDoApp.controlador.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())              // HTTP 200 OK
                .andExpect(view().name("about"))         // Que retorne la vista "about"
                .andExpect(model().attributeExists("titulo")) // Que el modelo contenga el atributo "titulo"
                .andExpect(model().attribute("titulo", "About")); // Que el valor de "titulo" sea "About"
    }
}


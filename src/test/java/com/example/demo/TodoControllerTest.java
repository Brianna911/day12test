package com.example.demo;

import com.example.demo.Entity.Todo;
import com.example.demo.Repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
    }

    @Test
    public void testGetAllTodos_EmptyList() throws Exception {
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void testGetAllTodos_OneItem() throws Exception {
        Todo todo = new Todo("Buy milk", false);
        Todo savedTodo = todoRepository.save(todo);

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedTodo.getId()))
                .andExpect(jsonPath("$[0].text").value("Buy milk"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    public void testCreateTodo_Success() throws Exception {
        String json = "{ \"text\": \"Buy milk\" }";

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Buy milk"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    public void testCreateTodo_EmptyText() throws Exception {
        String json = "{ \"text\": \"\", \"completed\": false }";

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testCreateTodo_MissingText() throws Exception {
        String json = "{ \"completed\": false }";

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testCreateTodo_ClientSentIdIgnored() throws Exception {
        String json = "{ \"id\": \"999\", \"text\": \"Buy bread\", \"completed\": false }";

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not(equalTo(999))))
                .andExpect(jsonPath("$.text").value("Buy bread"))
                .andExpect(jsonPath("$.completed").value(false));
    }
    @Test
    public void testUpdateTodo_NotFound() throws Exception {
        String updatedJson = "{ \"text\": \"Non-existent\", \"completed\": true }";

        mockMvc.perform(put("/todos/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteTodo_NotFound() throws Exception {
        mockMvc.perform(delete("/todos/9999"))
                .andExpect(status().isNotFound());
    }

}

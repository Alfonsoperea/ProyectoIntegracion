package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Muy importante: limpia la base de datos después de cada test
class CaptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CaptionRepository captionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /videominer/captions - Debe retornar lista (vacía o con datos)")
    void getAllCaptions_Real() throws Exception {
        mockMvc.perform(get("/videominer/captions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("PUT /videominer/captions/{id} - Debe actualizar una caption real")
    void updateCaption_Real() throws Exception {
        // 1. Pre-insertamos una caption en la BD real
        Caption caption = new Caption();
        caption.setId("cap-test");
        caption.setName("Original name");
        caption.setLanguage("en");
        captionRepository.save(caption);

        // 2. Intentamos actualizarla a través de la API
        caption.setName("Updated name");

        mockMvc.perform(put("/videominer/captions/cap-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(caption)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated name"));
    }

    @Test
    @DisplayName("DELETE /videominer/captions/{id} - Debe borrar si existe")
    void deleteCaption_Real() throws Exception {
        // 1. Pre-insertamos
        Caption caption = new Caption();
        caption.setId("cap-to-delete");
        captionRepository.save(caption);

        // 2. Borramos
        mockMvc.perform(delete("/videominer/captions/cap-to-delete"))
                .andExpect(status().isNoContent());

        // 3. Verificamos que ya no existe (opcional)
        mockMvc.perform(get("/videominer/captions/cap-to-delete"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /videominer/captions/{id} - 404 si no existe")
    void findOne_NotFound_Real() throws Exception {
        mockMvc.perform(get("/videominer/captions/id-fantasma"))
                .andExpect(status().isNotFound());
    }
}
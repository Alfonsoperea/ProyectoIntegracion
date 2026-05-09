package com.aiss.DailyMotionMiner.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /dailymotion/{id} - Real")
    void getChannel_Real() throws Exception {
        // Usamos un ID real de DailyMotion para que el servicio no devuelva error
        String channelId = "euronews";

        mockMvc.perform(get("/dailymotion/" + channelId)
                        .param("maxVideos", "2")
                        .param("maxPages", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /dailymotion/{id} - Real")
    void createChannel_Real() throws Exception {
        String channelId = "euronews";

        // IMPORTANTE: Para que este test de 201 (Created),
        // la aplicación VideoMiner debe estar corriendo en el puerto 8080.
        mockMvc.perform(post("/dailymotion/" + channelId)
                        .param("maxVideos", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
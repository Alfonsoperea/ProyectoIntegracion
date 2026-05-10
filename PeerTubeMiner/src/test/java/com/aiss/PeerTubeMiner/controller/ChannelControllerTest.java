package com.aiss.PeerTubeMiner.controller;

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
    @DisplayName("GET /peertube/{canal} - Test Real")
    void getChannel_Real() throws Exception {
        
        String canal = "framasoft";

        mockMvc.perform(get("/peertube/" + canal)
                        .param("maxVideos", "2")
                        .param("maxComments", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /peertube/{canal} - Integración Real con VideoMiner")
    void createChannel_Real() throws Exception {
        String canal = "framasoft";

        
        
        mockMvc.perform(post("/peertube/" + canal)
                        .param("maxVideos", "1")
                        .param("maxComments", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
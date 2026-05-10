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

        
        
        mockMvc.perform(post("/dailymotion/" + channelId)
                        .param("maxVideos", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
}
package aiss.videominer.controller;

import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional 
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /videominer/channels - Debe crear un canal en la BD real")
    void createChannel_Real() throws Exception {
        Channel channel = new Channel();
        channel.setId("channel-1");
        channel.setName("AISS");
        channel.setCreatedTime("2026-01-01");
        channel.setVideos(List.of());

        mockMvc.perform(post("/videominer/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("channel-1"));
    }

    @Test
    @DisplayName("GET /videominer/channels - Debe retornar lista con canales reales")
    void findAll_Real() throws Exception {
        
        Channel channel = new Channel();
        channel.setId("channel-test");
        channel.setName("Test Channel");
        channel.setCreatedTime("2026-05-09");
        channelRepository.save(channel);

        mockMvc.perform(get("/videominer/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'channel-test')]").exists());
    }

    @Test
    @DisplayName("GET /videominer/channels/{id} - Debe encontrar un canal persistido")
    void findOne_Real() throws Exception {
        Channel channel = new Channel();
        channel.setId("find-me");
        channel.setName("Find Me");
        channel.setCreatedTime("2026-05-09");
        channelRepository.save(channel);

        mockMvc.perform(get("/videominer/channels/find-me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("find-me"));
    }

    @Test
    @DisplayName("PUT /videominer/channels/{id} - Debe actualizar datos reales")
    void updateChannel_Real() throws Exception {
        Channel channel = new Channel();
        channel.setId("update-me");
        channel.setName("Old Name");
        channel.setCreatedTime("2026-05-09");
        channelRepository.save(channel);

        channel.setName("New Name");

        mockMvc.perform(put("/videominer/channels/update-me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    @DisplayName("DELETE /videominer/channels/{id} - Debe eliminar de la BD real")
    void deleteChannel_Real() throws Exception {
        Channel channel = new Channel();
        channel.setId("delete-me");
        channel.setName("Delete Me");
        channel.setCreatedTime("2026-05-09");
        channelRepository.save(channel);

        mockMvc.perform(delete("/videominer/channels/delete-me"))
                .andExpect(status().isNoContent());

        
        assert(channelRepository.findById("delete-me").isEmpty());
    }
}
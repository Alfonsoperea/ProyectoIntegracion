package aiss.videominer.controller;

import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelRepository channelRepository;

    @Test
    void createChannel_returnsCreated() throws Exception {
        Channel saved = new Channel();
        saved.setId("channel-1");
        saved.setName("AISS");
        saved.setCreatedTime("2026-01-01");
        saved.setVideos(List.of());

        when(channelRepository.save(any(Channel.class))).thenReturn(saved);

        String body = """
                {
                  "id": "channel-1",
                  "name": "AISS",
                  "description": "desc",
                  "createdTime": "2026-01-01",
                  "videos": []
                }
                """;

        mockMvc.perform(post("/videominer/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("channel-1"));
    }

    @Test
    void findAll_returnsOk() throws Exception {
        Channel channel = new Channel();
        channel.setId("channel-1");
        channel.setName("AISS");
        channel.setCreatedTime("2026-01-01");
        channel.setVideos(List.of());

        when(channelRepository.findAll()).thenReturn(List.of(channel));

        mockMvc.perform(get("/videominer/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("channel-1"));
    }

    @Test
    void findOne_returnsOk() throws Exception {
        Channel channel = new Channel();
        channel.setId("channel-1");
        channel.setName("AISS");
        channel.setCreatedTime("2026-01-01");
        channel.setVideos(List.of());

        when(channelRepository.findById("channel-1")).thenReturn(Optional.of(channel));

        mockMvc.perform(get("/videominer/channels/channel-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("channel-1"));
    }

    @Test
    void updateChannel_returnsOk() throws Exception {
        Channel updated = new Channel();
        updated.setId("channel-1");
        updated.setName("Updated");
        updated.setCreatedTime("2026-01-01");
        updated.setVideos(List.of());

        when(channelRepository.existsById("channel-1")).thenReturn(true);
        when(channelRepository.save(any(Channel.class))).thenReturn(updated);

        String body = """
                {
                  "name": "Updated",
                  "description": "desc",
                  "createdTime": "2026-01-01",
                  "videos": []
                }
                """;

        mockMvc.perform(put("/videominer/channels/channel-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteChannel_returnsNoContent() throws Exception {
        when(channelRepository.existsById("channel-1")).thenReturn(true);

        mockMvc.perform(delete("/videominer/channels/channel-1"))
                .andExpect(status().isNoContent());
    }
}

package com.aiss.PeerTubeMiner.controller;

import com.aiss.PeerTubeMiner.etl.transformer2;
import com.aiss.PeerTubeMiner.model.peertube.User;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import com.aiss.PeerTubeMiner.service.CaptionService;
import com.aiss.PeerTubeMiner.service.ChannelService;
import com.aiss.PeerTubeMiner.service.CommentService;
import com.aiss.PeerTubeMiner.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ChannelController.class, properties = "videominer.uri=http://localhost:8080/videominer/channels")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;
    @MockitoBean
    private VideoService videoService;
    @MockitoBean
    private CommentService commentService;
    @MockitoBean
    private CaptionService captionService;
    @MockitoBean
    private transformer2 transformer;
    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void getChannelTest_returnsOk() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setName("canal");
        user.setDescription("desc");
        user.setCreatedAt("2026-01-01");

        VideoSearch videoSearch = new VideoSearch();
        videoSearch.setData(List.of());

        VMChannel vmChannel = new VMChannel();
        vmChannel.setId("u1");
        vmChannel.setName("canal");
        vmChannel.setVideos(List.of());

        when(channelService.getUser("canal")).thenReturn(user);
        when(videoService.getVideos("canal", 10)).thenReturn(videoSearch);
        when(transformer.transformChannel(any(), eq(null))).thenReturn(vmChannel);

        mockMvc.perform(get("/peertube/canal")
                        .param("maxVideos", "10")
                        .param("maxComments", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("u1"));
    }

    @Test
    void createChannel_returnsCreated() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setName("canal");
        user.setDescription("desc");
        user.setCreatedAt("2026-01-01");

        VideoSearch videoSearch = new VideoSearch();
        videoSearch.setData(List.of());

        VMChannel vmChannel = new VMChannel();
        vmChannel.setId("u1");
        vmChannel.setName("canal");
        vmChannel.setVideos(List.of());

        when(channelService.getUser("canal")).thenReturn(user);
        when(videoService.getVideos("canal", 10)).thenReturn(videoSearch);
        when(transformer.transformChannel(any(), eq(null))).thenReturn(vmChannel);
        when(restTemplate.postForObject(
                eq("http://localhost:8080/videominer/channels"),
                any(VMChannel.class),
                eq(VMChannel.class))
        ).thenReturn(vmChannel);

        mockMvc.perform(post("/peertube/canal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxVideos", "10")
                        .param("maxComments", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("u1"));
    }
}

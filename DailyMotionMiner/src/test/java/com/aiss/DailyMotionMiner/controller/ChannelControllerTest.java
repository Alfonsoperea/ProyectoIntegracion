package com.aiss.DailyMotionMiner.controller;

import com.aiss.DailyMotionMiner.etl.DailyMotionTransformer;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.service.ChannelService;
import com.aiss.DailyMotionMiner.service.SubtitleService;
import com.aiss.DailyMotionMiner.service.VideoService;
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

@WebMvcTest(
        value = ChannelController.class,
        properties = {
                "videominer.uri=http://localhost:8080/videominer/channels",
                "dailymotionminer.maxVideos=10",
                "dailymotionminer.maxPages=2"
        })
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;
    @MockitoBean
    private VideoService videoService;
    @MockitoBean
    private SubtitleService subtitleService;
    @MockitoBean
    private DailyMotionTransformer transformer;
    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void getChannel_returnsOk() throws Exception {
        DMOwner owner = new DMOwner();
        owner.setId("owner-1");
        owner.setScreenname("euronews");
        owner.setDescription("desc");
        owner.setCreatedTime(1710000000L);

        DMVideoSearch videoSearch = new DMVideoSearch();
        videoSearch.setList(List.of());

        VMChannel vmChannel = new VMChannel();
        vmChannel.setId("owner-1");
        vmChannel.setName("euronews");
        vmChannel.setVideos(List.of());

        when(channelService.getChannel("euronews")).thenReturn(owner);
        when(videoService.getVideos("euronews", 5, 1)).thenReturn(videoSearch);
        when(transformer.transformChannel(eq(owner), any())).thenReturn(vmChannel);

        mockMvc.perform(get("/dailymotion/euronews")
                        .param("maxVideos", "5")
                        .param("maxPages", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("owner-1"));
    }

    @Test
    void createChannel_returnsCreated() throws Exception {
        DMOwner owner = new DMOwner();
        owner.setId("owner-1");
        owner.setScreenname("euronews");
        owner.setDescription("desc");
        owner.setCreatedTime(1710000000L);

        DMVideoSearch videoSearch = new DMVideoSearch();
        videoSearch.setList(List.of());

        VMChannel vmChannel = new VMChannel();
        vmChannel.setId("owner-1");
        vmChannel.setName("euronews");
        vmChannel.setVideos(List.of());

        when(channelService.getChannel("euronews")).thenReturn(owner);
        when(videoService.getVideos("euronews", 5, 1)).thenReturn(videoSearch);
        when(transformer.transformChannel(eq(owner), any())).thenReturn(vmChannel);
        when(restTemplate.postForObject(
                eq("http://localhost:8080/videominer/channels"),
                any(VMChannel.class),
                eq(VMChannel.class))
        ).thenReturn(vmChannel);

        mockMvc.perform(post("/dailymotion/euronews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("maxVideos", "5")
                        .param("maxPages", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("owner-1"));
    }
}

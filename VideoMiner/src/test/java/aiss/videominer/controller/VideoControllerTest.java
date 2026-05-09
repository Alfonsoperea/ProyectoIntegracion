package aiss.videominer.controller;

import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoRepository videoRepository;

    @Test
    void findAll_returnsOk() throws Exception {
        Video video = new Video();
        video.setId("video-1");
        video.setName("Intro");
        video.setReleaseTime("2026-01-01");

        when(videoRepository.findAll()).thenReturn(List.of(video));

        mockMvc.perform(get("/videominer/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("video-1"));
    }

    @Test
    void findOne_returnsOk() throws Exception {
        Video video = new Video();
        video.setId("video-1");
        video.setName("Intro");
        video.setReleaseTime("2026-01-01");

        when(videoRepository.findById("video-1")).thenReturn(Optional.of(video));

        mockMvc.perform(get("/videominer/videos/video-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("video-1"));
    }

    @Test
    void updateVideo_returnsOk() throws Exception {
        Video updated = new Video();
        updated.setId("video-1");
        updated.setName("Updated");
        updated.setReleaseTime("2026-01-01");

        when(videoRepository.existsById("video-1")).thenReturn(true);
        when(videoRepository.save(any(Video.class))).thenReturn(updated);

        String body = """
                {
                  "name": "Updated",
                  "description": "desc",
                  "releaseTime": "2026-01-01"
                }
                """;

        mockMvc.perform(put("/videominer/videos/video-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteVideo_returnsNoContent() throws Exception {
        when(videoRepository.existsById("video-1")).thenReturn(true);

        mockMvc.perform(delete("/videominer/videos/video-1"))
                .andExpect(status().isNoContent());
    }
}

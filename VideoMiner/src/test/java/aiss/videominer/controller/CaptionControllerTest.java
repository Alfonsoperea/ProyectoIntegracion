package aiss.videominer.controller;

import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
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

@WebMvcTest(CaptionController.class)
class CaptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CaptionRepository captionRepository;

    @MockitoBean
    private VideoRepository videoRepository;

    @Test
    void getAllCaptions_returnsOk() throws Exception {
        Caption caption = new Caption();
        caption.setId("caption-1");
        caption.setName("sub");
        caption.setLanguage("es");

        when(captionRepository.findAll()).thenReturn(List.of(caption));

        mockMvc.perform(get("/videominer/captions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("caption-1"));
    }

    @Test
    void findOne_returnsOk() throws Exception {
        Caption caption = new Caption();
        caption.setId("caption-1");
        caption.setName("sub");
        caption.setLanguage("es");

        when(captionRepository.findById("caption-1")).thenReturn(Optional.of(caption));

        mockMvc.perform(get("/videominer/captions/caption-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("caption-1"));
    }

    @Test
    void updateCaption_returnsOk() throws Exception {
        Caption caption = new Caption();
        caption.setId("caption-1");
        caption.setName("new-sub");
        caption.setLanguage("es");

        when(captionRepository.existsById("caption-1")).thenReturn(true);
        when(captionRepository.save(any(Caption.class))).thenReturn(caption);

        String body = """
                {
                  "name": "new-sub",
                  "language": "es"
                }
                """;

        mockMvc.perform(put("/videominer/captions/caption-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new-sub"));
    }

    @Test
    void deleteCaption_returnsNoContent() throws Exception {
        when(captionRepository.existsById("caption-1")).thenReturn(true);

        mockMvc.perform(delete("/videominer/captions/caption-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCaptionsByVideoId_returnsOk() throws Exception {
        Caption caption = new Caption();
        caption.setId("caption-1");
        caption.setName("sub");
        caption.setLanguage("es");

        Video video = new Video();
        video.setId("video-1");
        video.setCaptions(List.of(caption));

        when(videoRepository.findById("video-1")).thenReturn(Optional.of(video));

        mockMvc.perform(get("/videominer/videos/video-1/captions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("caption-1"));
    }
}

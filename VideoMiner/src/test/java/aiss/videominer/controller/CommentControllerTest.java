package aiss.videominer.controller;

import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
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

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private VideoRepository videoRepository;

    @Test
    void getAllComments_returnsOk() throws Exception {
        Comment comment = new Comment();
        comment.setId("comment-1");
        comment.setText("hola");

        when(commentRepository.findAll()).thenReturn(List.of(comment));

        mockMvc.perform(get("/videominer/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("comment-1"));
    }

    @Test
    void findOne_returnsOk() throws Exception {
        Comment comment = new Comment();
        comment.setId("comment-1");
        comment.setText("hola");

        when(commentRepository.findById("comment-1")).thenReturn(Optional.of(comment));

        mockMvc.perform(get("/videominer/comments/comment-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("comment-1"));
    }

    @Test
    void updateComment_returnsOk() throws Exception {
        Comment comment = new Comment();
        comment.setId("comment-1");
        comment.setText("nuevo");

        when(commentRepository.existsById("comment-1")).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        String body = """
                {
                  "text": "nuevo",
                  "createdOn": "2026-01-01"
                }
                """;

        mockMvc.perform(put("/videominer/comments/comment-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("nuevo"));
    }

    @Test
    void deleteComment_returnsNoContent() throws Exception {
        when(commentRepository.existsById("comment-1")).thenReturn(true);

        mockMvc.perform(delete("/videominer/comments/comment-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCommentsByVideoId_returnsOk() throws Exception {
        Comment comment = new Comment();
        comment.setId("comment-1");
        comment.setText("hola");

        Video video = new Video();
        video.setId("video-1");
        video.setComments(List.of(comment));

        when(videoRepository.findById("video-1")).thenReturn(Optional.of(video));

        mockMvc.perform(get("/videominer/videos/video-1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("comment-1"));
    }
}

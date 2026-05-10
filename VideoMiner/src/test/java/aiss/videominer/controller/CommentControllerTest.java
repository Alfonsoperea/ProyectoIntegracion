package aiss.videominer.controller;

import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
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
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /videominer/comments - Debe retornar lista de comentarios reales")
    void getAllComments_Real() throws Exception {
        
        Comment comment = new Comment();
        comment.setId("c-1");
        comment.setText("Comentario de prueba");
        commentRepository.save(comment);

        mockMvc.perform(get("/videominer/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'c-1')]").exists());
    }

    @Test
    @DisplayName("GET /videominer/comments/{id} - Debe encontrar un comentario persistido")
    void findOne_Real() throws Exception {
        Comment comment = new Comment();
        comment.setId("c-find");
        comment.setText("Búscame");
        commentRepository.save(comment);

        mockMvc.perform(get("/videominer/comments/c-find"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Búscame"));
    }

    @Test
    @DisplayName("PUT /videominer/comments/{id} - Debe actualizar un comentario en la BD")
    void updateComment_Real() throws Exception {
        Comment comment = new Comment();
        comment.setId("c-update");
        comment.setText("Texto antiguo");
        commentRepository.save(comment);

        comment.setText("Texto nuevo");

        mockMvc.perform(put("/videominer/comments/c-update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Texto nuevo"));
    }

    @Test
    @DisplayName("DELETE /videominer/comments/{id} - Debe borrar el comentario")
    void deleteComment_Real() throws Exception {
        Comment comment = new Comment();
        comment.setId("c-delete");
        commentRepository.save(comment);

        mockMvc.perform(delete("/videominer/comments/c-delete"))
                .andExpect(status().isNoContent());

        
        assert(commentRepository.findById("c-delete").isEmpty());
    }

    @Test
    @DisplayName("GET /videominer/videos/{id}/comments - Debe traer los comentarios de un video")
    void getCommentsByVideoId_Real() throws Exception {
        
        Comment comment = new Comment();
        comment.setId("c-video");
        comment.setText("Hola video");
        commentRepository.save(comment);

        
        Video video = new Video();
        video.setId("v-1");
        video.setName("Video con comentarios");
        video.setComments(List.of(comment));
        videoRepository.save(video);

        mockMvc.perform(get("/videominer/videos/v-1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("c-video"));
    }
}
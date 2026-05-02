package aiss.videominer.controller;

import aiss.videominer.exceptions.CommentNotFoundException;
import aiss.videominer.exceptions.VideoNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;

    @GetMapping("/comments")
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/comments/{id}")
    public Comment findOne(@PathVariable String id) throws CommentNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (!comment.isPresent()) {
            throw new CommentNotFoundException();
        }
        return comment.get();
    }

    @GetMapping("/videos/{videoId}/comments")
    public List<Comment> getCommentsByVideoId(@PathVariable String videoId) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(videoId);
        if (!video.isPresent()) {
            throw new VideoNotFoundException();
        }
        return video.get().getComments();
    }
}

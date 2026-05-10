package aiss.videominer.controller;

import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    
    @GetMapping
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(id);

        
        if (!video.isPresent()) {
            throw new VideoNotFoundException();
        }

        return video.get();
    }

    @PutMapping("/{id}")
    public Video updateVideo(@PathVariable String id,
                             @RequestBody @Valid Video updatedVideo)
            throws VideoNotFoundException {
        if (!videoRepository.existsById(id)) {
            throw new VideoNotFoundException();
        }

        updatedVideo.setId(id);
        return videoRepository.save(updatedVideo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(@PathVariable String id) throws VideoNotFoundException {
        if (!videoRepository.existsById(id)) {
            throw new VideoNotFoundException();
        }

        videoRepository.deleteById(id);
    }
}
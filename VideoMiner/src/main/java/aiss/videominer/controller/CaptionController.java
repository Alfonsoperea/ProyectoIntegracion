package aiss.videominer.controller;

import aiss.videominer.model.Video;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class CaptionController {
    @Autowired
    CaptionRepository captionRepository;
    @Autowired
    VideoRepository videoRepository;

    @GetMapping("/captions")
    public List<Caption> getAllCaptions() {
        return captionRepository.findAll();
    }

    @GetMapping("/captions/{id}")
    public Caption findOne(@PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(id);
        
        if (!caption.isPresent()) {
            throw new CaptionNotFoundException();
        }
        
        return caption.get();
    }

    @PutMapping("/captions/{id}")
    public Caption updateCaption(@PathVariable String id,
                                 @RequestBody Caption updatedCaption)
            throws CaptionNotFoundException {
        if (!captionRepository.existsById(id)) {
            throw new CaptionNotFoundException();
        }

        updatedCaption.setId(id);
        return captionRepository.save(updatedCaption);
    }

    @DeleteMapping("/captions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCaption(@PathVariable String id) throws CaptionNotFoundException {
        if (!captionRepository.existsById(id)) {
            throw new CaptionNotFoundException();
        }

        captionRepository.deleteById(id);
    }

    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> getCaptionsByVideoId(@PathVariable String videoId) throws VideoNotFoundException { 

        
        Optional<Video> video = videoRepository.findById(videoId);

        
        if (!video.isPresent()) {
            throw new VideoNotFoundException(); 
        }

        
        
        return video.get().getCaptions();
    }
}
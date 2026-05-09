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
        // Si la caption no está presente en la base de datos, lanzamos el error 404
        if (!caption.isPresent()) {
            throw new CaptionNotFoundException();
        }
        // Si llegamos aquí, es que sí existe, así que la devolvemos
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
    public List<Caption> getCaptionsByVideoId(@PathVariable String videoId) throws VideoNotFoundException { // Cambiar Exception por VideoNotFoundException

        // 1. Buscamos el vídeo usando el VideoRepository
        Optional<Video> video = videoRepository.findById(videoId);

        // 2. Si no existe, devolvemos 404
        if (!video.isPresent()) {
            throw new VideoNotFoundException(); // Reemplazar por tu excepción
        }

        // 3. Si existe, devolvemos su lista de captions.
        // (Esto funciona gracias a la relación @OneToMany que configuraste en la clase Video)
        return video.get().getCaptions();
    }
}
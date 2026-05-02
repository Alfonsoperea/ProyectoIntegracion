package aiss.videominer.controller;

import aiss.videominer.exceptions.VideoNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    // 1. GET: Listar todos los vídeos (Requisito del test "Get all videos")
    @GetMapping
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    // 2. GET: Obtener un vídeo por ID (Requisito del test "Get video")
    @GetMapping("/{id}")
    public Video findOne(@PathVariable String id) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(id);

        // Si el vídeo no existe, devolvemos 404 siguiendo las buenas prácticas REST del PDF
        if (!video.isPresent()) {
            throw new VideoNotFoundException();
        }

        return video.get();
    }
}
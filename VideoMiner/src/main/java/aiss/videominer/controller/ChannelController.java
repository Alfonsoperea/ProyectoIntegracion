package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import aiss.videominer.repository.ChannelRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/videominer/channels") // Esta ruta coincide exactamente con tu Postman
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    // 1. POST: Crear un canal (El test "Create Channel")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Devuelve un 201 como pide el test
    public Channel createChannel(@RequestBody @Valid Channel channel) {
        // Al guardar el canal, Hibernate guardará automáticamente sus vídeos, 
        // captions y comentarios si tienes puesto CascadeType.ALL en los modelos.
        normalizeChannelGraph(channel);
        return channelRepository.save(channel);
    }

    @PutMapping("/{id}")
    public Channel updateChannel(@PathVariable String id,
                                 @RequestBody @Valid Channel updatedChannel)
            throws ChannelNotFoundException {
        if (!channelRepository.existsById(id)) {
            throw new ChannelNotFoundException();
        }

        updatedChannel.setId(id);
        normalizeChannelGraph(updatedChannel);
        return channelRepository.save(updatedChannel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable String id) throws ChannelNotFoundException {
        if (!channelRepository.existsById(id)) {
            throw new ChannelNotFoundException();
        }

        channelRepository.deleteById(id);
    }

    private void normalizeChannelGraph(Channel channel) {
        if (channel == null || channel.getVideos() == null) {
            return;
        }

        Map<String, User> usersById = new HashMap<>();
        for (Video video : channel.getVideos()) {
            if (video == null) {
                continue;
            }

            User author = video.getAuthor();
            if (author != null && author.getId() != null && !author.getId().isBlank()) {
                User sharedAuthor = usersById.computeIfAbsent(author.getId(), id -> author);
                video.setAuthor(sharedAuthor);
            }

            if (video.getComments() != null) {
                for (Comment comment : video.getComments()) {
                    if (comment != null && (comment.getId() == null || comment.getId().isBlank())) {
                        comment.setId(UUID.randomUUID().toString());
                    }
                }
            }

            if (video.getCaptions() != null) {
                for (Caption caption : video.getCaptions()) {
                    if (caption != null && (caption.getId() == null || caption.getId().isBlank())) {
                        caption.setId(UUID.randomUUID().toString());
                    }
                }
            }
        }
    }

    // 2. GET: Listar todos los canales (El test "Get all channels")
    @GetMapping
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    // 3. GET: Obtener un canal por ID (El test "Get channel")
    @GetMapping("/{id}")
    public Channel findOne(@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        
        // El PDF especifica que si un recurso no existe, se debe devolver un 404
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException();
        }
        
        return channel.get();
    }
}
package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
        return channelRepository.save(channel);
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
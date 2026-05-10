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
@RequestMapping("/videominer/channels") 
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) 
    public Channel createChannel(@RequestBody @Valid Channel channel) {
        
        
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

    
    @GetMapping
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public Channel findOne(@PathVariable String id) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(id);
        
        
        if (!channel.isPresent()) {
            throw new ChannelNotFoundException();
        }
        
        return channel.get();
    }
}
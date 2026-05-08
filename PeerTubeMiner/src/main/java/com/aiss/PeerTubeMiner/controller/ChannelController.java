package com.aiss.PeerTubeMiner.controller;

import com.aiss.PeerTubeMiner.exception.CaptionNotFoundException;
import com.aiss.PeerTubeMiner.exception.ChannelNotFoundException;
import com.aiss.PeerTubeMiner.exception.CommentNotFoundException;
import com.aiss.PeerTubeMiner.exception.VideoNotFoundException;
import com.aiss.PeerTubeMiner.etl.transformer2;
import com.aiss.PeerTubeMiner.model.peertube.CaptionSearch;
import com.aiss.PeerTubeMiner.model.peertube.Channel;
import com.aiss.PeerTubeMiner.model.peertube.CommentSearch;
import com.aiss.PeerTubeMiner.model.peertube.User;
import com.aiss.PeerTubeMiner.model.peertube.Video;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import com.aiss.PeerTubeMiner.model.videominer.VMVideo;
import com.aiss.PeerTubeMiner.service.CaptionService;
import com.aiss.PeerTubeMiner.service.ChannelService;
import com.aiss.PeerTubeMiner.service.CommentService;
import com.aiss.PeerTubeMiner.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/peertube") // O el path base que prefieras
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private VideoService videoService;

    @Autowired
    private CommentService commentService; // Servicio para traer los Comment threads [cite: 73]
    @Autowired
    private CaptionService captionService; // Servicio para traer las Captions [cite: 71]

    @Autowired
    private transformer2 transformer;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${videominer.uri}")
    private String videoMinerUri;

    // 1. Operación POST: Busca, transforma y ENVÍA a VideoMiner
    @PostMapping("/{accountName}")
    @ResponseStatus(HttpStatus.CREATED)
    public VMChannel createChannel(
            @PathVariable String accountName,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) throws ChannelNotFoundException, VideoNotFoundException, CommentNotFoundException, CaptionNotFoundException {

        // A. Obtener datos de PeerTube (Orquestación de servicios)
        VMChannel commonChannel = fetchAndTransform(accountName, maxVideos, maxComments);

        // B. Realizar el POST a VideoMiner con el objeto transformado
        // El enunciado dice que se envía el canal y este ya contiene todo
        return restTemplate.postForObject(videoMinerUri, commonChannel, VMChannel.class);
    }

    // 2. Operación GET: De solo lectura para pruebas (recomendado en el PDF) 
    @GetMapping("/{accountName}")
    public VMChannel getChannelTest(
            @PathVariable String accountName,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) throws ChannelNotFoundException, VideoNotFoundException, CommentNotFoundException, CaptionNotFoundException {

        // Simplemente devuelve los datos transformados sin enviarlos a VideoMiner
        return fetchAndTransform(accountName, maxVideos, maxComments);
    }

    /**
     * Método auxiliar para centralizar la obtención y transformación de datos.
     */
    private VMChannel fetchAndTransform(String accountName, Integer maxVideos, Integer maxComments) throws ChannelNotFoundException, VideoNotFoundException, CommentNotFoundException, CaptionNotFoundException {
        // 1. Obtener metadatos del Canal/Usuario
        User ptAccount = channelService.getUser(accountName);
        Channel ptChannel = new Channel();
        ptChannel.setId(ptAccount.getId() != null ? String.valueOf(ptAccount.getId()) : accountName);
        ptChannel.setDisplayName(ptAccount.getName());
    
        // 2. Obtener los Vídeos del canal
        VideoSearch videoSearch = videoService.getVideos(accountName, maxVideos);
        List<Video> ptVideos = videoSearch.getData();
    
        // 3. Crear VMChannel y transformar cada vídeo con comments/captions del servicio
        VMChannel vmChannel = transformer.transformChannel(ptChannel, null);
        List<VMVideo> vmVideos = new ArrayList<>();
        if (ptVideos != null) {
            for (Video video : ptVideos) {
                CommentSearch commentSearch = commentService.getComments(video.getId(), maxComments);
                CaptionSearch captionSearch = captionService.getCaptions(video.getId());
                vmVideos.add(transformer.transformVideo(video, commentSearch.getData(), captionSearch.getData()));
            }
        }
        vmChannel.setVideos(vmVideos);
        return vmChannel;
    }
}

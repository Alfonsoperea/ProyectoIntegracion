package com.aiss.PeerTubeMiner.controller;

import com.aiss.PeerTubeMiner.etl.transformer2;
import com.aiss.PeerTubeMiner.model.peertube.Account;
import com.aiss.PeerTubeMiner.model.peertube.Channel;
import com.aiss.PeerTubeMiner.model.peertube.Video;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import com.aiss.PeerTubeMiner.service.ChannelService;
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

import java.util.List;

@RestController
@RequestMapping("/peertube") // O el path base que prefieras
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private transformer2 transformer;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${videominer.uri}")
    private String videoMinerUri;

    // 1. Operación POST: Busca, transforma y ENVÍA a VideoMiner
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public VMChannel createChannel(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) {

        // A. Obtener datos de PeerTube (Orquestación de servicios)
        VMChannel commonChannel = fetchAndTransform(id, maxVideos, maxComments);

        // B. Realizar el POST a VideoMiner con el objeto transformado
        // El enunciado dice que se envía el canal y este ya contiene todo
        return restTemplate.postForObject(videoMinerUri, commonChannel, VMChannel.class);
    }

    // 2. Operación GET: De solo lectura para pruebas (recomendado en el PDF) 
    @GetMapping("/{id}")
    public VMChannel getChannelTest(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @RequestParam(defaultValue = "2") Integer maxComments) {

        // Simplemente devuelve los datos transformados sin enviarlos a VideoMiner
        return fetchAndTransform(id, maxVideos, maxComments);
    }

    /**
     * Método auxiliar para centralizar la obtención y transformación de datos.
     */
    private VMChannel fetchAndTransform(String id, Integer maxVideos, Integer maxComments) {
        // 1. Obtener metadatos del Canal
        Account ptAccount = channelService.getAccount(id);
        Channel ptChannel = new Channel();
        ptChannel.setId(ptAccount.getId() != null ? String.valueOf(ptAccount.getId()) : id);
        ptChannel.setDisplayName(ptAccount.getDisplayName());
        ptChannel.setDescription(ptAccount.getDescription());
        ptChannel.setCreatedAt(ptAccount.getCreatedAt());

        // 2. Obtener los Vídeos del canal [cite: 19-21]
        VideoSearch videoSearch = videoService.getVideos(id, maxVideos);
        List<Video> ptVideos = videoSearch.getData();

        // 3. Transformar al modelo común de VideoMiner
        return transformer.transformChannel(ptChannel, ptVideos);
    }
}

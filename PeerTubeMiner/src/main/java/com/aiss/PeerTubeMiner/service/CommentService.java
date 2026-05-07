package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.model.peertube.CommentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String apiUrl;

    public CommentSearch getComments(String videoUuid, int maxComments) {
<<<<<<< HEAD:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/service/CommentService.java
        String url = apiUrl + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
=======
        // En PeerTube los comentarios se sacan por el UUID del video
        String url = API_URL + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
        // Coincide con tu clase CommentSearch.java
>>>>>>> 0505e9eb2d6f1e8834d6e01f4e580bae82b4e586:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/model/service/CommentService.java
        return restTemplate.getForObject(url, CommentSearch.class);
    }
}
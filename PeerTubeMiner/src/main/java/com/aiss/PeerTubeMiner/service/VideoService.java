package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VideoService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String apiUrl;

    public VideoSearch getVideos(String accountName, int maxVideos) {
<<<<<<< HEAD:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/service/VideoService.java
        String url = apiUrl + "/accounts/" + accountName + "/videos?count=" + maxVideos;
=======
        String url = API_URL + "/accounts/" + accountName + "/videos?count=" + maxVideos;
        // Coincide con tu clase VideoSearch.java
>>>>>>> 0505e9eb2d6f1e8834d6e01f4e580bae82b4e586:PeerTubeMiner/src/main/java/com/aiss/PeerTubeMiner/model/service/VideoService.java
        return restTemplate.getForObject(url, VideoSearch.class);
    }
}
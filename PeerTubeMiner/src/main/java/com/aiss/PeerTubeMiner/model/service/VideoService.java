package com.aiss.PeerTubeMiner.model.service;

import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VideoService {

    @Autowired
    private RestTemplate restTemplate;

    private final String API_URL = "https://peertube2.cpy.re/api/v1";

    public VideoSearch getVideos(String accountName, int maxVideos) {
        String url = API_URL + "/accounts/" + accountName + "/videos?count=" + maxVideos;
        return restTemplate.getForObject(url, VideoSearch.class);
    }
}
package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.exception.VideoNotFoundException;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class VideoService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String apiUrl;

    public VideoSearch getVideos(String accountName, int maxVideos) throws VideoNotFoundException{
        String url = apiUrl + "/accounts/" + accountName + "/videos?count=" + maxVideos;
        try {
            return restTemplate.getForObject(url, VideoSearch.class);
        } catch (HttpClientErrorException e){
            throw new VideoNotFoundException();
        }
    }
}
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
        String url = apiUrl + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
        return restTemplate.getForObject(url, CommentSearch.class);
    }
}
package com.aiss.PeerTubeMiner.model.service;

import com.aiss.PeerTubeMiner.model.peertube.CommentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommentService {

    @Autowired
    private RestTemplate restTemplate;

    private final String API_URL = "https://peertube2.cpy.re/api/v1";

    public CommentSearch getComments(String videoUuid, int maxComments) {
        String url = API_URL + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
        return restTemplate.getForObject(url, CommentSearch.class);
    }
}
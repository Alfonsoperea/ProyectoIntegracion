package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.model.peertube.CaptionSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptionService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${peertubeminer.baseuri}")
    private String apiUrl;

    public CaptionSearch getCaptions(String videoUuid) {
        String url = apiUrl + "/videos/" + videoUuid + "/captions";
        return restTemplate.getForObject(url, CaptionSearch.class);
    }
}

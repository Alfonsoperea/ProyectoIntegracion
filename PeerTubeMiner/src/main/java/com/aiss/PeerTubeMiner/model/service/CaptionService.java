package com.aiss.PeerTubeMiner.model.service;

import com.aiss.PeerTubeMiner.model.peertube.CaptionSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptionService {

    @Autowired
    private RestTemplate restTemplate;

    private final String API_URL = "https://peertube2.cpy.re/api/v1";

    public CaptionSearch getCaptions(String videoUuid) {
        String url = API_URL + "/videos/" + videoUuid + "/captions";
        return restTemplate.getForObject(url, CaptionSearch.class);
    }
}

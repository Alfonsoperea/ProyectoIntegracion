package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.CaptionNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtleSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class SubtitleService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    
    public DMSubtleSearch getSubtitles(String videoId) throws CaptionNotFoundException {
        String url = baseUri + "/video/" + videoId + "/subtitles"
                + "?fields=id,language,language_label,url";
        try {
            DMSubtleSearch result = restTemplate.getForObject(url, DMSubtleSearch.class);
            if (result == null) {
                throw new CaptionNotFoundException();
            }
            return result;
        } catch (HttpClientErrorException e) {
            throw new CaptionNotFoundException();
        }
    }
}

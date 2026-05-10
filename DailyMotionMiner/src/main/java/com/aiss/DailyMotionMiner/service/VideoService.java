package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.VideoNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class VideoService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    @Value("${dailymotionminer.maxVideos}")
    private int defaultMaxVideos;

    @Value("${dailymotionminer.maxPages}")
    private int defaultMaxPages;

    
    public DMVideoSearch getVideos(String userId, int maxVideos) throws VideoNotFoundException {
        return getVideos(userId, maxVideos, defaultMaxPages);
    }

    
    public DMVideoSearch getVideos(String userId, int maxVideos, int maxPages) throws VideoNotFoundException {
        int pageSize = maxVideos > 0 ? maxVideos : defaultMaxVideos;
        int pagesToFetch = maxPages > 0 ? maxPages : defaultMaxPages;
        String fields = "id,title,description,created_time,owner.id,owner.screenname,owner.url,owner.avatar_120_url,tags";
        List<DMVideo> allVideos = new ArrayList<>();
        DMVideoSearch aggregatedSearch = new DMVideoSearch();

        try {
            for (int page = 1; page <= pagesToFetch; page++) {
                String url = baseUri + "/user/" + userId + "/videos"
                        + "?fields=" + fields
                        + "&limit=" + pageSize
                        + "&page=" + page;

                DMVideoSearch pageSearch = restTemplate.getForObject(url, DMVideoSearch.class);
                if (pageSearch == null) {
                    break;
                }

                if (pageSearch.getList() != null) {
                    allVideos.addAll(pageSearch.getList());
                }

                aggregatedSearch.setPage(pageSearch.getPage());
                aggregatedSearch.setLimit(pageSearch.getLimit());
                aggregatedSearch.setTotal(pageSearch.getTotal());
                aggregatedSearch.setHasMore(pageSearch.getHasMore());

                if (!Boolean.TRUE.equals(pageSearch.getHasMore())) {
                    break;
                }
            }

            aggregatedSearch.setList(allVideos);
            return aggregatedSearch;
        } catch (HttpClientErrorException e) {
            throw new VideoNotFoundException();
        }
    }
}

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

/**
 * Servicio para recuperar los vídeos de un usuario/canal de Dailymotion.
 *
 * Endpoint utilizado:
 *   GET https://api.dailymotion.com/user/{userId}/videos
 *       ?fields=id,title,description,created_time,owner,tags
 *       &limit={maxVideos}
 *       &page={page}
 *
 * Notas de la API de Dailymotion:
 *  - El parámetro "limit" controla cuántos vídeos devuelve por página (máximo 100).
 *  - El parámetro "page" permite avanzar por las páginas de resultados.
 *  - "fields" es necesario para que la API devuelva owner y tags de forma consistente.
 *  - La respuesta incluye "list" (array de vídeos), "total" y "has_more".
 *  - No se requiere autenticación para vídeos públicos.
 */
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

    /**
     * Recupera los vídeos de un canal de Dailymotion.
     *
     * @param userId    identificador del usuario/canal en Dailymotion
     * @param maxVideos número máximo de vídeos a recuperar por página
     * @return DMVideoSearch con la lista de vídeos y metadatos de paginación
     * @throws VideoNotFoundException si el usuario no tiene vídeos o no existe
     */
    public DMVideoSearch getVideos(String userId, int maxVideos) throws VideoNotFoundException {
        return getVideos(userId, maxVideos, defaultMaxPages);
    }

    /**
     * Recupera y agrega varias páginas de vídeos de un canal de Dailymotion.
     *
     * @param userId    identificador del usuario/canal en Dailymotion
     * @param maxVideos número máximo de vídeos a recuperar por página
     * @param maxPages  número máximo de páginas a recorrer
     * @return DMVideoSearch con todos los vídeos encontrados en las páginas recorridas
     * @throws VideoNotFoundException si el usuario no tiene vídeos o no existe
     */
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

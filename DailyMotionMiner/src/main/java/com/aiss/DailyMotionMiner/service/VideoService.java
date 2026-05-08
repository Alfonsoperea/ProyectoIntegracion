package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.VideoNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para recuperar los vídeos de un usuario/canal de Dailymotion.
 *
 * Endpoint utilizado:
 *   GET https://api.dailymotion.com/user/{userId}/videos
 *       ?fields=id,title,description,created_time,owner,tags
 *       &limit={maxVideos}
 *
 * Notas de la API de Dailymotion:
 *  - El parámetro "limit" controla cuántos vídeos devuelve por página (máximo 100).
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

    /**
     * Recupera los vídeos de un canal de Dailymotion.
     *
     * @param userId    identificador del usuario/canal en Dailymotion
     * @param maxVideos número máximo de vídeos a recuperar
     * @return DMVideoSearch con la lista de vídeos y metadatos de paginación
     * @throws VideoNotFoundException si el usuario no tiene vídeos o no existe
     */
    public DMVideoSearch getVideos(String userId, int maxVideos) throws VideoNotFoundException {
        String fields = "id,title,description,created_time,owner.id,owner.screenname,owner.url,owner.avatar_120_url,tags";
        String url = baseUri + "/user/" + userId + "/videos"
                + "?fields=" + fields
                + "&limit=" + maxVideos;
        try {
            return restTemplate.getForObject(url, DMVideoSearch.class);
        } catch (HttpClientErrorException e) {
            throw new VideoNotFoundException();
        }
    }
}

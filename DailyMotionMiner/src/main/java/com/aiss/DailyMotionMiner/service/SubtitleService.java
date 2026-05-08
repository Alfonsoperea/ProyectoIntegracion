package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.CaptionNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtleSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para recuperar los subtítulos de un vídeo de Dailymotion.
 * Los subtítulos se mapean a los "captions" del modelo de VideoMiner.
 *
 * Endpoint utilizado:
 *   GET https://api.dailymotion.com/video/{videoId}/subtitles
 *       ?fields=id,language,language_label,url
 *
 * Notas de la API de Dailymotion:
 *  - Este endpoint devuelve los subtítulos disponibles para un vídeo dado.
 *  - Los campos "language" y "language_label" son especialmente útiles para
 *    identificar el idioma del subtítulo.
 *  - Si el vídeo no tiene subtítulos, se devuelve una lista vacía en "list".
 *  - Ante cualquier error HTTP, se devuelve una respuesta vacía en lugar de lanzar
 *    excepción, para no bloquear el procesamiento de otros vídeos.
 */
@Service
public class SubtitleService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    /**
     * Recupera los subtítulos de un vídeo de Dailymotion.
     *
     * @param videoId ID del vídeo en Dailymotion
     * @return DMSubtleSearch con la lista de subtítulos; si hay error, respuesta vacía
     */
    public DMSubtleSearch getSubtitles(String videoId) throws CaptionNotFoundException {
        String url = baseUri + "/video/" + videoId + "/subtitles"
                + "?fields=id,language,url";
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

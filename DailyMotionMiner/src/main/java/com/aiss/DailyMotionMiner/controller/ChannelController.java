package com.aiss.DailyMotionMiner.controller;

import com.aiss.DailyMotionMiner.etl.DailyMotionTransformer;
import com.aiss.DailyMotionMiner.exception.CaptionNotFoundException;
import com.aiss.DailyMotionMiner.exception.ChannelNotFoundException;
import com.aiss.DailyMotionMiner.exception.VideoNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtleSearch;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.model.videominer.VMVideo;
import com.aiss.DailyMotionMiner.service.ChannelService;
import com.aiss.DailyMotionMiner.service.SubtitleService;
import com.aiss.DailyMotionMiner.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST del DailyMotionMiner.
 *
 * Expone dos operaciones principales sobre el path base "/dailymotion":
 *
 *   GET  /dailymotion/{userId}  → Consulta de solo lectura (para pruebas).
 *                                  Obtiene, transforma y devuelve el canal SIN enviarlo a VideoMiner.
 *
 *   POST /dailymotion/{userId}  → Operación completa ETL.
 *                                  Obtiene de Dailymotion, transforma al formato común
 *                                  y envía el canal transformado a VideoMiner.
 *                                  Devuelve 201 CREATED con el objeto enviado.
 *
 * Parámetros de query opcionales:
 *   maxVideos → número máximo de vídeos a recuperar por página (por defecto: application.properties)
 *   maxPages  → número máximo de páginas a recorrer (por defecto: application.properties)
 *
 * Flujo ETL completo (POST):
 *   1. [Extract] ChannelService  → obtiene metadatos del canal (usuario) desde Dailymotion
 *   2. [Extract] VideoService    → obtiene la lista de vídeos del canal
 *   3. [Extract] SubtitleService → para cada vídeo, obtiene sus subtítulos
 *   4. [Transform] DailyMotionTransformer → convierte todos los objetos DM → VM
 *   5. [Load] RestTemplate.postForObject → envía el VMChannel a VideoMiner
 */
@RestController
@RequestMapping("/dailymotion")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private SubtitleService subtitleService;

    @Autowired
    private DailyMotionTransformer transformer;

    @Autowired
    private RestTemplate restTemplate;

    /** URI de VideoMiner, inyectada desde application.properties */
    @Value("${videominer.uri}")
    private String videoMinerUri;

    /** Límite por defecto de vídeos a extraer */
    @Value("${dailymotionminer.maxVideos}")
    private int defaultMaxVideos;

    /** Límite por defecto de páginas a recorrer */
    @Value("${dailymotionminer.maxPages}")
    private int defaultMaxPages;

    // ─── GET: operación de solo lectura para pruebas ─────────────────────────────

    /**
     * Devuelve el canal transformado al formato VideoMiner SIN enviarlo.
     * Útil para depuración e inspección manual antes de enviar.
     *
     * Ejemplo: GET http://localhost:8081/dailymotion/Euronews?maxVideos=5&maxPages=2
     */
    @GetMapping("/{userId}")
    public VMChannel getChannel(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int maxVideos,
            @RequestParam(defaultValue = "0") int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        int limit = maxVideos > 0 ? maxVideos : defaultMaxVideos;
        int pages = maxPages > 0 ? maxPages : defaultMaxPages;
        return fetchAndTransform(userId, limit, pages);
    }

    // ─── POST: operación ETL completa con envío a VideoMiner ─────────────────────

    /**
     * Extrae datos de Dailymotion, los transforma y los envía a VideoMiner.
     * Devuelve 201 CREATED con el objeto VMChannel enviado.
     *
     * Ejemplo: POST http://localhost:8081/dailymotion/Euronews?maxVideos=5&maxPages=2
     *
     * @param userId    ID o screenname del canal de Dailymotion
     * @param maxVideos número máximo de vídeos por página (opcional, usa el default si es 0)
     * @param maxPages  número máximo de páginas a recorrer (opcional, usa el default si es 0)
     */
    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VMChannel createChannel(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int maxVideos,
            @RequestParam(defaultValue = "0") int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        int limit = maxVideos > 0 ? maxVideos : defaultMaxVideos;
        int pages = maxPages > 0 ? maxPages : defaultMaxPages;

        // 1-4. Extract + Transform
        VMChannel vmChannel = fetchAndTransform(userId, limit, pages);

        // 5. Load: POST al endpoint de VideoMiner
        return restTemplate.postForObject(videoMinerUri, vmChannel, VMChannel.class);
    }

    // ─── Método auxiliar: Extract + Transform ────────────────────────────────────

    /**
     * Centraliza el proceso de Extract y Transform, reutilizado por GET y POST.
     *
     * @param userId    identificador del canal en Dailymotion
     * @param maxVideos número máximo de vídeos a extraer por página
     * @param maxPages  número máximo de páginas a recorrer
     * @return VMChannel transformado
     */
    private VMChannel fetchAndTransform(String userId, int maxVideos, int maxPages)
            throws ChannelNotFoundException, VideoNotFoundException, CaptionNotFoundException {

        // ── EXTRACT ──────────────────────────────────────────────────────────────

        // 1. Obtener metadatos del canal/usuario
        DMOwner dmChannel = channelService.getChannel(userId);

        // 2. Obtener los vídeos del canal
        DMVideoSearch videoSearch = videoService.getVideos(userId, maxVideos, maxPages);
        List<DMVideo> dmVideos = videoSearch.getList();

        // ── TRANSFORM ────────────────────────────────────────────────────────────

        // 3. Para cada vídeo: obtener subtítulos y transformar
        List<VMVideo> vmVideos = new ArrayList<>();
        if (dmVideos != null) {
            for (DMVideo dmVideo : dmVideos) {
                // Obtener subtítulos del vídeo (equivalente a captions en VideoMiner)
                DMSubtleSearch subtitleSearch = subtitleService.getSubtitles(dmVideo.getId());
                List<DMSubtle> subtitles =
                        subtitleSearch.getList();

                // Transformar vídeo + subtítulos → VMVideo
                VMVideo vmVideo = transformer.transformVideo(dmVideo, subtitles, dmChannel);
                vmVideos.add(vmVideo);
            }
        }

        // 4. Transformar canal con sus vídeos ya procesados → VMChannel
        return transformer.transformChannel(dmChannel, vmVideos);
    }
}

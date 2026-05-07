package com.aiss.DailyMotionMiner.etl;


import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.videominer.VMCaption;
import com.aiss.DailyMotionMiner.model.videominer.VMChannel;
import com.aiss.DailyMotionMiner.model.videominer.VMUser;
import com.aiss.DailyMotionMiner.model.videominer.VMVideo;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente ETL (Extract-Transform-Load) que convierte los modelos de Dailymotion
 * al formato común esperado por VideoMiner.
 *
 * Responsabilidades:
 *   - Convertir DMChannel  → VMChannel
 *   - Convertir DMVideo    → VMVideo
 *   - Convertir DMOwner    → VMUser
 *   - Convertir DMSubtitle → VMCaption
 *   - Transformar Unix timestamps de Dailymotion a strings ISO-8601
 *
 * Notas sobre la API de Dailymotion:
 *   - Los timestamps llegan como Long (segundos desde epoch, no milisegundos)
 *   - Los IDs de vídeo tienen el formato "xABCDE" (alfanumérico)
 *   - Los comentarios públicos NO están disponibles sin autenticación en la API v0,
 *     por lo que los vídeos se enviarán con lista de comentarios vacía ([])
 */
@Component
public class DailyMotionTransformer {

    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    // ─── Canal ──────────────────────────────────────────────────────────────────

    /**
     * Transforma un canal de Dailymotion al formato de VideoMiner.
     *
     * @param dmChannel canal de Dailymotion
     * @param videos    lista de vídeos ya transformados al formato VM
     * @return VMChannel listo para enviar a VideoMiner
     */
    public VMChannel transformChannel(DMOwner dmChannel, List<VMVideo> videos) {
        VMChannel vmChannel = new VMChannel();
        if (dmChannel == null) return vmChannel;

        vmChannel.setId(dmChannel.getId());
        vmChannel.setName(dmChannel.getScreenname());
        vmChannel.setDescription(dmChannel.getDescription());
        vmChannel.setCreatedTime(toIsoString(dmChannel.getCreatedTime()));
        vmChannel.setVideos(videos != null ? videos : new ArrayList<VMVideo>());

        return vmChannel;
    }

    // ─── Vídeo ──────────────────────────────────────────────────────────────────

    /**
     * Transforma un vídeo de Dailymotion al formato de VideoMiner.
     * Incluye el autor y los subtítulos, pero deja los comentarios vacíos
     * (la API pública de Dailymotion no expone comentarios sin autenticación).
     *
     * @param dmVideo    vídeo de Dailymotion
     * @param dmSubtitles lista de subtítulos del vídeo (puede ser null o vacía)
     * @return VMVideo listo para incluir en el canal
     */
    public VMVideo transformVideo(DMVideo dmVideo, List<com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle> dmSubtitles) {
        VMVideo vmVideo = new VMVideo();
        if (dmVideo == null) return vmVideo;

        vmVideo.setId(dmVideo.getId());
        // En VideoMiner el campo se llama "name" → usamos el "title" de Dailymotion
        vmVideo.setName(dmVideo.getTitle());
        vmVideo.setDescription(dmVideo.getDescription());
        // releaseTime usa el created_time del vídeo (fecha de subida)
        vmVideo.setReleaseTime(toIsoString(dmVideo.getCreatedTime()));

        // Transformar el propietario al modelo VMUser
        vmVideo.setAuthor(transformOwner(dmVideo.buildOwner()));

        // Dailymotion no ofrece comentarios en la API pública sin auth → lista vacía
        vmVideo.setComments(Collections.emptyList());

        // Transformar subtítulos a captions
        if (dmSubtitles != null && !dmSubtitles.isEmpty()) {
            vmVideo.setCaptions(
                    dmSubtitles.stream()
                            .map(this::transformSubtitle)
                            .collect(Collectors.toList())
            );
        } else {
            vmVideo.setCaptions(Collections.emptyList());
        }

        return vmVideo;
    }

    // ─── Owner → User ───────────────────────────────────────────────────────────

    /**
     * Transforma el propietario (owner) de un vídeo de Dailymotion al formato VMUser.
     *
     * @param owner propietario del vídeo
     * @return VMUser con los datos del autor
     */
    public VMUser transformOwner(DMOwner owner) {
        VMUser vmUser = new VMUser();
        if (owner == null) return vmUser;

        vmUser.setId(parseLongSafe(owner.getId()));
        vmUser.setName(owner.getScreenname());
        vmUser.setUser_link(owner.getUrl());
        vmUser.setPicture_link(owner.getAvatarUrl());

        return vmUser;
    }

    // ─── Subtítulo → Caption ────────────────────────────────────────────────────

    /**
     * Transforma un subtítulo de Dailymotion al formato VMCaption de VideoMiner.
     *
     * Mapeo de campos:
     *   DMSubtitle.id            → VMCaption.id
     *   DMSubtitle.url           → VMCaption.name    (URL del archivo de subtítulos)
     *   DMSubtitle.languageLabel → VMCaption.language (nombre completo del idioma)
     *
     * @param subtitle subtítulo de Dailymotion
     * @return VMCaption listo para incluir en el vídeo
     */
    public VMCaption transformSubtitle(Object subtitle) {
        VMCaption vmCaption = new VMCaption();
        if (subtitle == null) return vmCaption;

        vmCaption.setId(readString(subtitle, "getId"));
        vmCaption.setName(readString(subtitle, "getUrl"));
        vmCaption.setLanguage(readString(subtitle, "getLanguage"));

        return vmCaption;
    }

    // ─── Utilidades ─────────────────────────────────────────────────────────────

    /**
     * Convierte un Unix timestamp en segundos (Long) a una cadena ISO-8601 UTC.
     * Ejemplo: 1712345678 → "2024-04-05T18:34:38Z"
     *
     * La API de Dailymotion devuelve los timestamps como segundos (no milisegundos),
     * a diferencia de otras APIs que usan milisegundos.
     *
     * @param unixSeconds timestamp en segundos desde epoch (puede ser null)
     * @return cadena ISO-8601 o null si el input es null
     */
    private String toIsoString(Long unixSeconds) {
        if (unixSeconds == null) return null;
        return ISO_FORMATTER.format(Instant.ofEpochSecond(unixSeconds));
    }

    /**
     * Convierte un String a Long de forma segura, devolviendo null si falla.
     * Necesario porque el ID del owner de Dailymotion llega como String en el JSON.
     */
    private Long parseLongSafe(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String readString(Object target, String methodName) {
        try {
            Object value = target.getClass().getMethod(methodName).invoke(target);
            return value != null ? value.toString() : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}

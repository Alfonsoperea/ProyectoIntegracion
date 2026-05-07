package com.aiss.PeerTubeMiner.etl;

import aiss.videominer.model.Video;

import java.util.stream.Collectors;

@Service
public class MapperService {

    // Cambia 'ObjetoPeertube' por el nombre que le des a tu POJO cuando lo tengas
    // externalVideo -- objeto de PeerTube
    // VMVideo -- objeto de VideoMiner
    public VMVideo transformToVideoMiner(PeertubeVideo externalVideo) {
        VMVideo video = new Video();

        // Mapeo manual de campos según la Figura 2 del PDF
        video.setId(externalVideo.getUuid()); // Ejemplo de ID
        video.setName(externalVideo.getName());
        video.setDescription(externalVideo.getDescription());
        video.setReleaseTime(externalVideo.getPublishedAt());

        // Para el usuario (Owner/Account), recuerda que es una relación 1:1
        VMAccount user = new Account();
        user.setId(externalVideo.getUser().getId());
        user.setName(externalVideo.getUser().getName());
        video.setUser(user);

        video.setComments(externalVideo.getComments().stream().map(x -> transformComment(x)).collect(Collectors.toList()));
        video.setCaptions(externalVideo.getCaptions().stream().map(x -> transformCaption(x)).collect(Collectors.toList()));

        return video;
    }

    // Transformación de CAPTION (Subtitle en Dailymotion)
    public VMCaption transformCaption(PeertubeCaption source) {
        VMCaption target = new Caption();
        target.setId(source.getId()); // ID original de la API
        target.setLink(source.getCaptionUrl()); // URL del archivo de subtítulos
        target.setLanguage(source.getLanguage()); // Idioma (ej: "en", "es")
        return target;
    }

    // Transformación de COMMENT (Comment threads en PeerTube)
    public VMComment transformComment(PeertubeComment source) {
        VMComment target = new Comment();
        target.setId(source.getId());
        target.setText(source.getText()); // El contenido del comentario
        target.setCreatedOn(source.getCreatedAt()); // Fecha de creación

        // El diagrama muestra que Comment tiene un User asociado
        // target.setUser(transformUser(source.getAuthor()));

        return target;
    }
}
package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.ChannelNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para recuperar los metadatos de un canal (usuario) de Dailymotion.
 *
 * La API de Dailymotion modela los "canales" como usuarios (users).
 * Cada usuario de Dailymotion tiene un identificador único (userId o screenname).
 *
 * Endpoint utilizado:
 *   GET https://api.dailymotion.com/user/{userId}
 *       ?fields=id,screenname,description,created_time,url
 *
 * No requiere autenticación para datos públicos.
 */
@Service
public class ChannelService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${dailymotionminer.baseuri}")
    private String baseUri;

    /**
     * Recupera los metadatos de un canal/usuario de Dailymotion por su ID o screenname.
     *
     * @param userId identificador o screenname del usuario en Dailymotion
     * @return objeto DMChannel con los datos del canal
     * @throws ChannelNotFoundException si el usuario no existe (404 de la API)
     */
    public DMOwner getChannel(String userId) throws ChannelNotFoundException {
        // Campos solicitados a la API:
        //   id            → ID numérico del canal
        //   screenname    → nombre visible
        //   description   → descripción del canal
        //   created_time  → fecha de creación (Unix timestamp)
        //   url           → URL del perfil
        String url = baseUri + "/user/" + userId
                + "?fields=id,screenname,description,created_time,url";
        try {
            return restTemplate.getForObject(url, DMOwner.class);
        } catch (HttpClientErrorException e) {
            throw new ChannelNotFoundException();
        }
    }
}

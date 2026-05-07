package com.aiss.DailyMotionMiner.model.dailymotion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa un usuario/canal de Dailymotion tal como lo devuelve la API.
 *
 * En Dailymotion, el concepto de "canal" tal y como se concibe en VideoMiner se llama "user".
 *
 * nos va a proporcionar al mismo tiempo los datos para rellenar la clase VMChannel (el canal del creador)
 * y la clase VMUser (el Owner de los vídeos). No necesitas dos URIs distintas,
 * ¡con una matas dos pájaros de un tiro!
 *
 * Endpoint:
 *   GET https://api.dailymotion.com/user/{userId}
 *       ?fields=id,screenname,description,created_time,url,avatar_120_url
 *
 * Campos relevantes de la API de Dailymotion para el objeto user:
 *   - id            → identificador numérico único
 *   - screenname    → nombre visible del canal/usuario
 *   - description   → descripción del canal
 *   - created_time  → fecha de creación (Unix timestamp en segundos)
 *   - url           → URL del perfil
 *   - avatar_120_url → URL del avatar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DMOwner {

    /** ID único del usuario (ej. "x1234") */
    @JsonProperty("id")
    private String id;

    /** Nombre visible del canal/usuario */
    @JsonProperty("screenname")
    private String screenname;

    /** Descripción del canal */
    @JsonProperty("description")
    private String description;

    /** Fecha de creación del canal (Unix timestamp en segundos) */
    @JsonProperty("created_time")
    private Long createdTime;

    /** URL pública del perfil */
    @JsonProperty("url")
    private String url;

    /** URL del avatar del usuario */
    @JsonProperty("avatar_120_url")
    private String avatarUrl;

    // ---------- Getters y Setters ----------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getScreenname() { return screenname; }
    public void setScreenname(String screenname) { this.screenname = screenname; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatedTime() { return createdTime; }
    public void setCreatedTime(Long createdTime) { this.createdTime = createdTime; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    @Override
    public String toString() {
        return "DMChannel{id='" + id + "', screenname='" + screenname + "'}";
    }
}

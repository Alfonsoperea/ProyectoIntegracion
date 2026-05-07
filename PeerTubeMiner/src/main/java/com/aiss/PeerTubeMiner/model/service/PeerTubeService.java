package com.aiss.PeerTubeMiner.model.service;
import com.aiss.PeerTubeMiner.etl.MapperService;
import com.aiss.PeerTubeMiner.model.peertube.Account;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import com.aiss.PeerTubeMiner.model.videominer.VMChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PeerTubeService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MapperService mapperService;

    private final String PEERTUBE_API = "https://peertube2.cpy.re/api/v1";
    private final String VIDEOMINER_API = "http://localhost:8080/videominer/api/channels";

    public VMChannel fetchAndSave(String accountName, int maxVideos, int maxComments) {

        try {
            // 1. EXTRACT: Cuenta
            String accountUrl = PEERTUBE_API + "/accounts/" + accountName;
            Account ptAccount = restTemplate.getForObject(accountUrl, Account.class);

            // 2. EXTRACT: Vídeos
            String videosUrl = PEERTUBE_API + "/accounts/" + accountName + "/videos?count=" + maxVideos;
            VideoSearch ptVideos = restTemplate.getForObject(videosUrl, VideoSearch.class);

            // 3. TRANSFORM:
            // OJO: Si tu mapper no hace las peticiones de comentarios,
            // tendrías que pedirlos aquí vídeo por vídeo.
            // Asumiendo que tu mapper se encarga de llamar a la API para los comentarios:
            VMChannel vmChannel = mapperService.transformToVMChannel(ptAccount, ptVideos, maxComments);

            // 4. LOAD: Guardar en VideoMiner
            return restTemplate.postForObject(VIDEOMINER_API, vmChannel, VMChannel.class);

        } catch (HttpClientErrorException.NotFound e) {
            // Esto cumple con el requisito de devolver 404 si no existe en PeerTube
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found in PeerTube");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error mining PeerTube");
        }
    }
}

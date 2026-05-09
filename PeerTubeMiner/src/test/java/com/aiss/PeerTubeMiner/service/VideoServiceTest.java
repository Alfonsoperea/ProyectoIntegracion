package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.exception.VideoNotFoundException;
import com.aiss.PeerTubeMiner.model.peertube.VideoSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private VideoService service;

    @BeforeEach
    void setUp() {
        // Inicialización manual siguiendo paso a paso el patrón de tu amigo
        service = new VideoService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "apiUrl", "https://peertube.tv/api/v1");
    }

    // Método helper basado en el 'createTestObject' de tu amigo
    private VideoSearch createMockVideoSearch() {
        VideoSearch search = new VideoSearch();
        // Simulamos una respuesta con una lista vacía de vídeos
        search.setData(List.of());
        search.setTotal(0);
        return search;
    }

    @Test
    @DisplayName("getVideos devuelve la búsqueda de vídeos correctamente (Mock)")
    void getVideos_returnsVideoSearch() throws VideoNotFoundException {
        // 1. Preparamos la respuesta simulada (estilo createTestObject de tu amigo)
        VideoSearch apiResponse = createMockVideoSearch();

        when(restTemplate.getForObject(anyString(), eq(VideoSearch.class)))
                .thenReturn(apiResponse);

        // 2. Ejecutamos el método del servicio
        VideoSearch result = service.getVideos("ch-test", 10);

        // 3. Verificaciones (Assertions)
        assertNotNull(result, "El resultado de la búsqueda no debería ser nulo");
        assertEquals(0, result.getTotal(), "El total de vídeos debería ser 0");

        // 4. Verificamos que se llamó al RestTemplate una vez
        verify(restTemplate, times(1)).getForObject(anyString(), eq(VideoSearch.class));
    }

    @Test
    @DisplayName("getVideos lanza VideoNotFoundException ante un error 404 de la API")
    void getVideos_throwsExceptionOn404() {
        // Simulamos el error HttpClientErrorException (404 Not Found)
        when(restTemplate.getForObject(anyString(), eq(VideoSearch.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Verificamos que nuestro servicio captura el error y lanza la excepción correcta
        assertThrows(VideoNotFoundException.class, () -> {
            service.getVideos("canal-inexistente", 10);
        });
    }
}
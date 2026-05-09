package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.VideoNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideo;
import com.aiss.DailyMotionMiner.model.dailymotion.DMVideoSearch;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private VideoService service;

    @BeforeEach
    void setUp() {
        // Inicialización manual siguiendo el patrón de tu amigo
        service = new VideoService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "baseUri", "https://api.dailymotion.com");
        ReflectionTestUtils.setField(service, "defaultMaxVideos", 10);
        ReflectionTestUtils.setField(service, "defaultMaxPages", 1);
    }

    private DMVideoSearch createMockPage(String videoId, boolean hasMore) {
        DMVideo video = new DMVideo();
        video.setId(videoId);
        video.setTitle("Test Video");

        DMVideoSearch search = new DMVideoSearch();
        search.setList(List.of(video));
        search.setHasMore(hasMore);
        search.setPage(1);
        search.setLimit(10);
        return search;
    }

    @Test
    @DisplayName("getVideos devuelve la lista agregada de videos correctamente (Mock)")
    void getVideos_returnsAggregatedVideos() throws VideoNotFoundException {
        // 1. Preparamos la respuesta (estilo createTestObject de tu amigo)
        DMVideoSearch mockResponse = createMockPage("v001", false);

        when(restTemplate.getForObject(anyString(), eq(DMVideoSearch.class)))
                .thenReturn(mockResponse);

        // 2. Ejecutamos el servicio (pidiendo 1 página)
        DMVideoSearch result = service.getVideos("user-1", 10, 1);

        // 3. Verificaciones
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("v001", result.getList().get(0).getId());

        // 4. Verificamos que se llamó al RestTemplate
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(DMVideoSearch.class));
    }

    @Test
    @DisplayName("getVideos lanza VideoNotFoundException cuando la API responde 404")
    void getVideos_throwsExceptionOn404() {
        // Simulamos error 404
        when(restTemplate.getForObject(anyString(), eq(DMVideoSearch.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Verificamos excepción personalizada
        assertThrows(VideoNotFoundException.class, () -> {
            service.getVideos("user-falso", 10);
        });
    }

    @Test
    @DisplayName("getVideos se detiene si la API devuelve null")
    void getVideos_stopsOnNullResponse() throws VideoNotFoundException {
        // Simulamos que la primera llamada devuelve null
        when(restTemplate.getForObject(anyString(), eq(DMVideoSearch.class)))
                .thenReturn(null);

        DMVideoSearch result = service.getVideos("user-1", 10, 1);

        // El resultado debe tener una lista vacía de videos (inicializada en el service)
        assertTrue(result.getList().isEmpty());
    }
}
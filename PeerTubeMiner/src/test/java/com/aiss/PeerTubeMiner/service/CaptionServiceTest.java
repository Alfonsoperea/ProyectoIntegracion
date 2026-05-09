package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.exception.CaptionNotFoundException;
import com.aiss.PeerTubeMiner.model.peertube.CaptionSearch;
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
class CaptionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CaptionService service;

    @BeforeEach
    void setUp() {
        // Inicialización manual siguiendo paso a paso el patrón de tu amigo
        service = new CaptionService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "apiUrl", "https://peertube.tv/api/v1");
    }

    private CaptionSearch createMockCaptionSearch() {
        CaptionSearch search = new CaptionSearch();
        // Asumiendo que CaptionSearch tiene una lista de captions o datos similares
        search.setData(List.of());
        search.setTotal(0);
        return search;
    }

    @Test
    @DisplayName("getCaptions devuelve los subtítulos correctamente (Mock)")
    void getCaptions_returnsCaptionSearch() throws CaptionNotFoundException {
        // 1. Preparamos la respuesta simulada (estilo createTestObject de tu amigo)
        CaptionSearch apiResponse = createMockCaptionSearch();

        when(restTemplate.getForObject(anyString(), eq(CaptionSearch.class)))
                .thenReturn(apiResponse);

        // 2. Ejecutamos el método del servicio
        CaptionSearch result = service.getCaptions("video-uuid-123");

        // 3. Verificaciones (Assertions)
        assertNotNull(result, "El resultado no debería ser nulo");
        assertEquals(0, result.getTotal(), "El total de subtítulos debería coincidir");

        // 4. Verificamos que se llamó al RestTemplate una vez
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CaptionSearch.class));
    }

    @Test
    @DisplayName("getCaptions lanza CaptionNotFoundException ante un error 404 de la API")
    void getCaptions_throwsExceptionOn404() {
        // Simulamos el error HttpClientErrorException (404 Not Found)
        when(restTemplate.getForObject(anyString(), eq(CaptionSearch.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Verificamos que nuestro servicio captura el error y lanza la excepción correcta
        assertThrows(CaptionNotFoundException.class, () -> {
            service.getCaptions("uuid-inexistente");
        });
    }
}
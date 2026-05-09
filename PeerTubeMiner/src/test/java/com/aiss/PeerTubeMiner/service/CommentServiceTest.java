package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.exception.CommentNotFoundException;
import com.aiss.PeerTubeMiner.model.peertube.CommentSearch;
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
class CommentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private CommentService service;

    @BeforeEach
    void setUp() {
        // Inicialización manual idéntica a la de tu amigo en ObjectService
        service = new CommentService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "apiUrl", "https://peertube.tv/api/v1");
    }

    // Método helper basado en el 'createTestObject' de tu amigo
    private CommentSearch createMockCommentSearch() {
        CommentSearch search = new CommentSearch();
        // Asumiendo que CommentSearch tiene una estructura de lista similar a los otros modelos
        search.setData(List.of());
        search.setTotal(0);
        return search;
    }

    @Test
    @DisplayName("getComments devuelve la búsqueda de comentarios correctamente (Mock)")
    void getComments_returnsCommentSearch() throws CommentNotFoundException {
        // 1. Preparamos la respuesta simulada (estilo createTestObject)
        CommentSearch apiResponse = createMockCommentSearch();

        when(restTemplate.getForObject(anyString(), eq(CommentSearch.class)))
                .thenReturn(apiResponse);

        // 2. Ejecutamos el método del servicio
        CommentSearch result = service.getComments("video-uuid-456", 5);

        // 3. Verificaciones (Assertions) basadas en el estilo de tu amigo
        assertNotNull(result, "El resultado de comentarios no debería ser nulo");
        assertEquals(0, result.getTotal(), "El total de comentarios debería ser 0");

        // 4. Verificamos que se llamó al RestTemplate una vez
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CommentSearch.class));
    }

    @Test
    @DisplayName("getComments lanza CommentNotFoundException ante un error 404 de la API")
    void getComments_throwsExceptionOn404() {
        // Simulamos el error 404 de la API externa
        when(restTemplate.getForObject(anyString(), eq(CommentSearch.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Verificamos que nuestro servicio lanza la excepción personalizada correcta
        assertThrows(CommentNotFoundException.class, () -> {
            service.getComments("uuid-inexistente", 5);
        });
    }
}
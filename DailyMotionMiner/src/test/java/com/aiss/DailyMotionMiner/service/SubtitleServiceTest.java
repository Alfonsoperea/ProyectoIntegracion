package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.CaptionNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtle;
import com.aiss.DailyMotionMiner.model.dailymotion.DMSubtleSearch;
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
class SubtitleServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private SubtitleService service;

    @BeforeEach
    void setUp() {
        
        service = new SubtitleService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "baseUri", "https://api.dailymotion.com");
    }

    private DMSubtleSearch createMockSearch(String subId) {
        DMSubtle subtitle = new DMSubtle();
        subtitle.setId(subId);
        subtitle.setLanguage("en");
        subtitle.setUrl("https://dailymotion.com/sub.vtt");

        DMSubtleSearch search = new DMSubtleSearch();
        search.setList(List.of(subtitle));
        return search;
    }

    @Test
    @DisplayName("getSubtitles devuelve la búsqueda de subtítulos correctamente (Mock)")
    void getSubtitles_returnsSubtleSearch() throws CaptionNotFoundException {
        
        DMSubtleSearch apiResponse = createMockSearch("s100");

        when(restTemplate.getForObject(anyString(), eq(DMSubtleSearch.class)))
                .thenReturn(apiResponse);

        
        DMSubtleSearch result = service.getSubtitles("v123");

        
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("s100", result.getList().get(0).getId());

        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DMSubtleSearch.class));
    }

    @Test
    @DisplayName("getSubtitles lanza CaptionNotFoundException ante un error 404 de la API")
    void getSubtitles_throwsExceptionOn404() {
        
        when(restTemplate.getForObject(anyString(), eq(DMSubtleSearch.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        
        assertThrows(CaptionNotFoundException.class, () -> {
            service.getSubtitles("video-sin-subs");
        });
    }

    @Test
    @DisplayName("getSubtitles lanza CaptionNotFoundException si la API devuelve null")
    void getSubtitles_throwsExceptionOnNullResponse() {
        
        when(restTemplate.getForObject(anyString(), eq(DMSubtleSearch.class)))
                .thenReturn(null);

        
        assertThrows(CaptionNotFoundException.class, () -> {
            service.getSubtitles("v123");
        });
    }
}
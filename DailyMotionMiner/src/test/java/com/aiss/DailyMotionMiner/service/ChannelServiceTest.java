package com.aiss.DailyMotionMiner.service;

import com.aiss.DailyMotionMiner.exception.ChannelNotFoundException;
import com.aiss.DailyMotionMiner.model.dailymotion.DMOwner;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ChannelService service;

    @BeforeEach
    void setUp() {
        
        
        service = new ChannelService();
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "baseUri", "https://api.dailymotion.com");
    }

    private DMOwner createMockOwner(String id, String screenname) {
        DMOwner owner = new DMOwner();
        owner.setId(id);
        owner.setScreenname(screenname);
        owner.setDescription("Test Description");
        owner.setCreatedTime(1710000000L);
        return owner;
    }

    @Test
    @DisplayName("getChannel devuelve el canal correctamente (Mock)")
    void getChannel_returnsOwner() throws ChannelNotFoundException {
        
        DMOwner apiResponse = createMockOwner("x123", "euronews");

        when(restTemplate.getForObject(anyString(), eq(DMOwner.class)))
                .thenReturn(apiResponse);

        
        DMOwner result = service.getChannel("euronews");

        
        assertNotNull(result);
        assertEquals("x123", result.getId());
        assertEquals("euronews", result.getScreenname());

        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(DMOwner.class));
    }

    @Test
    @DisplayName("getChannel lanza ChannelNotFoundException ante un 404 de la API")
    void getChannel_throwsExceptionOn404() {
        
        when(restTemplate.getForObject(anyString(), eq(DMOwner.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        
        assertThrows(ChannelNotFoundException.class, () -> {
            service.getChannel("usuario-inexistente");
        });
    }
}
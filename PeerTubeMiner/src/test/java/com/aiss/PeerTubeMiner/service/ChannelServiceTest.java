package com.aiss.PeerTubeMiner.service;

import com.aiss.PeerTubeMiner.exception.ChannelNotFoundException;
import com.aiss.PeerTubeMiner.model.peertube.User;
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
        ReflectionTestUtils.setField(service, "apiUrl", "https://peertube.tv/api/v1");
    }

    
    private User createMockUser(String id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setDescription("PeerTube Channel Description");
        user.setCreatedAt("2026-05-09");
        return user;
    }

    @Test
    @DisplayName("getUser devuelve el usuario/canal correctamente (Mock)")
    void getUser_returnsUser() throws ChannelNotFoundException {
        
        User apiResponse = createMockUser("u123", "ch-test");

        when(restTemplate.getForObject(anyString(), eq(User.class)))
                .thenReturn(apiResponse);

        
        User result = service.getUser("ch-test");

        
        assertNotNull(result);
        assertEquals("u123", result.getId());
        assertEquals("ch-test", result.getName());

        
        verify(restTemplate, times(1)).getForObject(anyString(), eq(User.class));
    }

    @Test
    @DisplayName("getUser lanza ChannelNotFoundException ante un error 404 de la API")
    void getUser_throwsExceptionOn404() {
        
        when(restTemplate.getForObject(anyString(), eq(User.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        
        assertThrows(ChannelNotFoundException.class, () -> {
            service.getUser("canal-fantasma");
        });
    }
}
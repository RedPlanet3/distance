package org.nvictor.distance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nvictor.distance.model.AddressResult;
import org.nvictor.distance.repository.AddressResultRepository;
import org.nvictor.distance.web.dto.response.AddressResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private YandexService yandexService;
    @Mock
    private DadataService dadataService;
    @Mock
    private AddressResultRepository repository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void compareAddressCoordinates_Success() {
        String address = "Москва, Красная площадь";
        double[] yandexCoords = {55.753544, 37.621202};
        double[] dadataCoords = {55.7535859, 37.6210462};

        when(yandexService.getCoordinates(address)).thenReturn(Mono.just(yandexCoords));
        when(dadataService.getCoordinates(address)).thenReturn(Mono.just(dadataCoords));
        when(repository.save(any(AddressResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.compareAddressCoordinates(address).block();

        assertNotNull(response);
        assertEquals(55.753544, response.getYandexLatitude());
        assertEquals(37.621202, response.getYandexLongitude());
        assertEquals(55.7535859, response.getDadataLatitude());
        assertEquals(37.6210462, response.getDadataLongitude());
        assertTrue(response.getDistanceInMeters() > 0);
        assertTrue(response.getMessage().contains("Distance between Yandex and Dadata coordinates"));

        // Проверяем, что результат был сохранён
        ArgumentCaptor<AddressResult> captor = ArgumentCaptor.forClass(AddressResult.class);
        verify(repository, times(1)).save(captor.capture());
        AddressResult saved = captor.getValue();
        assertEquals(address, saved.getAddress());
    }

    @Test
    void compareAddressCoordinates_YandexFails() {
        String address = "Москва, Красная площадь";
        double[] dadataCoords = {55.7535859, 37.6210462};

        when(yandexService.getCoordinates(address)).thenReturn(Mono.error(new RuntimeException("Yandex error")));
        when(dadataService.getCoordinates(address)).thenReturn(Mono.just(dadataCoords));
        when(repository.save(any(AddressResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.compareAddressCoordinates(address).block();

        assertNotNull(response);
        assertEquals(0.0, response.getYandexLatitude());
        assertEquals(0.0, response.getYandexLongitude());
        assertEquals(55.7535859, response.getDadataLatitude());
        assertEquals(37.6210462, response.getDadataLongitude());
        assertTrue(response.getMessage().contains("Yandex API failed"));
    }

    @Test
    void compareAddressCoordinates_DadataFails() {
        String address = "Москва, Красная площадь";
        double[] yandexCoords = {55.753544, 37.621202};

        when(yandexService.getCoordinates(address)).thenReturn(Mono.just(yandexCoords));
        when(dadataService.getCoordinates(address)).thenReturn(Mono.error(new RuntimeException("Dadata error")));
        when(repository.save(any(AddressResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.compareAddressCoordinates(address).block();

        assertNotNull(response);
        assertEquals(55.753544, response.getYandexLatitude());
        assertEquals(37.621202, response.getYandexLongitude());
        assertEquals(0.0, response.getDadataLatitude());
        assertEquals(0.0, response.getDadataLongitude());
        assertTrue(response.getMessage().contains("Dadata API failed"));
    }

    @Test
    void compareAddressCoordinates_BothFail() {
        String address = "Несуществующий адрес";

        when(yandexService.getCoordinates(address)).thenReturn(Mono.error(new RuntimeException("Yandex error")));
        when(dadataService.getCoordinates(address)).thenReturn(Mono.error(new RuntimeException("Dadata error")));
        when(repository.save(any(AddressResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressResponse response = addressService.compareAddressCoordinates(address).block();

        assertNotNull(response);
        assertEquals(0.0, response.getYandexLatitude());
        assertEquals(0.0, response.getYandexLongitude());
        assertEquals(0.0, response.getDadataLatitude());
        assertEquals(0.0, response.getDadataLongitude());
        assertTrue(response.getMessage().contains("Both APIs failed"));
    }
} 
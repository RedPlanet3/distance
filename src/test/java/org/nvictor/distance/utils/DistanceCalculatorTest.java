package org.nvictor.distance.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void calculateDistance_SamePoint_ReturnsZero() {
        // Arrange
        double lat1 = 55.753544;
        double lon1 = 37.621202;
        double lat2 = 55.753544;
        double lon2 = 37.621202;

        // Act
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void calculateDistance_MoscowRedSquare_ReturnsExpectedDistance() {
        // Arrange
        double lat1 = 55.753544; // Яндекс координаты, Москва Красная площадь
        double lon1 = 37.621202;
        double lat2 = 55.7535859; // Dadata координаты, Москва Красная площадь
        double lon2 = 37.6210462;

        // Act
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertTrue(distance > 0);
        assertTrue(distance < 100); //Меньше 100 метров
        assertEquals(10.81, distance, 1.0); // Погрешность в метр
    }

    @Test
    void calculateDistance_LargeDistance_ReturnsCorrectValue() {
        // Arrange
        double lat1 = 55.753544; // Москва
        double lon1 = 37.621202;
        double lat2 = 59.932464; // Санкт-Петербург
        double lon2 = 30.349258;

        // Act
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertTrue(distance > 600000); // Больше 600 км
        assertTrue(distance < 700000); // Меньше 700 км
    }

    @Test
    void calculateDistance_ZeroCoordinates_ReturnsZero() {
        // Arrange
        double lat1 = 0.0;
        double lon1 = 0.0;
        double lat2 = 0.0;
        double lon2 = 0.0;

        // Act
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void calculateDistance_SmallDistance_ReturnsAccurateValue() {
        // Arrange
        double lat1 = 55.753544;
        double lon1 = 37.621202;
        double lat2 = 55.753544;
        double lon2 = 37.621203; // По долготе разница в 1 метр

        // Act
        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // Assert
        assertTrue(distance > 0);
        assertTrue(distance < 1.0); // Дистанция меньше метра
    }
} 
# Distance Service

## Описание

Distance Service — это Spring Boot приложение для сравнения координат, полученных с помощью API Яндекс и Dadata, и вычисления расстояния между ними для заданного адреса. Сервис сохраняет результаты в базу данных и предоставляет REST API для работы с адресами.

### Основные возможности:
- Геокодирование адреса через Яндекс и Dadata
- Расчёт расстояния между координатами
- Сохранение результатов в базу данных
- REST API для сравнения координат
- Логирование ошибок и ключевых событий

## Технологии
- Java 17
- Spring Boot 3
- WebFlux
- JPA (Hibernate)
- MySQL
- Docker, Docker Compose
- JUnit 5

## Быстрый старт

### 1. Клонируйте репозиторий
```bash
git clone https://github.com/RedPlanet3/distance
cd distance/distance
```

### 2. Настройте переменные окружения
Создайте файл `.env` в корне проекта и укажите ключи:
```
DADATA_API_KEY=your_dadata_api_key
DADATA_SECRET=your_dadata_secret
YANDEX_API_KEY=your_yandex_api_key
```

### 3. Соберите проект
```bash
./gradlew clean build
```

### 4. Запуск через Docker Compose
```bash
docker-compose up --build
```

Приложение будет доступно на `http://localhost:8080`.

## API

### POST `/api/address/compare`
**Описание:** Получить координаты из Яндекс и Dadata, рассчитать расстояние и сохранить результат.

**Пример запроса:**
```json
{
  "address": "Москва, Красная площадь"
}
```

**Пример ответа:**
```json
{
  "yandexLatitude": 55.753544,
  "yandexLongitude": 37.621202,
  "dadataLatitude": 55.7535859,
  "dadataLongitude": 37.6210462,
  "distanceInMeters": 10.81,
  "message": "Distance between Yandex and Dadata coordinates: 10.81 meters"
}
```

## Тестирование

Для запуска тестов выполните:
```bash
./gradlew test
```

## Примечания
- Все параметры конфигурации задаются через `application.yml` и переменные окружения.
- Для работы необходимы валидные ключи Dadata и Яндекс.
- Для запуска через Docker Compose требуется установленный Docker.

---

**Автор:** Elena 

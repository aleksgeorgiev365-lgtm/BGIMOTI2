# 🏠 БГИмоти — Уеб приложение за недвижими имоти

## Технологии
- **Backend:** Java 17 + Spring Boot 3.2 + Spring Security + Spring Data JPA
- **База данни:** MySQL 8
- **Frontend:** HTML5 + CSS3 + Vanilla JavaScript
- **Парола хеширане:** BCrypt (Spring Security)
- **REST API:** JSON комуникация между frontend и backend

---

## ⚙️ Инсталация и стартиране

### 1. Изисквания
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Браузър (Chrome, Firefox, Edge)

---

### 2. MySQL — настройка на базата данни

```sql
-- Отворете MySQL Workbench или терминал и изпълнете:
CREATE DATABASE bgimoti CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

След това редактирайте `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bgimoti?...
spring.datasource.username=root
spring.datasource.password=ВАШАТА_ПАРОЛА
```

---

### 3. Spring Boot — стартиране

```bash
# В главната папка на проекта:
cd bgimoti
mvn spring-boot:run
```

Сървърът стартира на: **http://localhost:8080**

При стартиране `schema.sql` автоматично създава таблиците, а `data.sql` зарежда примерни данни.

---

### 4. Frontend — отворете HTML файла

Отворете `frontend/imoti_bg.html` в браузър.

Приложението автоматично:
- Проверява дали Spring Boot работи на `http://localhost:8080`
- При **онлайн** режим — използва реалната MySQL база данни
- При **офлайн** режим — работи с вградени демо данни

---

## 🔑 Тестови акаунти (парола: `password123`)

| Роля          | Имейл               |
|---------------|---------------------|
| 🛡️ Администратор | admin@bgimoti.bg  |
| 🏢 Брокер     | maria@bgimoti.bg    |
| 👤 Клиент     | ivan@example.com    |

---

## 🌐 REST API Endpoints

### Автентикация
| Метод | URL                  | Описание                    |
|-------|----------------------|-----------------------------|
| POST  | `/api/auth/login`    | Вход с имейл и парола       |
| POST  | `/api/auth/register` | Регистрация на нов потребител|

**Пример — вход:**
```json
POST /api/auth/login
{
  "email": "admin@bgimoti.bg",
  "password": "password123"
}
```

**Успешен отговор (200):**
```json
{
  "success": true,
  "message": "Добре дошли, Администратор!",
  "userId": 1,
  "name": "Администратор",
  "email": "admin@bgimoti.bg",
  "role": "ADMIN"
}
```

**Грешни данни (401):**
```json
{
  "success": false,
  "message": "Невалиден имейл или парола. Моля опитайте отново."
}
```

### Имоти
| Метод  | URL                          | Описание                  |
|--------|------------------------------|---------------------------|
| GET    | `/api/properties`            | Всички активни обяви      |
| GET    | `/api/properties/search`     | Търсене с филтри          |
| GET    | `/api/properties/{id}`       | Детайли за обява          |
| POST   | `/api/properties`            | Публикуване на нова обява |
| DELETE | `/api/properties/{id}`       | Изтриване на обява        |
| POST   | `/api/properties/{id}/rate`  | Оценяване на брокер       |

**Пример — търсене:**
```
GET /api/properties/search?city=Sofia&type=APARTMENT&minPrice=50000&maxPrice=200000&rooms=3
```

---

## 📁 Структура на проекта

```
bgimoti/
├── pom.xml                          ← Maven зависимости
├── src/main/
│   ├── java/bg/imoti/
│   │   ├── BgImotiApplication.java  ← Главен клас
│   │   ├── config/
│   │   │   └── SecurityConfig.java  ← BCrypt + CORS + Security
│   │   ├── controller/
│   │   │   ├── AuthController.java  ← /api/auth/login, /register
│   │   │   └── PropertyController.java ← /api/properties/**
│   │   ├── model/
│   │   │   ├── User.java            ← Потребител (CLIENT/BROKER/ADMIN)
│   │   │   ├── Property.java        ← Обява за имот
│   │   │   └── BrokerRating.java    ← Оценка на брокер
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── PropertyRepository.java  ← JPQL търсене
│   │   │   └── BrokerRatingRepository.java
│   │   └── service/
│   │       └── UserService.java     ← Бизнес логика за акаунти
│   └── resources/
│       ├── application.properties   ← MySQL конфигурация
│       ├── schema.sql               ← Таблици (CREATE TABLE)
│       └── data.sql                 ← Начални данни (INSERT)
└── frontend/
    └── imoti_bg.html                ← Клиентска страна (HTML/CSS/JS)
```

---

## 🔒 Сигурност

- Паролите се хешират с **BCrypt** (strength=10) — никога не се пазят в plaintext
- Spring Security защитава всички endpoints
- CORS е конфигуриран да позволява заявки от HTML файла
- Валидация на входа — клиентска (JS) и сървърна (Spring `@Valid`)

# API Документация

## Аутентификация

Большинство эндпоинтов требуют JWT токен в заголовке:
(в нем зашит логин, айди пользователя)

Authorization: Bearer <jwt-token>

---

##  **Auth Controller** (`/api/auth`)

Публичные эндпоинты для регистрации и входа.

### Регистрация нового пользователя
```http
POST /auth/register
```
Request body:
```json
{
"login": "johndoe",
"password": "password123",
"firstName": "John",
"surname": "Doe",
"patronymic": "Smith"
}
```
Response: `200 OK` 

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "login": "johndoe",
  "userId": 1
}
```

### Вход в систему

```http 
POST /auth/login
```

Request body:

```json
{
  "login": "johndoe",
  "password": "password123"
}
```
Response: `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "login": "johndoe",
  "userId": 1
}
```

---

##  **User Controller** (`/api/users`)

Эндпоинты для работы с текущим пользователем.

### Получить информацию о текущем пользователе

```http 
GET /users/me
```

Response: `200 OK`

```json
{
  "id": 1,
  "login": "johndoe",
  "first_name": "John",
  "surname": "Doe",
  "patronymic": "Smith",
  "created_at": "2024-01-01T10:00:00",
  "updated_at": "2024-01-01T10:00:00"
}
```

### Обновить профиль

```http 
PUT /users/me
```

Request body:

```json
{
  "first_name": "Johnathan",
  "surname": "Doe",
  "patronymic": "Smith"
}
```
Response: `202 ACCEPTED`

```json
{
  "id": 1,
  "login": "johndoe",
  "first_name": "Johnathan",
  "surname": "Doe",
  "patronymic": "Smith",
  "created_at": "2024-01-01T10:00:00",
  "updated_at": "2024-01-01T11:00:00"
}
```

### Удалить свой аккаунт

```http
DELETE /users/me
```

Response: `200 OK`

### Сменить пароль

```http request
POST /users/me/change-password
```

Request body:

```json
{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```

Response: `202 ACCEPTED`

---

## **Admin User Controller** (`/api/admin/users`)

Эндпоинты для администраторов (требуется роль `ADMIN`).

### Получить пользователя по ID

```http request
GET /admin/users/{id}
```

Response: `200 OK`

### Обновить пользователя по ID

```http request
PUT /admin/users/{id}
```

Request body:

```json
{
  "first_name": "Johnathan",
  "surname": "Doe",
  "patronymic": "Smith"
}
```

Response: `200 OK`

```json
{
  "id": 1,
  "login": "johndoe",
  "first_name": "John",
  "surname": "Doe",
  "patronymic": "Smith",
  "created_at": "2024-01-01T10:00:00",
  "updated_at": "2024-01-01T10:00:00"
}
```

### Удалить пользователя по ID

```http request
DELETE /admin/users/{id}
```

Response: `200 OK`


### Сменить пароль пользователя

```http request
POST /admin/users/{id}/change-password
```

Request body:

```json
{
  "oldPassword": "password123",
  "newPassword": "newpassword456"
}
```
Response: `202 ACCEPTED`

---

## **Board Controller** (`/api/boards`)

Управление досками.

### Создать доску

```http 
POST /boards/create
```

Request body:

```json
{
  "name": "Мой проект",
  "description": "Описание проекта"
}
```
Response: `201 CREATED`

```json
{
  "id": 1,
  "name": "Мой проект",
  "description": "Описание проекта",
  "ownerId": 1,
  "created_at": "2024-01-01T10:00:00",
  "updated_at": "2024-01-01T10:00:00"
}
```

### Получить доску по ID

```http request
GET /boards/{id}
```
Response: `200 OK`

```json
{
  "id": 1,
  "name": "Мой проект",
  "description": "Описание проекта",
  "ownerId": 1,
  "created_at": "2026-02-25T12:00:00",
  "updated_at": "2026-02-25T12:00:00"
}
```

### Обновить доску

```http request
PUT /boards/{id}
```

Request body:

```json
{
  "name": "Новое название",
  "description": "Новое описание"
}
```

Response: `202 ACCEPTED`

```json
{
  "id": 1,
  "name": "Новое название",
  "description": "Новое описание",
  "ownerId": 1,
  "created_at": "2026-02-25T12:00:00",
  "updated_at": "2026-02-25T12:00:00"
}
```

### Удалить доску

```http request
DELETE /boards/{id}
```

Response: `200 OK`

---

## Admin Board Controller (`api/admin/boards`)

Все эндпоинты аналогичны обычным, но с префиксом /admin/boards и не проверяют права на доску (достаточно роли ADMIN).

### Создать доску

```http request
POST /admin/boards/create
```

### Получить доску по ID

```http request
GET /admin/boards/{id}
```

### Обновить доску

```http request
PUT /admin/boards/{id}
```

### Удалить доску

```http request
DELETE /admin/boards/{id}
```

---

## Board Members (`api/boards/{board_id}/members`)

Эндпоинты для отслеживания участников доски

### Добавить участника в доску

```http request
POST /boards/{board_id}/members
```

Request body:

```json
{
  "userId": 2,
  "role": "MEMBER"   // OWNER, MEMBER, VIEWER
}
```

Response: `201 CREATED`

```json
{
  "boardId": 1,
  "userId": 2,
  "role": "MEMBER",
  "joined_at": "2026-02-25T12:00:00",
  "left_at": null
}
```

### Получить всех участников доски

```http request
GET /boards/{board_id}/members
```

Response: `200 OK`

```json
[
  {
    "boardId": 1,
    "userId": 1,
    "role": "OWNER",
    "joined_at": "2026-02-25T12:00:00",
    "left_at": null
  },
  {
    "boardId": 1,
    "userId": 2,
    "role": "MEMBER",
    "joined_at": "2026-02-25T12:05:00",
    "left_at": null
  }
]
```

### Обновить роль участника

```http request
PUT /boards/{board_id}/members
```

Request body:

```json
{
  "userId": 2,
  "role": "MEMBER"   // OWNER, MEMBER, VIEWER
}
```

Response: `202 ACCEPTED`

### Удалить участника из доски (soft delete)

```http request
DELETE /boards/{board_id}/members/{user_id}
```

Response: `202 ACCEPTED`

---

## Admin Board Members

Аналогично обычным, но с путем `api/admin/boards/{board_id}/members`

---

## Tasks (`api/boards/{board_id}/tasks`)

Эндпоинты для отслеживания задач доски

### Получить все задачи доски

```http request
GET /boards/{board_id}/tasks
```

Response: `200 OK`

```json
[
  {
    "id": 1,
    "boardId": 1,
    "title": "Сделать авторизацию",
    "description": "Реализовать JWT",
    "status": "TODO",
    "priority": "HIGH",
    "position": 1,
    "creatorId": 1,
    "assigneeIds": [2, 3],
    "created_at": "2026-02-25T12:00:00",
    "updated_at": "2026-02-25T12:00:00"
  }
]
```

### Получить задачу по ID

```http request
GET /boards/{board_id}/tasks/{task_id}
```

Response: `200 OK`

```json
{
  "id": 1,
  "boardId": 1,
  "title": "Сделать авторизацию",
  "description": "Реализовать JWT",
  "status": "TODO",
  "priority": "HIGH",
  "position": 1,
  "creatorId": 1,
  "assigneeIds": [2, 3],
  "created_at": "2026-02-25T12:00:00",
  "updated_at": "2026-02-25T12:00:00"
}
```

### Создать задачу

```http request
POST /boards/{board_id}/tasks
```

Request body:

```json
{
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "TODO",
  "priority": "MEDIUM",
  "position": 2,
  "assigneeIds": [2, 4]
}
```

Response: `201 CREATED`

```json
{
  "id": 2,
  "boardId": 1,
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "TODO",
  "priority": "MEDIUM",
  "position": 2,
  "creatorId": 1,
  "assigneeIds": [2, 4],
  "created_at": "2026-02-25T12:10:00",
  "updated_at": "2026-02-25T12:10:00"
}
```

### Обновить задачу

```http request
PUT /boards/{board_id}/tasks/{taskId}
```

Request body: 
```json
{
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "TODO",
  "priority": "MEDIUM",
  "position": 2,
  "assigneeIds": [2, 4]
}
```

Response: `202 ACCEPTED`

```json
{
  "id": 2,
  "boardId": 1,
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "TODO",
  "priority": "MEDIUM",
  "position": 2,
  "creatorId": 1,
  "assigneeIds": [2, 4],
  "created_at": "2026-02-25T12:10:00",
  "updated_at": "2026-02-25T12:10:00"
}
```

### Удалить задачу

```http request
DELETE /boards/{board_id}/tasks/{taskId}
```

Response: `200 OK`

### Обновить статус задачи

```http request
PATCH /boards/{board_id}/tasks/{taskId}/status?status=DONE
```

Query param: `status` (одно из: TODO, IN_PROGRESS, TESTING, DONE)

Response: `200 OK`

```json
{
  "id": 2,
  "boardId": 1,
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "DONE",
  "priority": "MEDIUM",
  "position": 2,
  "creatorId": 1,
  "assigneeIds": [2, 4],
  "created_at": "2026-02-25T12:10:00",
  "updated_at": "2026-02-25T12:15:00"
}
```

### Обновить исполнителей задачи

```http request
PATCH /boards/{board_id}/tasks/{taskId}/assignees
```

Request body (массив ID пользователей):

```json
[2, 3, 5]
```

Response: `200 OK`

```json
{
  "id": 2,
  "boardId": 1,
  "title": "Написать тесты",
  "description": "Покрыть сервисы юнит-тестами",
  "status": "DONE",
  "priority": "MEDIUM",
  "position": 2,
  "creatorId": 1,
  "assigneeIds": [2, 3, 5],
  "created_at": "2026-02-25T12:10:00",
  "updated_at": "2026-02-25T12:15:00"
}
```

---

## Admin Tasks

Аналогично обычным, но с префиксом /admin/boards/{board_id}/tasks. Все методы дублируются.

## Примечания

- Во всех ответах поля `created_at` и `updated_at` имеют формат ISO 8601.
 
- Для `assigneeIds` в задачах может быть пустой массив.

- Роли участников доски: `OWNER`, `MEMBER`, `VIEWER`.

- Глобальные роли пользователей: `USER`, `ADMIN`.

- При удалении участника доски выполняется soft delete — проставляется `left_at`, запись остаётся в БД.

- При удалении доски каскадно удаляются все задачи и записи об участниках.

- При удалении пользователя каскадно удаляются все его доски, задачи и членства.
- 
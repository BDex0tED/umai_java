# Postman Examples для Dropbox Integration

## 🚀 Настройка Postman

### 1. Базовый URL
```
http://localhost:8080
```

### 2. Headers для всех запросов
```
Content-Type: multipart/form-data
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 📤 Загрузка файлов

### 1. Загрузить произвольный файл
```http
POST {{baseUrl}}/api/dropbox/upload
Content-Type: multipart/form-data

Body (form-data):
- file: [выберите файл]
- subfolder: covers
```

**Пример ответа:**
```json
{
    "url": "https://www.dropbox.com/s/abc123/filename.jpg?raw=1",
    "message": "File uploaded successfully"
}
```

### 2. Загрузить фото профиля
```http
POST {{baseUrl}}/api/dropbox/upload-profile-photo
Content-Type: multipart/form-data

Body (form-data):
- profilePhoto: [выберите изображение]
```

**Пример ответа:**
```json
{
    "url": "https://www.dropbox.com/s/def456/profile_photo.jpg?raw=1",
    "message": "Profile photo uploaded successfully"
}
```

### 3. Загрузить обложку произведения
```http
POST {{baseUrl}}/api/dropbox/upload-cover/1
Content-Type: multipart/form-data

Body (form-data):
- coverImage: [выберите изображение]
```

**Пример ответа:**
```json
{
    "url": "https://www.dropbox.com/s/ghi789/cover.jpg?raw=1",
    "message": "Cover uploaded successfully"
}
```

---

## 🗑️ Удаление файлов

### 1. Удалить фото профиля
```http
DELETE {{baseUrl}}/api/dropbox/delete-profile-photo
```

**Пример ответа:**
```json
{
    "message": "Profile photo deleted successfully"
}
```

### 2. Удалить обложку произведения
```http
DELETE {{baseUrl}}/api/dropbox/delete-cover/1
```

**Пример ответа:**
```json
{
    "message": "Cover deleted successfully"
}
```

---

## 🧹 Очистка файлов

### 1. Ручная очистка папки
```http
POST {{baseUrl}}/api/dropbox/cleanup
Content-Type: application/x-www-form-urlencoded

Body (x-www-form-urlencoded):
- subfolder: covers
- daysOld: 7
```

**Пример ответа:**
```json
{
    "deletedCount": 5,
    "subfolder": "covers",
    "daysOld": 7,
    "message": "Cleanup completed successfully"
}
```

### 2. Очистка всех временных файлов
```http
POST {{baseUrl}}/api/dropbox/cleanup-all
```

**Пример ответа:**
```json
{
    "deletedCount": 12,
    "message": "All cleanup completed successfully"
}
```

---

## 🔍 Проверка файлов

### 1. Проверить существование файла
```http
GET {{baseUrl}}/api/dropbox/file-exists?filePath=/umai-temp-storage/covers/filename.jpg
```

**Пример ответа:**
```json
{
    "exists": true,
    "filePath": "/umai-temp-storage/covers/filename.jpg"
}
```

---

## 📋 Полная коллекция Postman

### Переменные окружения
Создайте переменные в Postman:
```
baseUrl = http://localhost:8080
token = YOUR_JWT_TOKEN
```

### 1. Загрузка файла в папку covers
```http
POST {{baseUrl}}/api/dropbox/upload
Content-Type: multipart/form-data
Authorization: Bearer {{token}}

Body (form-data):
- file: [выберите файл]
- subfolder: covers
```

### 2. Загрузка файла в папку profiles
```http
POST {{baseUrl}}/api/dropbox/upload
Content-Type: multipart/form-data
Authorization: Bearer {{token}}

Body (form-data):
- file: [выберите файл]
- subfolder: profiles/testuser
```

### 3. Загрузка файла в папку temp
```http
POST {{baseUrl}}/api/dropbox/upload
Content-Type: multipart/form-data
Authorization: Bearer {{token}}

Body (form-data):
- file: [выберите файл]
- subfolder: temp
```

---

## 🧪 Тестовые сценарии

### Сценарий 1: Загрузка обложки произведения
1. **Загрузить обложку:**
   ```http
   POST {{baseUrl}}/api/dropbox/upload-cover/1
   Content-Type: multipart/form-data
   Authorization: Bearer {{token}}
   
   Body (form-data):
   - coverImage: [выберите изображение обложки]
   ```

2. **Проверить результат:**
   ```http
   GET {{baseUrl}}/api/works/1
   Authorization: Bearer {{token}}
   ```

### Сценарий 2: Загрузка фото профиля
1. **Загрузить фото:**
   ```http
   POST {{baseUrl}}/api/dropbox/upload-profile-photo
   Content-Type: multipart/form-data
   Authorization: Bearer {{token}}
   
   Body (form-data):
   - profilePhoto: [выберите фото профиля]
   ```

2. **Получить информацию о пользователе:**
   ```http
   GET {{baseUrl}}/api/users/me
   Authorization: Bearer {{token}}
   ```

### Сценарий 3: Очистка старых файлов
1. **Очистить папку covers:**
   ```http
   POST {{baseUrl}}/api/dropbox/cleanup
   Content-Type: application/x-www-form-urlencoded
   Authorization: Bearer {{token}}
   
   Body (x-www-form-urlencoded):
   - subfolder: covers
   - daysOld: 1
   ```

2. **Очистить все временные файлы:**
   ```http
   POST {{baseUrl}}/api/dropbox/cleanup-all
   Authorization: Bearer {{token}}
   ```

---

## 🔧 Настройка Pre-request Scripts

### Для автоматической авторизации
Добавьте в Pre-request Script:
```javascript
// Получить токен из переменной окружения
const token = pm.environment.get("token");
if (token) {
    pm.request.headers.add({
        key: "Authorization",
        value: "Bearer " + token
    });
}
```

### Для логирования запросов
```javascript
console.log("Request URL:", pm.request.url);
console.log("Request Method:", pm.request.method);
```

---

## 📊 Тестовые данные

### Поддерживаемые форматы файлов:
- **Изображения**: JPG, PNG, GIF, WebP
- **Документы**: PDF, DOC, DOCX, TXT
- **Архивы**: ZIP, RAR, 7Z

### Размеры файлов:
- Максимальный размер: 50MB (настроено в application.properties)
- Рекомендуемый размер изображений: до 5MB

---

## 🚨 Обработка ошибок

### Типичные ошибки и решения:

#### 1. Ошибка авторизации (401)
```json
{
    "error": "Unauthorized"
}
```
**Решение:** Проверьте JWT токен в заголовке Authorization

#### 2. Файл слишком большой (413)
```json
{
    "error": "File too large"
}
```
**Решение:** Уменьшите размер файла или увеличьте лимит в настройках

#### 3. Ошибка Dropbox (500)
```json
{
    "error": "Failed to upload file: Invalid access token"
}
```
**Решение:** Проверьте токен Dropbox в application.properties

#### 4. Произведение не найдено (404)
```json
{
    "error": "Work with id 1 not found"
}
```
**Решение:** Убедитесь, что произведение с указанным ID существует

---

## 📝 Примеры cURL команд

### Загрузка файла
```bash
curl -X POST "http://localhost:8080/api/dropbox/upload" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.jpg" \
  -F "subfolder=covers"
```

### Загрузка фото профиля
```bash
curl -X POST "http://localhost:8080/api/dropbox/upload-profile-photo" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "profilePhoto=@/path/to/profile.jpg"
```

### Очистка файлов
```bash
curl -X POST "http://localhost:8080/api/dropbox/cleanup" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "subfolder=covers&daysOld=7"
```

---

## 🎯 Готовые коллекции

Создайте в Postman коллекцию "Umai Dropbox API" с следующими запросами:

1. **Upload File** - POST /api/dropbox/upload
2. **Upload Profile Photo** - POST /api/dropbox/upload-profile-photo
3. **Upload Cover** - POST /api/dropbox/upload-cover/{workId}
4. **Delete Profile Photo** - DELETE /api/dropbox/delete-profile-photo
5. **Delete Cover** - DELETE /api/dropbox/delete-cover/{workId}
6. **Cleanup Folder** - POST /api/dropbox/cleanup
7. **Cleanup All** - POST /api/dropbox/cleanup-all
8. **Check File Exists** - GET /api/dropbox/file-exists

Теперь вы можете легко тестировать всю Dropbox интеграцию! 🚀

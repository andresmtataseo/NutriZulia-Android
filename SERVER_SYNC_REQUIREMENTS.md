# Requisitos del Servidor para Sincronización - NutriZulia

Este documento describe los requisitos técnicos que debe cumplir el servidor backend para soportar la sincronización bidireccional con la aplicación móvil NutriZulia-Android.

## 📋 Requisitos Generales

### Tecnología Base
- **Framework**: Cualquier framework web moderno (Spring Boot, Express.js, Django, etc.)
- **Base de Datos**: PostgreSQL, MySQL o similar con soporte para timestamps
- **Autenticación**: Sistema de autenticación JWT o similar
- **Formato de Datos**: JSON para todas las comunicaciones

### Configuración de Red
- **Protocolo**: HTTPS obligatorio para producción
- **CORS**: Configurado para permitir requests desde la aplicación móvil
- **Rate Limiting**: Implementado para prevenir abuso

## 🔄 Endpoints de Sincronización Requeridos

### 1. Endpoint PUSH - Recibir Cambios del Cliente

```http
POST /api/sync/push
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
  "pacientes": [
    {
      "id": "uuid",
      "nombres": "string",
      "apellidos": "string",
      "cedula": "string",
      "telefono": "string",
      "email": "string",
      "fechaNacimiento": "yyyy-MM-dd",
      "genero": "string",
      "direccion": "string",
      "etnia_id": "uuid",
      "municipio_id": "uuid",
      "parroquia_id": "uuid",
      "created_at": "yyyy-MM-dd'T'HH:mm:ss",
      "updated_at": "yyyy-MM-dd'T'HH:mm:ss"
    }
  ],
  "representantes": [...],
  "consultas": [...],
  "detallesAntropometricos": [...],
  "detallesVitales": [...],
  "detallesMetabolicos": [...],
  "detallesPediatricos": [...],
  "detallesObstetricias": [...],
  "evaluacionesAntropometricas": [...],
  "diagnosticos": [...],
  "pacientesRepresentantes": [...],
  "actividades": [...]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Datos sincronizados exitosamente",
  "processed_count": 150
}
```

### 2. Endpoint PULL - Enviar Cambios al Cliente

```http
GET /api/sync/pull?since={timestamp}
Authorization: Bearer {jwt_token}
```

**Query Parameters:**
- `since`: Timestamp en formato ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`)

**Response:**
```json
{
  "pacientes": [...],
  "representantes": [...],
  "consultas": [...],
  "detallesAntropometricos": [...],
  "detallesVitales": [...],
  "detallesMetabolicos": [...],
  "detallesPediatricos": [...],
  "detallesObstetricias": [...],
  "evaluacionesAntropometricas": [...],
  "diagnosticos": [...],
  "pacientesRepresentantes": [...],
  "actividades": [...]
}
```

## 🗄️ Estructura de Base de Datos

### Campos Obligatorios en Todas las Tablas

Cada tabla debe incluir estos campos para la sincronización:

```sql
-- Campos de auditoría obligatorios
id UUID PRIMARY KEY,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```

### Índices Requeridos

```sql
-- Índices para optimizar consultas de sincronización
CREATE INDEX idx_pacientes_updated_at ON pacientes(updated_at);
CREATE INDEX idx_representantes_updated_at ON representantes(updated_at);
CREATE INDEX idx_consultas_updated_at ON consultas(updated_at);
CREATE INDEX idx_detalle_antropometrico_updated_at ON detalle_antropometrico(updated_at);
CREATE INDEX idx_detalle_vital_updated_at ON detalle_vital(updated_at);
CREATE INDEX idx_detalle_metabolico_updated_at ON detalle_metabolico(updated_at);
CREATE INDEX idx_detalle_pediatrico_updated_at ON detalle_pediatrico(updated_at);
CREATE INDEX idx_detalle_obstetricia_updated_at ON detalle_obstetricia(updated_at);
CREATE INDEX idx_evaluacion_antropometrica_updated_at ON evaluacion_antropometrica(updated_at);
CREATE INDEX idx_diagnostico_updated_at ON diagnostico(updated_at);
CREATE INDEX idx_paciente_representante_updated_at ON paciente_representante(updated_at);
CREATE INDEX idx_actividad_updated_at ON actividad(updated_at);
```

## 🔐 Seguridad y Autenticación

### Autenticación JWT
- **Header**: `Authorization: Bearer {token}`
- **Validación**: El token debe ser válido y no expirado
- **Permisos**: El usuario debe tener permisos de sincronización

### Validación de Datos
- **Sanitización**: Todos los inputs deben ser sanitizados
- **Validación**: Validar tipos de datos y formatos
- **Integridad**: Verificar relaciones entre entidades

## ⚡ Optimizaciones de Rendimiento

### Consultas Eficientes
```sql
-- Ejemplo de consulta optimizada para PULL
SELECT * FROM pacientes 
WHERE updated_at > ? 
ORDER BY updated_at ASC 
LIMIT 1000;
```

### Paginación
- Implementar paginación para grandes volúmenes de datos
- Límite recomendado: 1000 registros por request

### Compresión
- Habilitar compresión GZIP para responses
- Reducir el tamaño de los payloads JSON

## 🔄 Lógica de Sincronización

### Resolución de Conflictos
1. **Last Write Wins**: El servidor siempre tiene la versión autoritativa
2. **Timestamp Comparison**: Usar `updated_at` para determinar la versión más reciente
3. **Upsert Operations**: Implementar INSERT ON CONFLICT UPDATE

### Manejo de Transacciones
```sql
-- Ejemplo de transacción para PUSH
BEGIN;
  -- Procesar todos los cambios
  INSERT INTO pacientes (...) ON CONFLICT (id) DO UPDATE SET ...;
  INSERT INTO consultas (...) ON CONFLICT (id) DO UPDATE SET ...;
  -- ... más operaciones
COMMIT;
```

## 📊 Logging y Monitoreo

### Logs Requeridos
- Timestamp de cada operación de sincronización
- Cantidad de registros procesados
- Errores y excepciones
- Tiempo de respuesta

### Métricas de Monitoreo
- Frecuencia de sincronización por usuario
- Volumen de datos sincronizados
- Errores de sincronización
- Latencia de endpoints

## 🚨 Manejo de Errores

### Códigos de Estado HTTP
- `200 OK`: Sincronización exitosa
- `400 Bad Request`: Datos inválidos
- `401 Unauthorized`: Token inválido
- `403 Forbidden`: Sin permisos
- `409 Conflict`: Conflicto de datos
- `500 Internal Server Error`: Error del servidor

### Formato de Respuesta de Error
```json
{
  "success": false,
  "error": {
    "code": "SYNC_ERROR",
    "message": "Descripción del error",
    "details": {
      "field": "campo_con_error",
      "reason": "razón_específica"
    }
  }
}
```

## 🔧 Configuración del Servidor

### Variables de Entorno
```bash
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=nutrizulia
DB_USER=nutrizulia_user
DB_PASSWORD=secure_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=24h

# Sincronización
SYNC_BATCH_SIZE=1000
SYNC_TIMEOUT=30s
```

### Dependencias Recomendadas
- **ORM**: Hibernate, Sequelize, Django ORM, etc.
- **Validación**: Bean Validation, Joi, Marshmallow, etc.
- **Logging**: Logback, Winston, Python logging, etc.
- **Monitoreo**: Prometheus, New Relic, DataDog, etc.

## 📝 Notas de Implementación

1. **Idempotencia**: Los endpoints deben ser idempotentes
2. **Atomicidad**: Usar transacciones para mantener consistencia
3. **Escalabilidad**: Diseñar para manejar múltiples clientes concurrentes
4. **Backup**: Implementar respaldos regulares de la base de datos
5. **Testing**: Crear tests unitarios e integración para los endpoints

## 🔄 Flujo de Sincronización Completo

1. **Cliente inicia sincronización**
2. **PUSH**: Cliente envía cambios locales al servidor
3. **Servidor procesa y almacena cambios**
4. **PULL**: Cliente solicita cambios del servidor
5. **Servidor envía cambios desde último timestamp**
6. **Cliente actualiza base de datos local**
7. **Cliente actualiza timestamp de última sincronización**

---

**Versión**: 1.0  
**Fecha**: Enero 2025  
**Proyecto**: NutriZulia-Android
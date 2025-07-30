# Prompt para Implementación de Sincronización en el Servidor Backend

## 🎯 Objetivo

Implementar un sistema de sincronización bidireccional completo en el servidor backend de NutriZulia que permita a la aplicación móvil Android sincronizar datos de manera eficiente y confiable.

## 📋 Contexto del Proyecto

**Aplicación**: NutriZulia - Sistema de gestión nutricional  
**Cliente**: Aplicación móvil Android desarrollada en Kotlin  
**Sincronización**: Bidireccional (PUSH/PULL) basada en timestamps  
**Arquitectura**: Cliente-servidor con base de datos centralizada  

## 🔧 Tareas a Implementar

### 1. Preparación de la Base de Datos

#### 1.1 Modificar Esquema de Tablas
Asegurar que TODAS las tablas principales tengan estos campos obligatorios:

```sql
-- Añadir a cada tabla si no existen
ALTER TABLE pacientes ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE pacientes ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Repetir para todas las tablas:
-- representantes, consultas, detalle_antropometrico, detalle_vital, 
-- detalle_metabolico, detalle_pediatrico, detalle_obstetricia,
-- evaluacion_antropometrica, diagnostico, paciente_representante, actividad
```

#### 1.2 Crear Índices de Rendimiento
```sql
-- Índices para optimizar consultas de sincronización
CREATE INDEX IF NOT EXISTS idx_pacientes_updated_at ON pacientes(updated_at);
CREATE INDEX IF NOT EXISTS idx_representantes_updated_at ON representantes(updated_at);
CREATE INDEX IF NOT EXISTS idx_consultas_updated_at ON consultas(updated_at);
-- ... continuar para todas las tablas
```

#### 1.3 Triggers de Actualización Automática
```sql
-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Aplicar a cada tabla
CREATE TRIGGER update_pacientes_updated_at 
    BEFORE UPDATE ON pacientes 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 2. Implementar Endpoints de Sincronización

#### 2.1 Endpoint PUSH - Recibir Cambios del Cliente

**Ruta**: `POST /api/sync/push`  
**Autenticación**: JWT Bearer Token requerido  

**Funcionalidades requeridas**:
- Validar autenticación JWT
- Validar estructura del payload JSON
- Procesar datos en transacción atómica
- Implementar operaciones UPSERT (INSERT ON CONFLICT UPDATE)
- Manejar relaciones entre entidades
- Retornar estadísticas de procesamiento

**Estructura del Request Body**:
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

**Lógica de Procesamiento**:
1. Validar token JWT y permisos
2. Iniciar transacción de base de datos
3. Para cada tipo de entidad:
   - Validar datos de entrada
   - Ejecutar UPSERT basado en ID
   - Mantener timestamp del servidor como autoritativo
4. Confirmar transacción
5. Retornar estadísticas

#### 2.2 Endpoint PULL - Enviar Cambios al Cliente

**Ruta**: `GET /api/sync/pull?since={timestamp}`  
**Autenticación**: JWT Bearer Token requerido  

**Parámetros**:
- `since`: Timestamp ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`)

**Funcionalidades requeridas**:
- Validar autenticación JWT
- Validar formato del timestamp
- Consultar cambios desde el timestamp proporcionado
- Implementar paginación (máximo 1000 registros por tipo)
- Ordenar por `updated_at` ASC
- Incluir todas las relaciones necesarias

**Consultas SQL de Ejemplo**:
```sql
-- Obtener pacientes modificados
SELECT * FROM pacientes 
WHERE updated_at > ? 
ORDER BY updated_at ASC 
LIMIT 1000;

-- Repetir para todas las entidades
```

### 3. Implementar Lógica de Negocio

#### 3.1 Servicio de Sincronización
Crear una clase/módulo `SyncService` con estos métodos:

```typescript
// Ejemplo en TypeScript/Node.js
class SyncService {
  async pushChanges(data: SyncPushRequest, userId: string): Promise<SyncResult>
  async pullChanges(since: Date, userId: string): Promise<SyncPullResponse>
  private async upsertPacientes(pacientes: Paciente[]): Promise<number>
  private async upsertRepresentantes(representantes: Representante[]): Promise<number>
  // ... métodos para cada entidad
}
```

#### 3.2 Validaciones de Datos
- Validar UUIDs
- Validar formatos de fecha
- Validar relaciones entre entidades (foreign keys)
- Sanitizar strings de entrada
- Validar rangos numéricos

#### 3.3 Manejo de Transacciones
```sql
-- Ejemplo de transacción completa
BEGIN;
  -- Procesar pacientes
  INSERT INTO pacientes (...) 
  ON CONFLICT (id) DO UPDATE SET 
    nombres = EXCLUDED.nombres,
    apellidos = EXCLUDED.apellidos,
    updated_at = EXCLUDED.updated_at
  WHERE pacientes.updated_at < EXCLUDED.updated_at;
  
  -- Procesar consultas
  INSERT INTO consultas (...) 
  ON CONFLICT (id) DO UPDATE SET ...;
  
  -- ... más operaciones
COMMIT;
```

### 4. Implementar Seguridad

#### 4.1 Middleware de Autenticación
- Validar JWT en header `Authorization: Bearer {token}`
- Verificar expiración del token
- Extraer información del usuario
- Validar permisos de sincronización

#### 4.2 Validación de Entrada
- Sanitizar todos los inputs
- Validar tipos de datos
- Prevenir inyección SQL
- Limitar tamaño de payloads

#### 4.3 Rate Limiting
- Implementar límites por usuario
- Máximo 10 requests de sincronización por minuto
- Bloquear IPs sospechosas

### 5. Optimizaciones de Rendimiento

#### 5.1 Consultas Optimizadas
- Usar índices en `updated_at`
- Implementar paginación eficiente
- Evitar N+1 queries
- Usar prepared statements

#### 5.2 Compresión y Caché
- Habilitar compresión GZIP
- Implementar caché para consultas frecuentes
- Optimizar serialización JSON

### 6. Logging y Monitoreo

#### 6.1 Logs Requeridos
```javascript
// Ejemplo de logs necesarios
logger.info('Sync PUSH started', { 
  userId, 
  recordCount: data.totalRecords,
  timestamp: new Date()
});

logger.info('Sync PUSH completed', { 
  userId, 
  processed: result.processedCount,
  duration: endTime - startTime
});

logger.error('Sync PUSH failed', { 
  userId, 
  error: error.message,
  stack: error.stack
});
```

#### 6.2 Métricas de Monitoreo
- Frecuencia de sincronización por usuario
- Tiempo de respuesta de endpoints
- Volumen de datos sincronizados
- Tasa de errores
- Uso de recursos del servidor

### 7. Manejo de Errores

#### 7.1 Códigos de Respuesta HTTP
- `200 OK`: Sincronización exitosa
- `400 Bad Request`: Datos inválidos o timestamp malformado
- `401 Unauthorized`: Token JWT inválido o expirado
- `403 Forbidden`: Usuario sin permisos de sincronización
- `409 Conflict`: Conflicto de datos irreconciliable
- `429 Too Many Requests`: Rate limit excedido
- `500 Internal Server Error`: Error interno del servidor

#### 7.2 Formato de Respuestas de Error
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Datos de entrada inválidos",
    "details": {
      "field": "pacientes[0].cedula",
      "reason": "Formato de cédula inválido"
    }
  }
}
```

## 📝 Entregables Esperados

1. **Endpoints funcionales** de PUSH y PULL
2. **Documentación de API** con ejemplos
3. **Scripts de migración** de base de datos
4. **Tests automatizados** con cobertura >80%
5. **Configuración de monitoreo** y alertas
6. **Manual de deployment** y configuración

## 🔍 Criterios de Aceptación

- [ ] Endpoints responden correctamente a requests válidos
- [ ] Manejo adecuado de errores y validaciones
- [ ] Transacciones atómicas funcionando
- [ ] Autenticación JWT implementada
- [ ] Logs y métricas configurados
- [ ] Tests pasando al 100%
- [ ] Documentación completa
- [ ] Rendimiento aceptable (<2s para 1000 registros)
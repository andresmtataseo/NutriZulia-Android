# Sistema de Sincronización Bidireccional - NutriZulia

## Descripción General

Este sistema implementa una sincronización bidireccional robusta y eficiente para la aplicación NutriZulia, siguiendo los principios de Clean Architecture y utilizando las mejores prácticas de Android.

## Arquitectura del Sistema

### Componentes Principales

1. **SyncRepository** - Lógica central de sincronización
2. **SyncWorker** - Ejecución en segundo plano con WorkManager
3. **ISyncService** - Interfaz Retrofit para comunicación con la API
4. **SyncManager** - Gestión de preferencias de sincronización
5. **SyncScheduler** - Programación de sincronización periódica

### Estrategia de Sincronización

El sistema utiliza la estrategia de "Última Fecha de Sincronización Exitosa" (`lastSuccessfulSyncTimestamp`):

- **Servidor como fuente de verdad**: Los timestamps del servidor son autoritativos
- **Sincronización incremental**: Solo se sincronizan los cambios desde la última sincronización exitosa
- **Operación Upsert**: Insertar si no existe, actualizar si ya existe

## Flujo de Sincronización

### Fase PUSH (Envío de cambios locales)
1. Obtener `lastSuccessfulSyncTimestamp` de DataStore
2. Consultar cambios locales con `updated_at > lastSuccessfulSyncTimestamp`
3. Enviar cambios al endpoint `/sync/push`

### Fase PULL (Recepción de cambios remotos)
1. Llamar al endpoint `/sync/pull` con el timestamp
2. Recibir lista de entidades modificadas desde el servidor
3. Aplicar operación `upsertAll` en la base de datos local

### Finalización
- Si ambas fases son exitosas, actualizar `lastSuccessfulSyncTimestamp`
- En caso de error, mantener el timestamp anterior para reintentar

## Uso del Sistema

### Inicialización Automática

La sincronización se inicializa automáticamente en `NutriZuliaApp`:

```kotlin
@HiltAndroidApp
class NutriZuliaApp: Application() {
    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        syncScheduler.schedulePeriodic() // Cada 15 minutos
    }
}
```

### Sincronización Manual

Para ejecutar una sincronización inmediata:

```kotlin
@Inject
lateinit var syncScheduler: SyncScheduler

// Sincronización inmediata
syncScheduler.syncNow()
```

### Monitoreo del Estado

```kotlin
// Observar el estado de la sincronización
syncScheduler.getSyncStatus().observe(this) { workInfos ->
    workInfos?.let { 
        // Manejar el estado de la sincronización
    }
}
```

## Configuración de WorkManager

### Restricciones
- **Red requerida**: Solo sincroniza con conexión a internet
- **Batería no baja**: Respeta el estado de la batería
- **Política de reintentos**: Backoff exponencial en caso de fallos

### Programación
- **Intervalo**: 15 minutos
- **Flexibilidad**: 5 minutos de ventana flexible
- **Política**: KEEP (mantiene la programación existente)

## Entidades Sincronizables

Las siguientes entidades incluyen campos de sincronización:

- `PacienteEntity`
- `ConsultaEntity`
- `RepresentanteEntity`
- `EvaluacionAntropometricaEntity`
- `DetallePediatricoEntity`
- `DetalleMetabolicoEntity`
- `DetalleVitalEntity`
- `DetalleAntropometricoEntity`
- `DetalleObstetriciaEntity`

### Campos Requeridos
- `updated_at: LocalDateTime` - Timestamp de última modificación
- `is_deleted: Boolean` - Marca de eliminación lógica

## API Endpoints

### POST /sync/push
Envía cambios locales al servidor.

**Request:**
```json
{
  "pacientes": [...],
  "consultas": [...],
  "representantes": [...]
}
```

### GET /sync/pull?since={timestamp}
Obtiene cambios del servidor desde un timestamp.

**Response:**
```json
{
  "pacientes": [...],
  "consultas": [...],
  "representantes": [...],
  "serverTimestamp": "2024-01-15T10:30:00"
}
```

## Manejo de Errores

### Tipos de Error
- **Red**: Fallos de conectividad
- **Servidor**: Errores HTTP (4xx, 5xx)
- **Base de datos**: Errores de Room
- **Serialización**: Errores de Gson

### Estrategia de Recuperación
- **Reintentos automáticos**: WorkManager maneja los reintentos
- **Backoff exponencial**: Incrementa el tiempo entre reintentos
- **Preservación del estado**: El timestamp no se actualiza en caso de error

## Consideraciones de Rendimiento

### Optimizaciones
- **Consultas incrementales**: Solo cambios desde la última sincronización
- **Operaciones en lotes**: `upsertAll` para múltiples entidades
- **Contexto IO**: Operaciones de red y BD en hilo apropiado
- **Restricciones de WorkManager**: Respeta el estado del sistema

### Limitaciones
- **Tamaño de lote**: Considerar limitar el número de entidades por sincronización
- **Timeout**: Configurar timeouts apropiados para operaciones de red
- **Memoria**: Monitorear el uso de memoria con grandes conjuntos de datos

## Extensibilidad

### Agregar Nueva Entidad
1. Agregar campos `updated_at` e `is_deleted` a la entidad
2. Implementar `findPendingChanges` y `upsertAll` en el DAO
3. Agregar la entidad a `SyncDtos.kt`
4. Actualizar `SyncRepository` para incluir la nueva entidad

### Personalizar Estrategia
El sistema está diseñado para ser extensible. Se pueden implementar diferentes estrategias de sincronización heredando de interfaces base o modificando la lógica en `SyncRepository`.

## Seguridad

### Autenticación
- Utiliza `@AuthenticatedRetrofit` para incluir tokens de autenticación
- Los endpoints de sincronización requieren autenticación válida

### Validación
- Validación de datos en el servidor antes de aplicar cambios
- Verificación de permisos por usuario/institución

## Testing

### Pruebas Unitarias
- Mockear `ISyncService` para pruebas de `SyncRepository`
- Usar `TestCoroutineDispatcher` para pruebas de corrutinas
- Verificar la lógica de actualización de timestamps

### Pruebas de Integración
- Probar el flujo completo de sincronización
- Verificar el comportamiento con datos reales
- Validar el manejo de errores de red

## Monitoreo y Logs

### Logging
- Logs detallados en cada fase de sincronización
- Métricas de rendimiento (tiempo, cantidad de datos)
- Registro de errores para debugging

### Métricas
- Frecuencia de sincronización exitosa
- Tiempo promedio de sincronización
- Tasa de errores por tipo
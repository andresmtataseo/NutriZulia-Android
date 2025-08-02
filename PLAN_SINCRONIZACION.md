# Plan de Sincronización de Datos - NutriZulia Android

## 1. Objetivo

Implementar sincronización manual de `PacienteEntity` entre la aplicación Android y el servidor Spring Boot. **El registro con timestamp más reciente prevalece** (Last Write Wins).

### Características:
- Sincronización activada por el usuario
- Resolución automática de conflictos por timestamp
- Manejo simple de errores
- Integración con la arquitectura existente del proyecto

## 2. Arquitectura Simplificada

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│                 │    │                 │    │                 │
│ - PacienteVM    │◄──►│ - SyncUseCase   │◄──►│ - PacienteRepo  │
│ - PacienteList  │    │                 │    │ - SyncApiService│
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                               ┌───────▼───────┐
                                               │  Existing     │
                                               │               │
                                               │ - Room DB     │
                                               │ - Retrofit    │
                                               └───────────────┘
```

### Flujo Simple:
1. **Usuario presiona "Sincronizar"**
2. **Push**: Enviar pacientes modificados localmente
3. **Pull**: Obtener pacientes del servidor
4. **Merge**: Aplicar regla "timestamp más reciente gana"
5. **Actualizar UI**

## 3. Modificaciones a PacienteEntity

### 3.1 Campos Adicionales Requeridos

Agregar solo estos campos esenciales a `PacienteEntity`:

```kotlin
@ColumnInfo(name = "needs_sync") val needsSync: Boolean = false,
@ColumnInfo(name = "last_sync_at") val lastSyncAt: LocalDateTime? = null
```

### 3.2 Estados Simples

```kotlin
// En PacienteEntity.kt
fun markForSync(): PacienteEntity = this.copy(
    needsSync = true,
    updatedAt = LocalDateTime.now()
)

fun markAsSynced(): PacienteEntity = this.copy(
    needsSync = false,
    lastSyncAt = LocalDateTime.now()
)
```

## 4. APIs del Servidor Spring Boot

### 4.1 URL Base
```
https://api.nutrizulia.com/api/v1/pacientes
```

### 4.2 Endpoint 1: Sincronizar Pacientes (Push + Pull)
```http
POST /api/v1/pacientes/sync
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "usuarioInstitucionId": 123,
  "lastSyncTimestamp": "2024-01-15T10:30:00",
  "localPacientes": [
    {
      "id": "pac_001",
      "usuarioInstitucionId": 123,
      "cedula": "12345678",
      "nombres": "Juan Carlos",
      "apellidos": "Pérez González",
      "fechaNacimiento": "1990-05-15",
      "genero": "M",
      "etniaId": 1,
      "nacionalidadId": 1,
      "parroquiaId": 1,
      "domicilio": "Av. Principal #123",
      "telefono": "04141234567",
      "correo": "juan@email.com",
      "updatedAt": "2024-01-15T14:30:00",
      "isDeleted": false
    }
  ]
}
```

**Response Body (200 OK):**
```json
{
  "success": true,
  "message": "Sincronización completada",
  "serverTimestamp": "2024-01-15T14:35:00",
  "stats": {
    "pushedCount": 1,
    "pulledCount": 2
  },
  "serverPacientes": [
    {
      "id": "pac_002",
      "usuarioInstitucionId": 123,
      "cedula": "87654321",
      "nombres": "María Elena",
      "apellidos": "García López",
      "fechaNacimiento": "1985-08-20",
      "genero": "F",
      "etniaId": 1,
      "nacionalidadId": 1,
      "parroquiaId": 2,
      "domicilio": "Calle 5 #456",
      "telefono": "04167891234",
      "correo": "maria@email.com",
      "updatedAt": "2024-01-15T13:45:00",
      "isDeleted": false
    },
    {
      "id": "pac_001",
      "usuarioInstitucionId": 123,
      "cedula": "12345678",
      "nombres": "Juan Carlos",
      "apellidos": "Pérez González",
      "fechaNacimiento": "1990-05-15",
      "genero": "M",
      "etniaId": 1,
      "nacionalidadId": 1,
      "parroquiaId": 1,
      "domicilio": "Av. Principal #123 - Actualizada",
      "telefono": "04141234567",
      "correo": "juan.nuevo@email.com",
      "updatedAt": "2024-01-15T14:32:00",
      "isDeleted": false
    }
  ]
}
```

**Response Body (Error 400/500):**
```json
{
  "success": false,
  "message": "Error en la sincronización",
  "error": {
    "code": "SYNC_ERROR",
    "details": "Descripción específica del error"
  }
}
```

## 5. Implementación Android

### 5.1 Archivos a Crear/Modificar

```
app/src/main/java/com/nutrizulia/
├── data/
│   ├── local/
│   │   ├── entity/collection/
│   │   │   └── PacienteEntity.kt (MODIFICAR)
│   │   └── dao/
│   │       └── PacienteDao.kt (MODIFICAR)
│   ├── remote/
│   │   ├── api/
│   │   │   └── PacienteSyncApiService.kt (CREAR)
│   │   └── dto/
│   │       ├── SyncRequest.kt (CREAR)
│   │       └── SyncResponse.kt (CREAR)
│   └── repository/
│       └── collection/
│           └── PacienteRepository.kt (MODIFICAR)
├── domain/
│   └── usecase/
│       └── collection/
│           └── SyncPacientesUseCase.kt (CREAR)
└── presentation/
    ├── viewmodel/
    │   └── paciente/
    │       └── PacienteViewModel.kt (MODIFICAR)
    └── view/
        └── paciente/
            └── PacienteListFragment.kt (MODIFICAR)
```

### 5.2 DTOs de Sincronización

```kotlin
// SyncRequest.kt
data class SyncRequest(
    val usuarioInstitucionId: Int,
    val lastSyncTimestamp: String?, // ISO 8601 format
    val localPacientes: List<PacienteSyncDto>
)

// SyncResponse.kt
data class SyncResponse(
    val success: Boolean,
    val message: String,
    val serverTimestamp: String,
    val stats: SyncStats?,
    val serverPacientes: List<PacienteSyncDto>?,
    val error: SyncError?
)

data class SyncStats(
    val pushedCount: Int,
    val pulledCount: Int
)

data class SyncError(
    val code: String,
    val details: String
)

// PacienteSyncDto.kt
data class PacienteSyncDto(
    val id: String,
    val usuarioInstitucionId: Int,
    val cedula: String,
    val nombres: String,
    val apellidos: String,
    val fechaNacimiento: String, // "YYYY-MM-DD"
    val genero: String,
    val etniaId: Int,
    val nacionalidadId: Int,
    val parroquiaId: Int,
    val domicilio: String,
    val telefono: String?,
    val correo: String?,
    val updatedAt: String, // ISO 8601 format
    val isDeleted: Boolean
)
```

### 5.3 API Service

```kotlin
// PacienteSyncApiService.kt
interface PacienteSyncApiService {
    @POST("pacientes/sync")
    suspend fun syncPacientes(
        @Body request: SyncRequest
    ): Response<SyncResponse>
}
```

### 5.4 Lógica de Sincronización

```kotlin
// SyncPacientesUseCase.kt
class SyncPacientesUseCase(
    private val pacienteRepository: PacienteRepository,
    private val sessionManager: SessionManager
) {
    suspend fun execute(): Flow<SyncResult> = flow {
        emit(SyncResult.Loading("Iniciando sincronización..."))
        
        try {
            // 1. Obtener pacientes que necesitan sincronización
            val localPacientes = pacienteRepository.getPacientesNeedingSync()
            
            // 2. Obtener último timestamp de sincronización
            val lastSync = pacienteRepository.getLastSyncTimestamp()
            
            // 3. Llamar API de sincronización
            val response = pacienteRepository.syncWithServer(localPacientes, lastSync)
            
            if (response.success) {
                // 4. Procesar respuesta del servidor
                response.serverPacientes?.let { serverPacientes ->
                    pacienteRepository.mergeServerPacientes(serverPacientes)
                }
                
                // 5. Marcar pacientes locales como sincronizados
                pacienteRepository.markPacientesAsSynced(localPacientes.map { it.id })
                
                // 6. Guardar timestamp de sincronización
                pacienteRepository.saveLastSyncTimestamp(response.serverTimestamp)
                
                emit(SyncResult.Success(
                    pushedCount = response.stats?.pushedCount ?: 0,
                    pulledCount = response.stats?.pulledCount ?: 0,
                    message = response.message
                ))
            } else {
                emit(SyncResult.Error(
                    exception = Exception(response.error?.details ?: "Error desconocido"),
                    message = response.message
                ))
            }
        } catch (e: Exception) {
            emit(SyncResult.Error(e, "Error de conexión"))
        }
    }
}
```

## 6. Regla de Resolución de Conflictos

### 6.1 Algoritmo Simple: Last Write Wins

```kotlin
// En PacienteRepository.kt
private suspend fun mergeServerPacientes(serverPacientes: List<PacienteSyncDto>) {
    serverPacientes.forEach { serverPaciente ->
        val localPaciente = pacienteDao.getPacienteById(serverPaciente.id)
        
        if (localPaciente == null) {
            // Nuevo paciente del servidor
            pacienteDao.insert(serverPaciente.toEntity())
        } else {
            // Comparar timestamps y mantener el más reciente
            val serverTimestamp = LocalDateTime.parse(serverPaciente.updatedAt)
            val localTimestamp = localPaciente.updatedAt
            
            if (serverTimestamp.isAfter(localTimestamp)) {
                // Servidor tiene versión más reciente
                pacienteDao.update(serverPaciente.toEntity())
            }
            // Si local es más reciente, no hacer nada (ya se envió al servidor)
        }
    }
}
```

## 7. UI - Integración con PacienteListFragment

### 7.1 Botón de Sincronización

```xml
<!-- En el layout de PacienteListFragment -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fabSync"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="16dp"
    android:src="@drawable/ic_sync"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

### 7.2 Estados en ViewModel

```kotlin
// En PacienteViewModel.kt
data class PacienteUiState(
    val pacientes: List<Paciente> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val error: String? = null
)

// Función de sincronización
fun syncPacientes() {
    viewModelScope.launch {
        syncPacientesUseCase.execute().collect { result ->
            when (result) {
                is SyncResult.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = true,
                        syncMessage = result.message
                    )
                }
                is SyncResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        syncMessage = "Sincronizado: ${result.pushedCount} enviados, ${result.pulledCount} recibidos"
                    )
                    loadPacientes() // Recargar lista
                }
                is SyncResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        error = result.message
                    )
                }
            }
        }
    }
}
```

## 8. Manejo de Errores

### 8.1 Tipos de Error

```kotlin
sealed class SyncError : Exception() {
    object NetworkError : SyncError()
    object AuthenticationError : SyncError()
    data class ServerError(val code: String, val details: String) : SyncError()
    data class ValidationError(val message: String) : SyncError()
}
```

### 8.2 Mensajes de Usuario

```kotlin
fun getSyncErrorMessage(error: SyncError): String = when (error) {
    is SyncError.NetworkError -> "Sin conexión a internet"
    is SyncError.AuthenticationError -> "Sesión expirada, inicia sesión nuevamente"
    is SyncError.ServerError -> "Error del servidor: ${error.details}"
    is SyncError.ValidationError -> error.message
}
```

## 9. Implementación por Fases

### Fase 1: Infraestructura (3-4 días)
- [ ] Modificar `PacienteEntity` con campos de sincronización
- [ ] Crear DTOs (`SyncRequest`, `SyncResponse`, `PacienteSyncDto`)
- [ ] Crear `PacienteSyncApiService`
- [ ] Modificar `PacienteDao` con métodos de sincronización

### Fase 2: Lógica de Negocio (3-4 días)
- [ ] Implementar `SyncPacientesUseCase`
- [ ] Modificar `PacienteRepository` con métodos de sincronización
- [ ] Implementar algoritmo "Last Write Wins"
- [ ] Agregar manejo de errores

### Fase 3: UI (2-3 días)
- [ ] Modificar `PacienteViewModel` con funcionalidad de sync
- [ ] Agregar botón de sincronización en `PacienteListFragment`
- [ ] Implementar indicadores de progreso
- [ ] Agregar mensajes de estado y error

### Fase 4: Testing y Refinamiento (2-3 días)
- [ ] Testing de la lógica de sincronización
- [ ] Testing de UI
- [ ] Manejo de casos edge
- [ ] Optimizaciones de UX

## 10. Consideraciones Técnicas

### 10.1 Dependencias Adicionales
```kotlin
// En build.gradle.kts (app)
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("androidx.work:work-runtime-ktx:2.9.0") // Para sync en background futuro
```

### 10.2 Permisos
```xml
<!-- En AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## Resumen de APIs Esperadas del Servidor

| Endpoint | Método | Propósito |
|----------|--------|-----------|
| `/api/v1/pacientes/sync` | POST | Sincronización bidireccional |

**Request esperado:** JSON con pacientes locales y timestamp de última sincronización
**Response esperado:** JSON con pacientes del servidor y estadísticas de sincronización

¿Te parece más claro y enfocado este plan simplificado?
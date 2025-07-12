# NutriZulia

## 📱 Descripción

**NutriZulia** es una aplicación móvil Android desarrollada para la **Coordinación Regional de Nutrición y Dietética** y la **Secretaría de Salud del Estado Zulia**. Esta herramienta digital facilita la gestión integral de pacientes y consultas nutricionales, permitiendo a los profesionales de la salud realizar un seguimiento completo del estado nutricional de sus pacientes.

## 🎯 Características Principales

### 🔐 Autenticación y Gestión de Usuarios
- Sistema de login seguro con cédula y contraseña
- Gestión de perfiles institucionales
- Selección de institución de trabajo
- Cierre de sesión y salida segura

### 👥 Gestión de Pacientes
- **Registro completo de pacientes** con información personal detallada
- **Búsqueda y filtrado** de pacientes por nombre o cédula
- **Historia clínica** con datos antropométricos y metabólicos
- **Información demográfica** (estado, municipio, parroquia, etnia, nacionalidad)
- **Datos de contacto** (teléfono, correo electrónico)

### 🏥 Gestión de Consultas
- **Programación de citas** con fecha, hora y especialidad
- **Registro de consultas** con motivo y observaciones
- **Estados de consulta** (programada, realizada, cancelada)

### 📊 Evaluación Nutricional
- **Mediciones antropométricas**:
  - Peso, altura, talla
  - Circunferencias (braquial, cadera, cintura)
  - Perímetro cefálico
  - Pliegues cutáneos (tricipital, subescapular)
- **Evaluación metabólica**:
  - Glicemia (basal, postprandial, aleatoria)
  - Hemoglobina glicosilada
  - Perfil lipídico (triglicéridos, colesterol total, HDL, LDL)
- **Signos vitales**:
  - Tensión arterial (sistólica y diastólica)
  - Frecuencia cardíaca y respiratoria
  - Temperatura y saturación de oxígeno
  - Pulso

### 🩺 Diagnósticos y Evaluaciones
- **Diagnósticos principales y secundarios**
- **Evaluación antropométrica** con indicadores nutricionales
- **Reglas de interpretación** para Z-Score
- **Detalles especializados**:
  - Pediatría (lactancia, uso de biberón)
  - Obstetricia (embarazo, semanas de gestación)

### 📈 Reportes y Seguimiento
- **Historial de consultas** por paciente
- **Reportes de actividades** nutricionales
- **Seguimiento temporal** de indicadores

## 🏗️ Arquitectura del Proyecto

El proyecto sigue una **arquitectura limpia (Clean Architecture)** con separación clara de responsabilidades:

```
app/src/main/java/com/nutrizulia/
├── data/                    # Capa de datos
│   ├── local/              # Base de datos local (Room)
│   │   ├── entity/         # Entidades de base de datos
│   │   ├── dao/            # Data Access Objects
│   │   └── converter/      # Convertidores para Room
│   ├── remote/             # API remota
│   │   ├── api/            # Servicios de API
│   │   └── dto/            # Data Transfer Objects
│   └── repository/         # Repositorios
├── domain/                 # Capa de dominio
│   ├── model/              # Modelos de dominio
│   ├── usecase/            # Casos de uso
│   └── exception/          # Excepciones de dominio
├── presentation/           # Capa de presentación
│   ├── view/              # Activities y Fragments
│   ├── viewmodel/         # ViewModels
│   └── adapter/           # Adaptadores para RecyclerViews
├── di/                    # Inyección de dependencias (Hilt)
└── util/                  # Utilidades y helpers
```

## 🛠️ Tecnologías Utilizadas

### Core Android
- **Kotlin** - Lenguaje de programación principal
- **Android SDK** - API 35 (Android 15)
- **ViewBinding** - Binding de vistas
- **Navigation Component** - Navegación entre pantallas

### Arquitectura y Patrones
- **MVVM (Model-View-ViewModel)** - Patrón de arquitectura
- **Clean Architecture** - Separación de capas
- **Repository Pattern** - Acceso a datos
- **Use Case Pattern** - Lógica de negocio

### Base de Datos
- **Room Database** - Base de datos local
- **SQLite** - Motor de base de datos
- **Type Converters** - Conversión de tipos complejos

### Networking
- **Retrofit** - Cliente HTTP
- **OkHttp** - Interceptor de logging
- **Gson** - Serialización JSON

### Inyección de Dependencias
- **Hilt** - Inyección de dependencias
- **KSP** - Procesamiento de anotaciones

### UI/UX
- **Material Design** - Componentes de UI
- **SwipeRefreshLayout** - Pull-to-refresh
- **Lottie** - Animaciones
- **Speed Dial** - Botón flotante de acciones

### Seguridad
- **Security Crypto** - Encriptación de datos sensibles
- **DataStore** - Almacenamiento seguro de preferencias
- **JWT** - Autenticación con tokens

## 📋 Requisitos del Sistema

- **Android API Level**: 26+ (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Java Version**: 11
- **Kotlin Version**: 2.0.21

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Arctic Fox o superior
- JDK 11
- Gradle 8.10.1

## 📱 Uso de la Aplicación

### 1. Inicio de Sesión
- Ingresar cédula y contraseña
- Seleccionar institución de trabajo
- Acceder al dashboard principal

### 2. Gestión de Pacientes
- **Registrar nuevo paciente**: Completar formulario con datos personales
- **Buscar pacientes**: Usar barra de búsqueda
- **Ver historial**: Acceder a información completa del paciente

### 3. Programación de Consultas
- **Agendar cita**: Seleccionar fecha, hora y especialidad
- **Registrar consulta**: Capturar motivo y observaciones
- **Gestionar estados**: Programada, realizada, cancelada

### 4. Evaluación Nutricional
- **Mediciones**: Capturar datos antropométricos
- **Signos vitales**: Registrar valores clínicos
- **Evaluación metabólica**: Datos de laboratorio
- **Diagnósticos**: Establecer diagnósticos principales y secundarios

## 📊 Base de Datos

La aplicación utiliza **Room Database** con las siguientes entidades principales:

### Entidades de Usuario
- `Usuario` - Información básica del usuario
- `UsuarioInstitucion` - Relación usuario-institución
- `Institucion` - Instituciones de salud
- `Rol` - Roles de usuario

### Entidades de Pacientes
- `Paciente` - Información personal del paciente
- `Representante` - Representantes legales
- `PacienteRepresentante` - Relación paciente-representante

### Entidades de Consultas
- `Consulta` - Información de la consulta
- `DetalleAntropometrico` - Mediciones antropométricas
- `DetalleMetabolico` - Datos metabólicos
- `DetalleVital` - Signos vitales
- `Diagnostico` - Diagnósticos médicos

### Entidades de Catálogos
- `Estado`, `Municipio`, `Parroquia` - Ubicación geográfica
- `Etnia`, `Nacionalidad` - Información demográfica
- `Enfermedad` - Catálogo de enfermedades
- `Especialidad` - Especialidades médicas

## 🔐 Seguridad

- **Autenticación JWT**: Tokens seguros para autenticación
- **Encriptación local**: Datos sensibles encriptados
- **Validación de entrada**: Verificación de datos de entrada
- **Manejo de errores**: Gestión segura de excepciones

## 📈 Funcionalidades Futuras

- [ ] Sincronización offline/online
- [ ] Reportes avanzados con gráficos
- [ ] Notificaciones push para citas
- [ ] Exportación de datos
- [ ] Modo offline completo

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto es desarrollado para la **Coordinación Regional de Nutrición y Dietética** del Estado Zulia. Todos los derechos reservados.

---

**Versión**: 1.0.0  
**Desarrollado para**: Secretaría de Salud del Estado Zulia  
**Coordinación Regional de Nutrición y Dietética** 
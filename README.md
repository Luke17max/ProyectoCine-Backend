# 🎬 Cine-MS — Sistema de Gestión de Cines con Microservicios

Sistema modular de gestión de cines desarrollado con **Spring Boot**, **Spring Cloud (Eureka & Gateway)**, **MySQL** y **Maven Multi-Módulo**. Todo el tráfico externo se centraliza a través de un **API Gateway**.

---

## 🔗 Descargas y Recursos del Proyecto

> **Accede aquí antes de comenzar la instalación.**

| Recurso | Enlace |
| :--- | :--- |
| 📦 **Versión Nativa** (.jar + script `.bat`) | [Descargar desde Drive](https://drive.google.com/drive/folders/19zxRYSyvEEUzyS052_lrjyQNPYHC66lp?usp=drive_link) |
| 🐳 **Versión Docker** (`docker-compose.yml`) | [Descargar desde Drive](https://drive.google.com/drive/folders/1KpTxfz00Ya8yVmxKliQrc1z8RDxyKMga?usp=drive_link) |
| 🎥 **Video de Defensa** | [Ver en Drive](https://drive.google.com/REEMPLAZAR_ENLACE_VIDEO) |

> 📝 El archivo de subtítulos del video se encuentra en [`docs/subtitulos-video.txt`](./docs/subtitulos-video.txt)

---

## 👥 Integrantes

| Nombre | Rol |
| :--- | :--- |
| Pedro García | Desarrollador Backend |
| Lucas Guerra | Desarrollador Backend |

---

## 📋 Funcionalidades Implementadas

| Módulo | Descripción |
| :--- | :--- |
| 🎞️ **Gestión de Cartelera** | Administración de películas, sucursales, salas y programación de funciones |
| 🎫 **Sistema de Reservas** | Selección y reserva de asientos para funciones específicas |
| 🍿 **Módulo de Confitería** | Gestión de inventario y compra de productos (combos, bebidas) |
| 💳 **Pasarela de Pagos Distribuida** | Procesamiento independiente para boletos (`ms-pago`) y dulcería (`ms-pago_confiteria`) |
| 🔔 **Notificaciones** | Generación de alertas para confirmar transacciones a los usuarios |
| 🌐 **Enrutamiento y Descubrimiento** | Registro automático en Eureka y enrutamiento inteligente por API Gateway |

---

## 🗺️ Mapa de Puertos y Rutas

| Servicio | Puerto | Ruta Base (vía Gateway) |
| :--- | :---: | :--- |
| **api-gateway** | `8080` | `http://localhost:8080/` |
| **eureka-server** | `8761` | `http://localhost:8761` |
| **ms-peliculas** | `8081` | `/api/peliculas/**` |
| **ms-usuarios** | `8082` | `/api/usuarios/**` |
| **ms-sucursales** | `8083` | `/api/sucursales/**` |
| **ms-salas** | `8084` | `/api/salas/**` |
| **ms-funciones** | `8085` | `/api/funciones/**` |
| **ms-confiteria** | `8086` | `/api/confiteria/**` |
| **ms-pago** | `8087` | `/api/pagos/**` |
| **ms-pago_confiteria** | `8088` | `/api/pagos-confiteria/**` |
| **ms-reservas** | `8089` | `/api/reservas/**` |
| **ms-notificaciones** | `8090` | `/api/notificaciones/**` |

---

## ⚙️ Puesta en Marcha — Versión Nativa

### Prerrequisitos

- ☕ Java 21+
- 🐬 XAMPP con **Apache** y **MySQL** activos (puerto `3306`)
- 📦 Maven 3.9+ (solo si deseas compilar desde fuente)

### Paso 1 — Base de Datos

1. Inicia **XAMPP** y activa los módulos **Apache** y **MySQL**.
2. Abre **phpMyAdmin** en `http://localhost/phpmyadmin`.
3. Importa el script unificado de bases de datos:

```
docs/script-bd.sql
```

### Paso 2 — Ejecución Automática con Script `.bat`

Descarga la **Versión Nativa** desde el enlace de arriba, descomprime el `.zip` y ejecuta:

```bat
start-all.bat
```

El script levanta los servicios en el **orden jerárquico obligatorio**:

```
1. eureka-server    →  Puerto 8761  (Registro y descubrimiento)
2. ms-peliculas     →  Puerto 8081
   ms-usuarios      →  Puerto 8082
   ms-sucursales    →  Puerto 8083
   ms-salas         →  Puerto 8084
   ms-funciones     →  Puerto 8085
   ms-confiteria    →  Puerto 8086
   ms-pago          →  Puerto 8087
   ms-pago_confiteria → Puerto 8088
   ms-reservas      →  Puerto 8089
   ms-notificaciones → Puerto 8090
3. api-gateway      →  Puerto 8080  (Último en arrancar)
```

> ⚠️ **Importante:** Espera a que Eureka esté completamente levantado antes de iniciar los microservicios. El API Gateway debe arrancar siempre al final.

### Paso 3 — Verificación

- Panel Eureka: `http://localhost:8761` — todos los microservicios deben aparecer como **UP**.
- Prueba de endpoint vía Gateway con Postman: `http://localhost:8080/api/peliculas`

---

## 🐳 Puesta en Marcha — Versión Docker

### Prerrequisitos

- 🐳 Docker Desktop instalado y en ejecución

### Ejecución

Descarga la **Versión Docker** desde el enlace de arriba, descomprime el `.zip` y ejecuta en la carpeta raíz:

```bash
docker-compose up -d
```

Para detener todos los contenedores:

```bash
docker-compose down
```

> Docker Compose levanta automáticamente todos los servicios en el orden correcto incluyendo la base de datos MySQL.

---

## 🧪 Pruebas Unitarias

El proyecto incluye una suite de pruebas unitarias con **JUnit 5** y **Mockito** para las capas de **Controller** y **Service** de los microservicios principales.

### Ejecutar todas las pruebas

Desde la raíz del proyecto (`cine-ms-parent`):

```bash
mvn clean install
```

### Ejecutar pruebas de un microservicio específico

```bash
# Ejemplo: solo ms-reservas
mvn -pl ms-reservas test

# Ejemplo: clase específica
mvn -pl ms-usuarios -Dtest=UsuarioServiceTest test
```

### Clases de prueba implementadas

| Microservicio | Clase de Test | Capa | Tests |
| :--- | :--- | :--- | :---: |
| `ms-confiteria` | `ConfiteriaControllerTest` | Controller | 8 |
| `ms-confiteria` | `ConfiteriaServiceTest` | Service | 11 |
| `ms-funciones` | `FuncionControllerTest` | Controller | 6 |
| `ms-funciones` | `FuncionServiceTest` | Service | 11 |
| `ms-notificaciones` | `NotificacionControllerTest` | Controller | 6 |
| `ms-notificaciones` | `NotificacionServiceTest` | Service | 9 |
| `ms-pago` | `PagoControllerTest` | Controller | 8 |
| `ms-pago` | `PagoServiceTest` | Service | 12 |
| `ms-pago_confiteria` | `PagoConfiteriaControllerTest` | Controller | 7 |
| `ms-pago_confiteria` | `PagoConfiteriaServiceTest` | Service | 11 |
| `ms-peliculas` | `PeliculaControllerTest` | Controller | 5 |
| `ms-peliculas` | `PeliculaServiceTest` | Service | 5 |
| `ms-reservas` | `ReservaControllerTest` | Controller | 6 |
| `ms-reservas` | `ReservaServiceImplTest` | Service | 9 |
| `ms-salas` | `SalaControllerTest` | Controller | 6 |
| `ms-salas` | `SalaServiceTest` | Service | 6 |
| `ms-sucursales` | `SucursalControllerTest` | Controller | 6 |
| `ms-sucursales` | `SucursalServiceTest` | Service | 6 |
| `ms-usuarios` | `UsuarioControllerTest` | Controller | 5 |
| `ms-usuarios` | `UsuarioServiceTest` | Service | 5 |
| **TOTAL** | **20 clases de test** | — | **152** |

### Reportes de pruebas

Los resultados se generan automáticamente en:

```
{microservicio}/target/surefire-reports/
```

---

## 📄 Documentación de Endpoints (Swagger / OpenAPI)

Cada microservicio expone su documentación interactiva de endpoints en:

```
http://localhost:{puerto}/swagger-ui/index.html
```

### Accesos directos

| Microservicio | URL de Swagger |
| :--- | :--- |
| ms-peliculas | http://localhost:8081/swagger-ui/index.html |
| ms-usuarios | http://localhost:8082/swagger-ui/index.html |
| ms-sucursales | http://localhost:8083/swagger-ui/index.html |
| ms-salas | http://localhost:8084/swagger-ui/index.html |
| ms-funciones | http://localhost:8085/swagger-ui/index.html |
| ms-confiteria | http://localhost:8086/swagger-ui/index.html |
| ms-pago | http://localhost:8087/swagger-ui/index.html |
| ms-pago_confiteria | http://localhost:8088/swagger-ui/index.html |
| ms-reservas | http://localhost:8089/swagger-ui/index.html |
| ms-notificaciones | http://localhost:8090/swagger-ui/index.html |

---

## 🎥 Video de Defensa

| Campo | Detalle |
| :--- | :--- |
| ⏱️ **Duración** | ~15 minutos (máximo 18 minutos) |
| 🔗 **Enlace** | [Ver Video en Drive](https://drive.google.com/REEMPLAZAR_ENLACE_VIDEO) |
| 📝 **Subtítulos** | Archivo [`docs/subtitulos-video.txt`](./docs/subtitulos-video.txt) |

---

## 🗂️ Estructura del Proyecto

```
cine-ms-parent/
├── api-gateway/             # Puerta de entrada (puerto 8080)
├── eureka-server/           # Servidor de descubrimiento (puerto 8761)
├── ms-peliculas/            # Gestión de películas
├── ms-usuarios/             # Gestión de usuarios
├── ms-sucursales/           # Gestión de sucursales
├── ms-salas/                # Gestión de salas
├── ms-funciones/            # Programación de funciones
├── ms-confiteria/           # Inventario de confitería
├── ms-pago/                 # Pago de boletos
├── ms-pago_confiteria/      # Pago de confitería
├── ms-reservas/             # Reservas de asientos
├── ms-notificaciones/       # Notificaciones al usuario
├── docs/
│   ├── script-bd.sql        # Script unificado de bases de datos
│   └── subtitulos-video.txt # Subtítulos del video de defensa
└── pom.xml                  # POM padre (Maven Multi-Módulo)
```

---

> **Desarrollado para la asignatura Desarrollo Fullstack I** — Evaluación Parcial 3

# Proyecto Cine-MS (Microservicios)

Sistema modular de gestión de cines desarrollado con **Spring Boot**, **Spring Cloud (Eureka & Gateway)**, **MySQL** y **Maven**. Todo el tráfico se centraliza a través de un API Gateway.

---

##  Integrantes
* [Pedro García] 
* [Lucas Guerra] 


---

##  Funcionalidades Implementadas
* **Gestión de Cartelera:** Administración de películas, sucursales, salas y programación de funciones.
* **Sistema de Reservas:** Selección para una función específica.
* **Módulo de Confitería:** Gestión de inventario y compra de productos (combos, bebidas).
* **Pasarela de Pagos Distribuida:** Procesamiento independiente para la compra de boletos (`ms-pago`) y productos de dulcería (`ms-pago_confiteria`).
* **Notificaciones:** Generación de alertas para confirmar transacciones a los usuarios.
* **Enrutamiento y Descubrimiento:** Registro automático de microservicios en Eureka y balanceo de carga mediante API Gateway.



##  Mapa de Puertos y Rutas

| Servicio | Puerto | Ruta Base (Gateway) |
| :--- | :---: | :--- |
| **api-gateway** (Puerta de Entrada) | `8080` | `/` |
| **eureka-server** (Descubrimiento) | `8761` | `http://localhost:8761` |
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



##  Pasos para Ejecutar

Ejecuta los módulos en este orden estricto abriendo una terminal en la carpeta de cada uno:

### 1. Ejecutar XAMPP (Activar módulos Apache y MySql (port 3306))

--dentro de VSCODE ejecutar microservicios
### 2. Servidor Eureka (Primero)

### 3. Ejecutar los microservicios que necesites 

### 4. Ejecutar API-Gateway (8080) para probar en postman

### 5. Abrir Postman y con esta URL http://localhost:8080 realizar peticiones

Link Video Presentacion:
https://drive.google.com/drive/folders/1jpv1js-bmRp0hrfpto8l6gctJps0hvoq?usp=drive_link


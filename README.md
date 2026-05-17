# Community Budget

## Descripcion general

**Community Budget** es un proyecto orientado a facilitar la gestion de gastos compartidos entre amigos, compañeros de piso, parejas o cualquier grupo de personas que necesite organizar cuentas en comun de forma sencilla y practica.

La aplicacion busca ofrecer una solucion util para registrar gastos, dividir importes y llevar un mejor control de los pagos pendientes dentro de un grupo.

## Objetivo del proyecto

El principal objetivo de **Community Budget** es ayudar a los usuarios a administrar gastos compartidos de manera clara, ordenada y accesible. De esta forma, se pretende reducir confusiones, errores en las cuentas y posibles inconvenientes al momento de repartir pagos entre varias personas.

Este proyecto toma como referencia el funcionamiento de aplicaciones ya existentes, como **Settle Up**, adaptando la idea a una propuesta propia desarrollada con tecnologias modernas.

## Clonar repositorios

El proyecto completo se divide en dos repositorios (BackEnd y FrontEnd). Para clonarlos en tu máquina local, ejecuta los siguientes comandos:

### Repositorio BackEnd
```bash
git clone https://github.com/Carrillo1992/CommunityBudgetBack.git
```

### Repositorio FrontEnd
```bash
git clone https://github.com/FranckMJS92/TFG.git
```
---

## Tecnologias utilizadas

### BackEnd

Para el desarrollo del back end se esta utilizando:

- **Java** como lenguaje de programacion principal.
- **Spring Boot** como framework base del proyecto.
- **Spring Security** para la autenticación y autorización (basada en JWT y OAuth2 con Google).
- **Arquitectura Hexagonal (Puertos y Adaptadores)** y principios de **DDD (Domain-Driven Design)** para organizar la logica del negocio de forma estructurada y mantenible.
- **MySQL** como sistema de gestion de base de datos relacional.
- **JPA / Hibernate** para el mapeo objeto-relacional (ORM).
- **MapStruct** para el mapeo de objetos (DTOs a entidades/dominio y viceversa).
- **Docker** para la creación de contenedores y facilitar el entorno de desarrollo y despliegue.

### FrontEnd

Para el desarrollo del front end se esta utilizando:

- **React** como framework para la construccion de la interfaz de usuario.
- **Tailwind CSS** para el diseno y los estilos visuales de la aplicacion.

##  Requisitos previos (FrontEnd)

- **Node.js** 18.x o superior
- **npm** 9.x o superior

---

##  Instalación y ejecución (FrontEnd)

###  Navegar al directorio
```bash
cd CommunityBudgetFront
```

###  Instalar dependencias
```bash
npm install
```

### Ejecutar en desarrollo
```bash
npm run dev
```

 Disponible en: http://localhost:5173

## ️ Construir para producción (FrontEnd)
```bash
npm run build
```

---

## Características principales

- **Gestión de usuarios**: Registro, inicio de sesión (local y con Google), actualización de perfil y cambio de contraseña.
- **Autenticación segura**: Uso de tokens JWT (Access y Refresh tokens) para mantener sesiones seguras.
- **Recuperación de contraseña**: Flujo de restablecimiento de contraseña mediante tokens por correo electrónico.
- **Gestión de grupos y gastos**: Registro y control detallado de los gastos realizados dentro de cada grupo.
- **Cálculo de balances y deudas**: Sistema inteligente que calcula de manera automática los balances de cada usuario y quién debe a quién para facilitar la liquidación de deudas.
- **Roles y permisos**: Diferenciación entre usuarios normales y administradores.
- **Manejo de errores global**: Respuestas de error estructuradas para una mejor experiencia de desarrollo y de usuario.

---

## Entorno de desarrollo (Docker - BackEnd)

El proyecto cuenta con configuración de Docker para simplificar el levantamiento del entorno de desarrollo. Con `docker-compose`, se pueden levantar rápidamente la base de datos MySQL y la propia aplicación empaquetada.

### Requisitos previos

- Tener instalado [Docker](https://www.docker.com/get-started) y [Docker Compose](https://docs.docker.com/compose/install/).

### Configuración inicial

Antes de iniciar el entorno de Docker, necesitas crear un archivo `.env` en la raíz del proyecto. Este archivo contendrá las variables de entorno necesarias para la base de datos y la aplicación.

Crea un archivo llamado `.env` en la misma ubicación que el archivo `docker-compose.yml` y añade las siguientes variables de configuración (ajustando los valores a tu entorno, sin subir datos reales al repositorio):

```env
# ======== Configuración de MySQL (Base de Datos) ========
MYSQL_ROOT_PASSWORD={{MYSQL_ROOT_PASSWORD}}
MYSQL_DATABASE={{MYSQL_DATABASE_NAME}}
# (Opcionales, si se usan en un entorno de producción o distinto a root)
# MYSQL_USER={{MYSQL_USER}}
# MYSQL_PASSWORD={{MYSQL_PASSWORD}}

# ======== Configuración del Servidor ========
SERVER_PORT={{SERVER_PORT}}
DB_PORT={{DB_PORT}}
DB_HOST={{DB_HOST}}
DB_NAME={{DB_NAME}}
DB_USER={{DB_USER}}
DB_PASSWORD={{DB_PASSWORD}}

# ======== Configuración de Seguridad y JWT ========
SPRING_SECURITY_USERNAME={{ADMIN_USERNAME}}
SPRING_SECURITY_PASSWORD={{ADMIN_PASSWORD}}
JWT_SECRET_KEY={{JWT_SECRET_KEY}}
JWT_EXPIRATION_MS={{TIEMPO_EXP_JWT}}
JWT_REFRESH_EXPIRATION_MS={{TIEMPO_EXP_JWT_REFRESH}}

# ======== Configuración de Google OAuth2 ========
GOOGLE_CLIENT_ID={{GOOGLE_CLIENT_ID}}
GOOGLE_CLIENT_SECRET={{GOOGLE_CLIENT_SECRET}}

# ======== Configuración de Servidor de Correos (Mail) ========
MAIL_FROM={{MAIL_FROM_ADDRESS}}
MAIL_HOST={{MAIL_HOST}}
MAIL_PORT={{MAIL_PORT}}
```

### Iniciar los contenedores

Una vez configurado el archivo `.env`, puedes levantar los servicios ejecutando el siguiente comando en la raíz del proyecto:

```bash
docker-compose up -d --build
```

Este comando descargará las imágenes necesarias, construirá la imagen de la aplicación y levantará ambos contenedores (la base de datos y la aplicación) en segundo plano.

### Detener los contenedores

Para detener los servicios, simplemente ejecuta:

```bash
docker-compose down
```

---

## Propuesta de valor

La finalidad de **Community Budget** es convertirse en una herramienta de apoyo para cualquier usuario que comparta gastos con otras personas. La aplicacion permite centralizar la informacion economica del grupo y mejorar la organizacion de los pagos, ofreciendo una experiencia similar a la de **Settle Up**, pero dentro del contexto de un proyecto propio.

## Conclusion

**Community Budget** es un proyecto pensado para resolver una necesidad comun en la vida cotidiana: la organizacion de gastos compartidos. Gracias al uso de tecnologias como Java, DDD, MySQL, React, Tailwind CSS y Docker, se busca construir una aplicacion moderna, funcional y escalable que aporte valor real a sus usuarios.

---

## Autores

### Nombre
- Daniel Carrillo Rangel
- Carlos García de la Torre
- Francisco López Monrroy

<div align="center"> <sub>Built with ❤️ for Community Budget</sub> </div>
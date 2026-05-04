# Community Budget

## Descripcion general

**Community Budget** es un proyecto orientado a facilitar la gestion de gastos compartidos entre amigos, companeros de piso, parejas o cualquier grupo de personas que necesite organizar cuentas en comun de forma sencilla y practica.

La aplicacion busca ofrecer una solucion util para registrar gastos, dividir importes y llevar un mejor control de los pagos pendientes dentro de un grupo.

## Objetivo del proyecto

El principal objetivo de **Community Budget** es ayudar a los usuarios a administrar gastos compartidos de manera clara, ordenada y accesible. De esta forma, se pretende reducir confusiones, errores en las cuentas y posibles inconvenientes al momento de repartir pagos entre varias personas.

Este proyecto toma como referencia el funcionamiento de aplicaciones ya existentes, como **Settle Up**, adaptando la idea a una propuesta propia desarrollada con tecnologias modernas.

## Tecnologias utilizadas

### Back end

Para el desarrollo del back end se esta utilizando:

- **Java** como lenguaje de programacion principal.
- **Spring Boot** como framework base del proyecto.
- **Spring Security** para la autenticación y autorización (basada en JWT y OAuth2 con Google).
- **Arquitectura Hexagonal (Puertos y Adaptadores)** y principios de **DDD (Domain-Driven Design)** para organizar la logica del negocio de forma estructurada y mantenible.
- **MySQL** como sistema de gestion de base de datos relacional.
- **JPA / Hibernate** para el mapeo objeto-relacional (ORM).
- **MapStruct** para el mapeo de objetos (DTOs a entidades/dominio y viceversa).

### Front end

Para el desarrollo del front end se esta utilizando:

- **React** como framework para la construccion de la interfaz de usuario.
- **Tailwind CSS** para el diseno y los estilos visuales de la aplicacion.

## Características principales

- **Gestión de usuarios**: Registro, inicio de sesión (local y con Google), actualización de perfil y cambio de contraseña.
- **Autenticación segura**: Uso de tokens JWT (Access y Refresh tokens) para mantener sesiones seguras.
- **Recuperación de contraseña**: Flujo de restablecimiento de contraseña mediante tokens por correo electrónico.
- **Roles y permisos**: Diferenciación entre usuarios normales y administradores.
- **Manejo de errores global**: Respuestas de error estructuradas para una mejor experiencia de desarrollo y de usuario.

## Propuesta de valor

La finalidad de **Community Budget** es convertirse en una herramienta de apoyo para cualquier usuario que comparta gastos con otras personas. La aplicacion permite centralizar la informacion economica del grupo y mejorar la organizacion de los pagos, ofreciendo una experiencia similar a la de **Settle Up**, pero dentro del contexto de un proyecto propio.

## Conclusion

**Community Budget** es un proyecto pensado para resolver una necesidad comun en la vida cotidiana: la organizacion de gastos compartidos. Gracias al uso de tecnologias como Java, DDD, MySQL, React y Tailwind CSS, se busca construir una aplicacion moderna, funcional y escalable que aporte valor real a sus usuarios.

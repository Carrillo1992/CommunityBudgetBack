# Postman Collection - CommunityBudget API

Esta carpeta contiene los archivos de Postman para probar la API de CommunityBudget.

## Archivos

- **CommunityBudget.postman_collection.json**: Colección completa con todos los endpoints de Auth y User.
- **CommunityBudget.postman_environment.json**: Archivo de entorno con las variables necesarias.

## Cómo importar

1. Abre Postman
2. Haz clic en "Import" (Importar)
3. Selecciona ambos archivos JSON
4. La colección y el entorno se importarán automáticamente

## Configuración

Asegúrate de seleccionar el entorno **"CommunityBudget - Local"** en Postman antes de ejecutar las peticiones.

## Endpoints disponibles

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Registrar un nuevo usuario |
| POST | `/api/v1/auth/login` | Iniciar sesión y obtener tokens |
| POST | `/api/v1/auth/refresh` | Refrescar token de acceso |

### User
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/user/me` | Obtener usuario autenticado |
| PUT | `/api/v1/user/me` | Actualizar usuario autenticado |
| PUT | `/api/v1/user/me/change-password` | Cambiar contraseña |
| DELETE | `/api/v1/user/me` | Eliminar cuenta del usuario |
| POST | `/api/v1/user/register` | Registrar nuevo usuario |

## Flujo de uso recomendado

1. **Registrar un usuario** usando `Auth > Register`
2. **Iniciar sesión** usando `Auth > Login` - Los tokens se guardan automáticamente en las variables de la colección
3. **Usar los endpoints de User** - El token de acceso se incluye automáticamente en las cabeceras
4. **Refrescar token** cuando expire usando `Auth > Refresh Token`

## Variables

| Variable | Descripción |
|----------|-------------|
| `baseUrl` | URL base de la API (por defecto: `http://localhost:8080/api/v1`) |
| `accessToken` | Token JWT de acceso (se guarda automáticamente tras login) |
| `refreshToken` | Token de refresco (se guarda automáticamente tras login) |

## Notas

- Las contraseñas deben cumplir con el patrón: mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&)
- Los endpoints de User requieren autenticación (Bearer Token)
- Los endpoints de Auth no requieren autenticación


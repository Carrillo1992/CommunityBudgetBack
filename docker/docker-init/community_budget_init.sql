-- ESQUEMA DE BASE DE DATOS PARA COMMUNITY BUDGET (MySQL)
-- Ejecutar este script para inicializar la BBDD

-- 1. TABLA DE USUARIOS
-- Almacena la info básica y el ID de Google para el OAuth
CREATE TABLE `users`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email`       VARCHAR(255) NOT NULL UNIQUE,
    `name`        VARCHAR(100) NOT NULL,
    `avatar_url`  VARCHAR(500),
    `provider`    VARCHAR(20) DEFAULT 'LOCAL', -- 'GOOGLE', 'LOCAL'
    `provider_id` VARCHAR(255),                -- ID único que devuelve Google (sub)
    `created_at`  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- 2. TABLA DE GRUPOS
-- Los grupos de gastos (ej: "Viaje a Berlín", "Piso Estudiantes")
CREATE TABLE `groups`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(100) NOT NULL,
    `description` TEXT,
    `currency`    VARCHAR(3) DEFAULT 'EUR', -- EUR, USD, etc.
    `invite_code` VARCHAR(50) UNIQUE,       -- Código para compartir (ej: "X8J9L2")
    `created_by`  BIGINT,
    `created_at`  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`created_by`) REFERENCES users (`id`)
);

-- 3. TABLA INTERMEDIA (USUARIOS <-> GRUPOS)
-- Relación Many-to-Many: Un usuario puede estar en N grupos y un grupo tiene N usuarios.
CREATE TABLE `group_members`
(
    `id`        BIGINT AUTO_INCREMENT PRIMARY KEY,
    `group_id`  BIGINT NOT NULL,
    `user_id`   BIGINT NOT NULL,
    `joined_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `is_admin`  BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    UNIQUE (`group_id`, `user_id`) -- Evita que un usuario esté duplicado en el mismo grupo
);

-- 4. TABLA DE GASTOS (CABECERA)
-- Representa el "ticket" o el evento de pago.
CREATE TABLE expenses
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `group_id`      BIGINT         NOT NULL,
    `payer_id`      BIGINT         NOT NULL,                   -- Quién sacó la cartera y pagó
    `description`   VARCHAR(255)   NOT NULL,
    `amount`        DECIMAL(19, 4) NOT NULL,                   -- Total del ticket
    `date`          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `category`      VARCHAR(50)             DEFAULT 'GENERAL', -- COMIDA, TRANSPORTE, etc.
    `is_settlement` BOOLEAN                 DEFAULT FALSE,     -- TRUE si es un pago para saldar deuda (ej: Bizum de ajuste)
    `image_url`     VARCHAR(500),                              -- Foto del ticket (opcional)
    `created_at`    TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`payer_id`) REFERENCES users (`id`)
);

-- 5. TABLA DE DIVISIÓN DE GASTOS (DETALLE)
-- Especifica cuánto le corresponde pagar a cada miembro del gasto total.
-- Ejemplo: Gasto de 30€. Payer: Juan.
-- Shares: Juan (10€), Ana (10€), Pedro (10€).
CREATE TABLE `expense_shares`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `expense_id`  BIGINT         NOT NULL,
    `user_id`     BIGINT         NOT NULL, -- Persona involucrada en el gasto
    `owed_amount` DECIMAL(19, 4) NOT NULL, -- Cuánto le toca pagar a esta persona
    FOREIGN KEY (`expense_id`) REFERENCES expenses (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES users (`id`)
);

-- 6. TABLA DE ROLES
-- Define los roles disponibles en el sistema
CREATE TABLE `roles`
(
    `id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE
);

-- 7. TABLA INTERMEDIA (USUARIOS <-> ROLES)
-- Relación Many-to-Many: Un usuario puede tener N roles y un rol puede pertenecer a N usuarios.
CREATE TABLE `user_roles`
(
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES roles (`id`) ON DELETE CASCADE
);

-- 8. TABLA DE RECUPERACION DE CONTRASEÑAS

CREATE TABLE `password_resets`
(
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `token`      VARCHAR(50) NOT NULL,
    `email`      VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `expired_at` TIMESTAMP
);


-- Insertar roles por defecto
INSERT INTO `roles` (`name`)
VALUES ('ROLE_USER');
INSERT INTO `roles` (`name`)
VALUES ('ROLE_ADMIN');

-- Índices para mejorar el rendimiento (Opcional pero recomendado)
CREATE INDEX idx_group_members_user ON group_members (`user_id`);
CREATE INDEX idx_expenses_group ON expenses (`group_id`);
CREATE INDEX idx_expense_shares_expense ON expense_shares (`expense_id`);
CREATE INDEX idx_user_roles_user ON user_roles (`user_id`);
CREATE INDEX idx_user_roles_role ON user_roles (`role_id`);

-- Script SQL para crear la tabla de usuarios en PostgreSQL
-- Base de datos: zapateria

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Crear índices para mejorar búsquedas
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Comentarios descriptivos
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema de zapatería';
COMMENT ON COLUMN users.email IS 'Email único del usuario';
COMMENT ON COLUMN users.password IS 'Contraseña encriptada con BCrypt';
COMMENT ON COLUMN users.first_name IS 'Primer nombre del usuario';
COMMENT ON COLUMN users.last_name IS 'Apellido del usuario';
COMMENT ON COLUMN users.phone_number IS 'Número de teléfono del usuario';
COMMENT ON COLUMN users.address IS 'Dirección del usuario';
COMMENT ON COLUMN users.city IS 'Ciudad del usuario';
COMMENT ON COLUMN users.postal_code IS 'Código postal del usuario';
COMMENT ON COLUMN users.created_at IS 'Fecha de creación del registro';
COMMENT ON COLUMN users.updated_at IS 'Fecha de última actualización';
COMMENT ON COLUMN users.is_active IS 'Indica si el usuario está activo';

-- Insertar usuario de prueba (contraseña: password123)
-- La contraseña está encriptada con BCrypt
INSERT INTO users (email, password, first_name, last_name, phone_number, address, city, postal_code, is_active)
VALUES ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36jbMxO2', 'Test', 'User', '987654321', 'Calle 123', 'Lima', '01001', true);

-- Seleccionar para verificar
SELECT * FROM users;

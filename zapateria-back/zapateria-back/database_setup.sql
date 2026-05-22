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

-- Categorías y productos para restaurar el catálogo visual
-- Ejecutar sobre una base de datos vacía o antes de volver a cargar datos de prueba
ALTER TABLE IF EXISTS category ADD COLUMN IF NOT EXISTS image VARCHAR(500);

DELETE FROM product;
DELETE FROM category;

INSERT INTO category (id, name, image) VALUES
(1, 'Hombre', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop'),
(2, 'Mujer', 'https://images.unsplash.com/photo-1543163521-9efcc06814ee?w=400&h=300&fit=crop'),
(3, 'Deportivos', 'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=300&fit=crop'),
(4, 'Formales', 'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=300&fit=crop'),
(5, 'Accesorios', 'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=300&fit=crop');

INSERT INTO product (id, name, price, image, category_id, stock, is_new, discount, rating) VALUES
('1','Zapato Casual Premium',189.00,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',1,20,false,20,4.5),
('2','Zapatilla Deportiva Elite',249.00,'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop',3,15,true,0,5.0),
('3','Sandalia Cómoda de Cuero',149.00,'https://images.unsplash.com/photo-1562183241-b937e341ade7?w=400&h=400&fit=crop',2,18,false,0,4.0),
('4','Zapato Formal Ejecutivo',299.00,'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop',4,12,false,15,4.8),
('5','Derby Clásico',279.00,'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',4,8,false,10,4.7),
('6','Kit de Limpieza Premium',79.00,'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=400&fit=crop',5,50,true,0,4.1),
('7','Zapato Infantil Colorido',129.00,'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=400&fit=crop',3,25,true,5,4.3),
('8','Tenis Casual Urbano',179.00,'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop',1,30,false,0,4.4);

-- Seleccionar para verificar
SELECT * FROM users;

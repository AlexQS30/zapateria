-- Script para restaurar las imagenes del catalogo sin perder los registros existentes.
-- Ejecutar sobre la base de datos zapateria despues de crear las tablas.

ALTER TABLE IF EXISTS category ADD COLUMN IF NOT EXISTS image VARCHAR(500);

UPDATE category SET image = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=300&fit=crop' WHERE name = 'Hombre';
UPDATE category SET image = 'https://images.unsplash.com/photo-1543163521-9efcc06814ee?w=400&h=300&fit=crop' WHERE name = 'Mujer';
UPDATE category SET image = 'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=300&fit=crop' WHERE name = 'Deportivos';
UPDATE category SET image = 'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=300&fit=crop' WHERE name = 'Formales';
UPDATE category SET image = 'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=300&fit=crop' WHERE name = 'Accesorios';

UPDATE product SET image = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop' WHERE id = '1';
UPDATE product SET image = 'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop' WHERE id = '2';
UPDATE product SET image = 'https://images.unsplash.com/photo-1562183241-b937e341ade7?w=400&h=400&fit=crop' WHERE id = '3';
UPDATE product SET image = 'https://images.unsplash.com/photo-1507222405253-b8ff5d6c0937?w=400&h=400&fit=crop' WHERE id = '4';
UPDATE product SET image = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop' WHERE id = '5';
UPDATE product SET image = 'https://images.unsplash.com/photo-1572307480616-406f0ee9293e?w=400&h=400&fit=crop' WHERE id = '6';
UPDATE product SET image = 'https://images.unsplash.com/photo-1541959227685-cdde63974b53?w=400&h=400&fit=crop' WHERE id = '7';
UPDATE product SET image = 'https://images.unsplash.com/photo-1460353581641-694a62b78e76?w=400&h=400&fit=crop' WHERE id = '8';
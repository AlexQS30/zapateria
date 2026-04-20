-- Categories
INSERT INTO category (id, name) VALUES (1, 'Hombre');
INSERT INTO category (id, name) VALUES (2, 'Mujer');
INSERT INTO category (id, name) VALUES (3, 'Deportivos');
INSERT INTO category (id, name) VALUES (4, 'Formales');
INSERT INTO category (id, name) VALUES (5, 'Accesorios');

-- Products (id is string)
INSERT INTO product (id, name, price, image, category_id, stock, is_new, discount, rating) VALUES
('1','Zapato Casual Premium',189.00,'/img/casual.jpg',1,20,false,0,4.5),
('2','Tenis Deportivo Elite',259.00,'/img/deportivo.jpg',3,15,true,20,5.0),
('3','Zapato Formal Ejecutivo',279.00,'/img/formal.jpg',1,10,false,10,4.8),
('4','Zapatilla Running',199.00,'/img/running.jpg',3,0,false,0,4.3),
('5','Zapato Derby Formal',299.00,'/img/formal.jpg',4,12,false,15,4.7),
('6','Kit de Limpieza Premium',79.00,'/img/casual.jpg',5,50,true,0,4.1);

-- Users: create an admin and a regular user
INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role)
VALUES (1,'admin@example.com','$2a$10$e0NRjVq1q7gYxQZp2QF2..', 'Admin','User','000','Address','City','0000', now(), now(), true, 'ADMIN');

INSERT INTO users (id, email, password, first_name, last_name, phone_number, address, city, postal_code, created_at, updated_at, is_active, role)
VALUES (2,'user@example.com','$2a$10$e0NRjVq1q7gYxQZp2QF2..','Regular','User','111','Address2','City2','1111', now(), now(), true, 'USER');

-- Purchases: sample purchase by user 2 buying product 2
INSERT INTO purchase (id, user_id, total, created_at) VALUES ('p1','2',259.00, now());
INSERT INTO purchase_item (purchase_id, product_id, quantity) VALUES ('p1','2',1);

-- Reviews: user 2 reviewed product 2
INSERT INTO review (product_id, user_id, rating, comment, created_at) VALUES ('2','2',5,'Excelente producto', now());

-- Limpia la tabla
DELETE FROM books;

INSERT INTO books (id, titulo, autor, descripcion, imagen_url, precio, stock, categoria, activo)
VALUES
(1, 'Informática en 8 pasos', 'Skadi', 'Aprende informática desde cero.',
 'https://blogs.uoc.edu/informatica/wp-content/uploads/sites/153/2017/03/cx-picture-100608993-primary-idge_.jpg',
 10000, 10, 'Tecnología', true),

(2, 'Poder Tecnológico', 'Heavy', 'La tecnología es poder.',
 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ91CUBxpwFzAt_PiQ3trLYrQhGImOQxyfIiA&s',
 15000, 12, 'Tecnología', true),

(3, 'Aprende a codear', 'Crown', 'El arte de la programación.',
 'https://i.ytimg.com/vi/awP_WP_63fc/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLBlJplHjrUa6tz_WB-w9VJWU9iPQw',
 20000, 8, 'Programación', true),

(4, 'Java sí, Python no', 'Victor', '¿Java o Python? El eterno debate.',
 'https://imgur.com/a/fXpcW0B.jpg',
 25000, 6, 'Programación', true),

(5, 'Lolsito: la odisea', 'Lukkk', 'Una aventura épica en el mundo de los videojuegos.',
 'https://cdn2.unrealengine.com/a-beginner-s-guide-to-league-of-legends-teemo-1215x717-dc27844d5953.jpg',
 30000, 5, 'Videojuegos', true);


USE error404db;

CREATE TABLE IF NOT EXISTS User (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    ActFrag INT DEFAULT 0,
    BestScore INT DEFAULT 0,
    vidaInicial INT DEFAULT 100,
    monedas INT DEFAULT 100
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS GameObject (
    id VARCHAR(10) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(50) NOT NULL,
    precio INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS User_GameObject (
    username VARCHAR(50) NOT NULL,
    id VARCHAR(10) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    PRIMARY KEY (username, id),
    FOREIGN KEY (username) REFERENCES User(username) ON DELETE CASCADE,
    FOREIGN KEY (id) REFERENCES GameObject(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj01', 'Espada de Fuego', 'Una espada legendaria que causa daño de fuego', 'ESPADA', 150),
('obj02', 'Escudo Mágico', 'Escudo que absorbe daño mágico', 'ESCUDO', 120),
('obj03', 'Poción de Vida', 'Restaura 50 puntos de vida', 'POCION', 30);

INSERT IGNORE INTO User (username, password, email, ActFrag, BestScore, vidaInicial, monedas) VALUES
('testuser', 'test123', 'test@example.com', 0, 0, 100, 500);

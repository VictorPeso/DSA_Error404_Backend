-- =====================================================
-- Script para añadir más objetos a la tienda
-- TOTAL: 10 objetos (3 ya existen + 7 nuevos)
-- Sistema de rareza integrado
-- =====================================================

USE error404db;

-- NOTA: obj01, obj02, obj03 ya existen en setup_tables.sql
-- Aquí añadimos 7 objetos nuevos para completar 10 totales

-- =====================================================
-- OBJETOS COMUNES (50% drop) - Precio: 20-35
-- =====================================================

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj04', 'Reinicio rápido', 'Restaura 25 puntos de vida', 'POCION', 20),
('obj05', 'Escaner de paquetes', 'Ataque básico (+10 daño)', 'ARMA', 30),
('obj06', 'Firewall adaptativo', 'Protección básica (+8 defensa)', 'ARMADURA', 35);

-- =====================================================
-- OBJETOS RAROS (30% drop) - Precio: 75-110
-- =====================================================

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj09', 'Antivirus predictivo', 'Antivirus resistente a cualquier amenaza (+25 daño)', 'ARMA', 90),
('obj10', 'Actualización de emergencia', 'Actualización pesada y resistente (+20 defensa, +30 HP)', 'ARMADURA', 110),
('obj11', 'Snapshot del sistema', 'Copia de seguridad que ofrece protección (+12 defensa)', 'ARMADURA', 75);

-- =====================================================
-- OBJETOS ÉPICOS (15% drop) - Precio: 180-200
-- =====================================================

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj14', 'Destructor de malware', 'Encargado de eliminar toda amenaza sospechosa (+40 daño, +10% crítico)', 'ARMA', 180),
('obj15', 'Cifrado avanzado', 'Cifrado forjado por ingenieros expertos (+35 defensa, +50 HP)', 'ARMADURA', 200);

-- =====================================================
-- OBJETOS LEGENDARIOS (5% drop) - Precio: 300-450
-- =====================================================

-- obj01 ya existe (Espada de Fuego - 300 monedas)

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj18', 'CCleaner', 'Protege hasta del malware indestructible (+60 defensa, +100 HP, -15% daño recibido)', 'ARMADURA', 450);

-- =====================================================
-- RESUMEN DE OBJETOS (10 TOTALES)
-- =====================================================

-- COMÚN (50% drop):
--   obj04: Poción Pequeña (20 monedas) - POCION
--   obj05: Daga Básica (30 monedas) - ESPADA
--   obj06: Escudo de Madera (35 monedas) - ESCUDO

-- RARO (30% drop):
--   obj09: Espada de Acero (90 monedas) - ESPADA
--   obj10: Armadura de Hierro (110 monedas) - ARMADURA
--   obj11: Casco de Soldado (75 monedas) - CASCO

-- ÉPICO (15% drop):
--   obj02: Escudo Mágico (120 monedas) - ESCUDO [original]
--   obj14: Espada Encantada (180 monedas) - ESPADA
--   obj15: Escudo de Dragón (200 monedas) - ESCUDO

-- LEGENDARIO (5% drop):
--   obj01: Espada de Fuego (300 monedas) - ESPADA [original]
--   obj18: Armadura del Titán (450 monedas) - ARMADURA

-- obj03: Poción de Vida (30 monedas) - POCION [original, puede ser comprada en tienda]

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

-- Para ver todos los objetos:
-- SELECT id, nombre, tipo, precio FROM GameObject ORDER BY precio;

-- Para contar objetos:
-- SELECT COUNT(*) as total_objetos FROM GameObject;
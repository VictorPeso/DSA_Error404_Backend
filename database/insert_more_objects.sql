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
('obj04', 'Poción Pequeña', 'Restaura 25 puntos de vida', 'POCION', 20),
('obj05', 'Daga Básica', 'Daga de entrenamiento (+10 daño)', 'ESPADA', 30),
('obj06', 'Escudo de Madera', 'Protección básica (+8 defensa)', 'ESCUDO', 35);

-- =====================================================
-- OBJETOS RAROS (30% drop) - Precio: 75-110
-- =====================================================

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj09', 'Espada de Acero', 'Espada forjada en acero resistente (+25 daño)', 'ESPADA', 90),
('obj10', 'Armadura de Hierro', 'Armadura pesada y resistente (+20 defensa, +30 HP)', 'ARMADURA', 110),
('obj11', 'Casco de Soldado', 'Casco de batalla que protege la cabeza (+12 defensa)', 'CASCO', 75);

-- =====================================================
-- OBJETOS ÉPICOS (15% drop) - Precio: 180-200
-- =====================================================

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj14', 'Espada Encantada', 'Espada imbuida con magia antigua (+40 daño, +10% crítico)', 'ESPADA', 180),
('obj15', 'Escudo de Dragón', 'Escudo forjado con escamas de dragón (+35 defensa, +50 HP)', 'ESCUDO', 200);

-- =====================================================
-- OBJETOS LEGENDARIOS (5% drop) - Precio: 300-450
-- =====================================================

-- obj01 ya existe (Espada de Fuego - 300 monedas)

INSERT IGNORE INTO GameObject (id, nombre, descripcion, tipo, precio) VALUES
('obj18', 'Armadura del Titán', 'Armadura legendaria indestructible (+60 defensa, +100 HP, -15% daño recibido)', 'ARMADURA', 450);

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

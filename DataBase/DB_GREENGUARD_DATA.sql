-- ============================================
-- DATOS DE PRUEBA PARA SISTEMA DE REPORTES
-- ============================================

-- TB_ROL
INSERT INTO tb_rol (descripcion) VALUES
('ADMIN'),
('CLIENTE'),
('COMERCIANTE'), --(Tienda)
('SUPERVISOR'); -- (Municipalidad)

-- TB_DISTRITO
INSERT INTO tb_distrito (desc_distrito) VALUES
('Lima'),
('Miraflores'),
('San Isidro'),
('Surco'),
('La Molina'),
('San Borja'),
('Jesús María'),
('Lince'),
('Magdalena'),
('Pueblo Libre'),
('San Miguel'),
('Breña'),
('La Victoria'),
('Cercado de Lima'),
('Barranco'),
('Chorrillos'),
('San Juan de Miraflores'),
('Villa El Salvador'),
('Villa María del Triunfo'),
('San Juan de Lurigancho');

-- TB_TIPOS_INCIDENTES
INSERT INTO tb_tipos_incidentes (desc_tipo_inci) VALUES
('BACHES'),
('POSTE CAÍDO'),
('VEREDA RAJADA'),
('PISTAS CON HUECOS'),
('SEMÁFORO MALOGRADO'),
('FUGA DE AGUA'),
('BASURA ACUMULADA'),
('ALUMBRADO PÚBLICO'),
('ÁRBOL CAÍDO'),
('OTROS');

-- TB_TIPO_CLASIFICACION
INSERT INTO tb_tipo_clasificacion (desc_tipo_clasi) VALUES
('Riesgo Bajo'),
('Riesgo Medio'),
('Riesgo Alto'),
('Riesgo Crítico');

-- TB_CATEGORIA
INSERT INTO tb_categoria (desc_cate) VALUES
('ROPAS'),
('COMIDAS'),
('ABARROTES'),
('ELECTRODOMÉSTICOS'),
('SALUD Y BELLEZA'),
('ENTRETENIMIENTO'),
('TECNOLOGÍA'),
('DEPORTES');

-- TB_USUARIO
INSERT INTO tb_usuario (nom_usu, ape_pat_usu, ape_mat_usu, documento_usu, correo_usu, password_usu, telefono_usu, genero_usu, puntos_usu, id_rol,id_distrito) VALUES
('Carlos', 'García', 'López', '12345678', 'carlos.garcia@email.com', 'clave123', '987654321', 'M', 150, 1,1),

('María', 'Rodríguez', 'Pérez', '87654321', 'maria.rodriguez@email.com', 'clave123', '987654322', 'F', 280, 2,2),
('Juan', 'Martínez', 'Sánchez', '11223344', 'juan.martinez@email.com', 'clave123', '987654323', 'M', 420, 2,3),
('Pedro', 'Flores', 'Vega', '55667788', 'pedro.flores@email.com', 'clave123', '987654325', 'M', 95, 2,4),
('Roberto', 'Morales', 'Gutiérrez', '99887766', 'roberto.morales@email.com', 'clave123', '987654327', 'M', 520, 2,4),
('Miguel', 'Herrera', 'Silva', '77889900', 'miguel.herrera@email.com', 'clave123', '987654329', 'M', 210, 2,6),
('Ana', 'Torres', 'Ramos', '44332211', 'ana.torres@email.com', 'clave123', '987654324', 'F', 180, 3,7),

('Lucía', 'Díaz', 'Castro', '88776655', 'lucia.diaz@email.com', 'clave123', '987654326', 'F', 340, 3, 1),
('Isabel', 'Quispe', 'Rojas', '00998877', 'isabel.quispe@email.com', 'clave123', '987654330', 'F', 160, 3,2),

('Carmen', 'Vargas', 'Mendoza', '66778899', 'carmen.vargas@email.com', 'clave123', '987654328', 'F', 75, 4,3);



-- TB_TIENDA
INSERT INTO tb_tienda (nom_tienda, id_usu, id_distrito) VALUES
('Tienda La Esquina', 4, 2),
('Bodega San Miguel', 6, 11),
('Minimarket El Sol', 10, 4),
('Comercial Lima Centro', 4, 1),
('Tienda Rápida Express', 6, 5);

-- TB_CUPON
INSERT INTO tb_cupon (nombre_cupon, desc_cupon, id_cate, cod_cupon, puntos_requeridos, id_tienda) VALUES
('Descuento 20% Ropa', '20% desc. en toda la tienda', 1, 'ROPA20', 100, 1),
('Descuento 50% Comidas', '50% desc. en todos los almuerzos', 2, 'LUNCH50', 150, 2),
('Descuento 10% Abarrotes', '10% desc. en compra de abarrotes', 3, 'ABARR10', 80, 3),
('Descuento 15% Electrodomésticos', '15% desc. en electrodomésticos', 4, 'ELECTRO15', 200, 4),
('Descuento 25% Belleza', '25% desc. en productos de belleza', 5, 'BELLE25', 120, 5),
('Descuento 35% General', '35% desc. en toda la tienda', 3, 'DESC35', 180, 1),
('Descuento 30% Deportes', '30% desc. en artículos deportivos', 8, 'SPORT30', 250, 2),
('Descuento 40% Comidas', '40% desc. en productos alimenticios', 2, 'FOOD40', 160, 3);

-- TB_USUARIO_CUPON
INSERT INTO tb_usuario_cupon (id_cupon, id_usuario, codigo_cupon, qr_verification_code, canjeado, estado) VALUES
(1, 2, 'ROPA20-USR2-001', 'QR-a1b2c3d4e5f6g7h8', FALSE, 'AC'),
(2, 3, 'LUNCH50-USR3-001', 'QR-b2c3d4e5f6g7h8i9', TRUE, 'CA'),
(3, 5, 'ABARR10-USR5-001', 'QR-c3d4e5f6g7h8i9j0', FALSE, 'AC'),
(4, 7, 'ELECTRO15-USR7-001', 'QR-d4e5f6g7h8i9j0k1', FALSE, 'AC'),
(5, 9, 'BELLE25-USR9-001', 'QR-e5f6g7h8i9j0k1l2', FALSE, 'VE'),
(6, 2, 'DESC35-USR2-002', 'QR-f6g7h8i9j0k1l2m3', FALSE, 'AC'),
(7, 3, 'SPORT30-USR3-002', 'QR-g7h8i9j0k1l2m3n4', FALSE, 'AC'),
(8, 7, 'FOOD40-USR7-002', 'QR-h8i9j0k1l2m3n4o5', TRUE, 'CA');

-- TB_TIPO_NOTIFICACION

INSERT INTO tb_tipo_notificacion(descripcion) VALUES

('REPORTE_PENDIENTE'),
('REPORTE_EN_PROCESO'),
('REPORTE_RESUELTO'),
('REPORTE_CANCELADO'),
('PUNTOS_GANADOS'),
('BONUS_MULTIPLO_5'),
('CUPON_DISPONIBLE'),
('CUPON_POR_VENCER'),
('CUPON_VENCIDO'),
('SISTEMA'),
('PROMOCIÓN');

-- TB_NOTIFICACION
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, id_cupon, leida) VALUES
(2, 1, 'Reporte Registrado', 'Tu reporte ha sido registrado exitosamente', NULL, NULL, NULL, TRUE),
(3, 3, 'Reporte Resuelto', 'Tu reporte ha sido resuelto. ¡Ganaste 50 puntos!', NULL, NULL, NULL, TRUE),
(2, 5, 'Cupón Canjeado', 'Has canjeado un cupón de 20% descuento', NULL, 1, 1, FALSE),
(5, 6, 'Nuevo Cupón Disponible', 'Hay nuevos cupones disponibles en tu zona', NULL, NULL, 3, FALSE),
(7, 8, 'Puntos Ganados', 'Has ganado 50 puntos por tu reporte', NULL, NULL, NULL, TRUE),
(9, 7, 'Cupón por Vencer', 'Tu cupón vence en 3 días', NULL, 5, 5, FALSE),
(3, 2, 'Reporte en Proceso', 'Tu reporte está siendo atendido', NULL, NULL, NULL, TRUE),
(7, 6, 'Promoción Especial', 'Nuevas promociones disponibles esta semana', NULL, NULL, NULL, FALSE),
(2, 9, 'Actualización del Sistema', 'Nueva versión disponible con mejoras', NULL, NULL, NULL, TRUE),
(5, 10, 'Promoción 2x1', 'Doble puntos este fin de semana', NULL, NULL, NULL, FALSE);




select * from 

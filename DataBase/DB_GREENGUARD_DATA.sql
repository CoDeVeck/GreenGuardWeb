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
('Ancón'),
('Ate'),
('Barranco'),
('Breña'),
('Carabayllo'),
('Chaclacayo'),
('Chorrillos'),
('Cieneguilla'),
('Comas'),
('El Agustino'),
('Independencia'),
('Jesús María'),
('La Molina'),
('La Victoria'),
('Lince'),
('Los Olivos'),
('Lurigancho'),
('Lurín'),
('Magdalena del Mar'),
('Miraflores'),
('Pachacámac'),
('Pucusana'),
('Pueblo Libre'),
('Puente Piedra'),
('Punta Hermosa'),
('Punta Negra'),
('Rímac'),
('San Bartolo'),
('San Borja'),
('San Isidro'),
('San Juan de Lurigancho'),
('San Juan de Miraflores'),
('San Luis'),
('San Martín de Porres'),
('San Miguel'),
('Santa Anita'),
('Santa María del Mar'),
('Santa Rosa'),
('Santiago de Surco'),
('Surquillo'),
('Villa El Salvador'),
('Villa María del Triunfo');


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
INSERT INTO tb_cupon (nombre_cupon, desc_cupon, id_cate, puntos_requeridos, id_tienda,stock_disponible, fecha_vencimiento) VALUES
('Descuento 20% Ropa', '20% desc. en toda la tienda', 1, 100, 1,100, '2025-12-24'),
('Descuento 50% Comidas', '50% desc. en todos los almuerzos', 2, 150, 2,100,'2025-12-24'),
('Descuento 10% Abarrotes', '10% desc. en compra de abarrotes', 3, 80, 3,100,'2025-12-24'),
('Descuento 15% Electrodomésticos', '15% desc. en electrodomésticos', 4, 200, 4,100,'2025-12-24'),
('Descuento 25% Belleza', '25% desc. en productos de belleza', 5, 120, 5,100,'2025-12-24'),
('Descuento 35% General', '35% desc. en toda la tienda', 3, 180, 1,100,'2025-12-24'),
('Descuento 30% Deportes', '30% desc. en artículos deportivos', 8, 250, 2,100,'2025-12-24'),
('Descuento 40% Comidas', '40% desc. en productos alimenticios', 2, 160, 3,100,'2025-12-24');

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









--SOLO SON DATOS DE PRUEBA NO ES NCESARIO EJECUTAR ESTOS INSERTS 

-- ============================================
-- REPORTES CON ESTADO 'PE' (PENDIENTE)
-- ============================================

-- Reporte PE + Incidente 1 + Clasificación 1 (Riesgo Bajo)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00002', 2, 'Basura acumulada en la esquina', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo1.jpg', 'PE', -12.046500, -77.042800, 1, 1, 2, '2025-12-07 08:30:00', NULL);

-- Reporte PE + Incidente 2 + Clasificación 2 (Riesgo Medio)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00003', 2, 'Poste de luz dañado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo2.jpg', 'PE', -12.047000, -77.043000, 2, 2, 2, '2025-12-07 09:15:00', NULL);

-- Reporte PE + Incidente 3 + Clasificación 3 (Riesgo Alto)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00004', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo3.jpg', 'PE', -12.047500, -77.043500, 3, 3, 2, '2025-12-07 10:00:00', NULL);

-- ============================================
-- REPORTES CON ESTADO 'EP' (EN PROCESO)
-- ============================================

-- Reporte EP + Incidente 1 + Clasificación 2 (Riesgo Medio)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00005', 2, 'Árbol caído bloqueando la vía', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo4.jpg', 'EP', -12.048000, -77.044000, 1, 2, 2, '2025-12-06 14:20:00', NULL);

-- Reporte EP + Incidente 2 + Clasificación 3 (Riesgo Alto)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00006', 2, 'Fuga de agua en tubería principal', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo5.jpg', 'EP', -12.048500, -77.044500, 2, 3, 2, '2025-12-06 15:45:00', NULL);

-- Reporte EP + Incidente 3 + Clasificación 1 (Riesgo Bajo)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00007', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo6.jpg', 'EP', -12.049000, -77.045000, 3, 1, 2, '2025-12-06 16:30:00', NULL);

-- ============================================
-- REPORTES CON ESTADO 'RE' (RESUELTO)
-- ============================================

-- Reporte RE + Incidente 1 + Clasificación 1 (Riesgo Bajo)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00008', 2, 'Bache en la pista reparado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo7.jpg', 'RE', -12.049500, -77.045500, 1, 1, 2, '2025-12-05 08:00:00', '2025-12-06 18:00:00');

-- Reporte RE + Incidente 2 + Clasificación 2 (Riesgo Medio)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00009', 2, 'Semáforo reparado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo8.jpg', 'RE', -12.050000, -77.046000, 2, 2, 2, '2025-12-04 10:30:00', '2025-12-05 12:00:00');

-- Reporte RE + Incidente 3 + Clasificación 3 (Riesgo Alto)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00010', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo9.jpg', 'RE', -12.050500, -77.046500, 3, 3, 2, '2025-12-03 09:00:00', '2025-12-04 17:30:00');

-- ============================================
-- REPORTES CON ESTADO 'CA' (CANCELADO)
-- ============================================

-- Reporte CA + Incidente 1 + Clasificación 3 (Riesgo Alto)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00011', 2, 'Reporte duplicado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo10.jpg', 'CA', -12.051000, -77.047000, 1, 3, 2, '2025-12-07 11:00:00', NULL);

-- Reporte CA + Incidente 2 + Clasificación 1 (Riesgo Bajo)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00012', 2, 'Información incorrecta', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo11.jpg', 'CA', -12.051500, -77.047500, 2, 1, 2, '2025-12-07 12:00:00', NULL);

-- Reporte CA + Incidente 3 + Clasificación 2 (Riesgo Medio)
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES ('rep-2025-7-00013', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo12.jpg', 'CA', -12.052000, -77.048000, 3, 2, 2, '2025-12-07 13:00:00', NULL);

-- ============================================
-- REPORTES ADICIONALES PARA COMBINACIONES
-- ============================================

-- Más reportes PE para diversidad
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES 
('rep-2025-7-00014', 2, 'Señalización borrada', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo13.jpg', 'PE', -12.052500, -77.048500, 1, 3, 2, '2025-12-07 14:00:00', NULL),
('rep-2025-7-00015', 2, 'Jardín descuidado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo14.jpg', 'PE', -12.053000, -77.049000, 2, 1, 2, '2025-12-07 15:00:00', NULL);

-- Más reportes EP para diversidad
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES 
('rep-2025-7-00016', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo15.jpg', 'EP', -12.053500, -77.049500, 3, 2, 2, '2025-12-06 17:00:00', NULL),
('rep-2025-7-00017', 2, 'Alcantarilla obstruida', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo16.jpg', 'EP', -12.054000, -77.050000, 1, 1, 2, '2025-12-06 18:00:00', NULL);

-- Más reportes RE para diversidad
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto)
VALUES 
('rep-2025-7-00018', 2, 'Luminaria reparada', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo17.jpg', 'RE', -12.054500, -77.050500, 2, 3, 2, '2025-12-02 08:00:00', '2025-12-03 15:00:00'),
('rep-2025-7-00019', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo18.jpg', 'RE', -12.055000, -77.051000, 3, 1, 2, '2025-12-01 10:00:00', '2025-12-02 14:00:00');



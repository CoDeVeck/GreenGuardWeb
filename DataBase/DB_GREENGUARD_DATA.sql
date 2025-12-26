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
('REPORTE_REGISTRADO'),           -- Cuando se crea el reporte
('CAMBIO_ESTADO_REPORTE'),        -- Cuando cambia a: en proceso, resuelto, cancelado
('PUNTOS_GANADOS'),               -- Cuando ganas puntos (incluye bonus)
('CUPON_CANJEADO'),               -- Cuando canjeas puntos por cupón
('CUPON_POR_VENCER'),             -- Recordatorio antes de vencer (ej: 3 días antes)
('CUPON_VENCIDO'),                -- Cuando el cupón ya venció
('CUPON_USADO');                  -- Cuando usas el cupón en tienda


-- TIPO 1: REPORTE_REGISTRADO (SIN id_reporte porque acababan de registrarse)
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 1, '¡Reporte Registrado!', 'Tu reporte ha sido registrado exitosamente. Lo revisaremos pronto.', NULL, NULL, '2025-12-07 08:30:00', TRUE),
(2, 1, '¡Reporte Registrado!', 'Tu reporte ha sido registrado exitosamente. Lo revisaremos pronto.', NULL, NULL, '2025-12-07 09:15:00', TRUE),
(2, 1, '¡Reporte Registrado!', 'Tu reporte ha sido registrado exitosamente. Lo revisaremos pronto.', NULL, NULL, '2025-12-07 10:00:00', FALSE);

-- TIPO 2: CAMBIO_ESTADO_REPORTE (En Proceso - sin id_reporte específico)
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 2, 'Reporte en Proceso', 'Tu reporte está siendo atendido por las autoridades.', NULL, NULL, '2025-12-06 14:30:00', TRUE),
(2, 2, 'Reporte en Proceso', 'Tu reporte está siendo atendido por las autoridades.', NULL, NULL, '2025-12-06 15:50:00', TRUE);

-- TIPO 2: CAMBIO_ESTADO_REPORTE (Resuelto)
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 2, '¡Reporte Resuelto!', 'Tu reporte ha sido resuelto. ¡Ganaste 10 puntos!', NULL, NULL, '2025-12-06 18:00:00', TRUE),
(2, 2, '¡Reporte Resuelto!', 'Tu reporte ha sido resuelto. ¡Ganaste 20 puntos!', NULL, NULL, '2025-12-05 12:00:00', TRUE),
(2, 2, '¡Reporte Resuelto!', 'Tu reporte ha sido resuelto. ¡Ganaste 30 puntos!', NULL, NULL, '2025-12-04 17:30:00', FALSE);

-- TIPO 3: PUNTOS_GANADOS
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 3, '¡Puntos Ganados!', 'Has ganado 10 puntos por reporte resuelto.', NULL, NULL, '2025-12-06 18:01:00', TRUE),
(2, 3, '¡Puntos Ganados!', 'Has ganado 20 puntos por reporte resuelto.', NULL, NULL, '2025-12-05 12:01:00', TRUE),
(3, 3, '¡Puntos Ganados!', 'Has ganado 50 puntos. ¡Sigue reportando!', NULL, NULL, '2025-12-03 10:00:00', FALSE);

-- TIPO 4: CUPON_CANJEADO
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 4, '¡Cupón Canjeado!', 'Has canjeado 100 puntos por: Descuento 20% Ropa', NULL, 1, '2025-12-05 14:30:00', TRUE),
(3, 4, '¡Cupón Canjeado!', 'Has canjeado 150 puntos por: Descuento 50% Comidas', NULL, 2, '2025-12-04 16:45:00', TRUE),
(5, 4, '¡Cupón Canjeado!', 'Has canjeado 80 puntos por: Descuento 10% Abarrotes', NULL, 3, '2025-12-03 11:20:00', FALSE),
(7, 4, '¡Cupón Canjeado!', 'Has canjeado 200 puntos por: Descuento 15% Electrodomésticos', NULL, 4, '2025-12-02 09:15:00', TRUE);

-- TIPO 5: CUPON_POR_VENCER
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(2, 5, 'Cupón por Vencer', 'Tu cupón Descuento 20% Ropa vence en 3 días. ¡No lo dejes pasar!', NULL, 1, '2025-12-21 08:00:00', FALSE),
(5, 5, 'Cupón por Vencer', 'Tu cupón Descuento 10% Abarrotes vence en 5 días. ¡No lo dejes pasar!', NULL, 3, '2025-12-19 08:00:00', FALSE),
(7, 5, 'Cupón por Vencer', 'Tu cupón Descuento 15% Electrodomésticos vence en 2 días. ¡No lo dejes pasar!', NULL, 4, '2025-12-22 08:00:00', TRUE);

-- TIPO 6: CUPON_VENCIDO
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(9, 6, 'Cupón Vencido', 'Tu cupón Descuento 25% Belleza ha vencido.', NULL, 5, '2025-12-24 00:01:00', FALSE);

-- TIPO 7: CUPON_USADO
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(3, 7, 'Cupón Utilizado', 'Has usado tu cupón Descuento 50% Comidas en Bodega San Miguel. ¡Disfrútalo!', NULL, 2, '2025-12-06 13:30:00', TRUE),
(7, 7, 'Cupón Utilizado', 'Has usado tu cupón Descuento 40% Comidas en Minimarket El Sol. ¡Disfrútalo!', NULL, 8, '2025-12-05 19:45:00', TRUE);

-- Notificaciones adicionales para otros usuarios
INSERT INTO tb_notificacion (id_usuario, id_tipo_notificacion, titulo, mensaje, id_reporte, id_usuario_cupon, fecha_creacion, leida) VALUES
(3, 1, '¡Reporte Registrado!', 'Tu reporte ha sido registrado exitosamente. Lo revisaremos pronto.', NULL, NULL, '2025-12-01 10:30:00', TRUE),
(5, 3, '¡Puntos Ganados!', 'Has ganado 30 puntos por reporte resuelto.', NULL, NULL, '2025-11-28 15:00:00', TRUE),
(7, 2, '¡Reporte Resuelto!', 'Tu reporte ha sido resuelto. ¡Ganaste 20 puntos!', NULL, NULL, '2025-11-25 12:00:00', FALSE);

-- ============================================
-- REPORTES CON ESTADO 'PE' (PENDIENTE)
-- ============================================

-- Reporte PE + Incidente 1 + Clasificación 1 (Riesgo Bajo) + Puntos 10
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00002', 2, 'Basura acumulada en la esquina', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo1.jpg', 'PE', -12.046500, -77.042800, 1, 1, 2, '2025-12-07 08:30:00', NULL, 10);

-- Reporte PE + Incidente 2 + Clasificación 2 (Riesgo Medio) + Puntos 20
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00003', 2, 'Poste de luz dañado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo2.jpg', 'PE', -12.047000, -77.043000, 2, 2, 2, '2025-12-07 09:15:00', NULL, 20);

-- Reporte PE + Incidente 3 + Clasificación 3 (Riesgo Alto) + Puntos 30
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00004', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo3.jpg', 'PE', -12.047500, -77.043500, 3, 3, 2, '2025-12-07 10:00:00', NULL, 30);

-- ============================================
-- REPORTES CON ESTADO 'EP' (EN PROCESO)
-- ============================================

-- Reporte EP + Incidente 1 + Clasificación 2 (Riesgo Medio) + Puntos 20
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00005', 2, 'Árbol caído bloqueando la vía', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo4.jpg', 'EP', -12.048000, -77.044000, 1, 2, 2, '2025-12-06 14:20:00', NULL, 20);

-- Reporte EP + Incidente 2 + Clasificación 3 (Riesgo Alto) + Puntos 30
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00006', 2, 'Fuga de agua en tubería principal', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo5.jpg', 'EP', -12.048500, -77.044500, 2, 3, 2, '2025-12-06 15:45:00', NULL, 30);

-- Reporte EP + Incidente 3 + Clasificación 1 (Riesgo Bajo) + Puntos 10
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00007', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo6.jpg', 'EP', -12.049000, -77.045000, 3, 1, 2, '2025-12-06 16:30:00', NULL, 10);

-- ============================================
-- REPORTES CON ESTADO 'RE' (RESUELTO)
-- ============================================

-- Reporte RE + Incidente 1 + Clasificación 1 (Riesgo Bajo) + Puntos 10
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00008', 2, 'Bache en la pista reparado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteBajo/ejemplo7.jpg', 'RE', -12.049500, -77.045500, 1, 1, 2, '2025-12-05 08:00:00', '2025-12-06 18:00:00', 10);

-- Reporte RE + Incidente 2 + Clasificación 2 (Riesgo Medio) + Puntos 20
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00009', 2, 'Semáforo reparado', 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteMedio/ejemplo8.jpg', 'RE', -12.050000, -77.046000, 2, 2, 2, '2025-12-04 10:30:00', '2025-12-05 12:00:00', 20);

-- Reporte RE + Incidente 3 + Clasificación 3 (Riesgo Alto) + Puntos 30
INSERT INTO tb_reporte (num_report, id_usu, detalle_repo, imagen_repo, estado, latitud, longitud, id_tipo_inci, id_tipo_clasi, id_distrito, repo_registrado, repo_resuelto, puntos_ganados)
VALUES ('rep-2025-7-00010', 2, NULL, 'https://res.cloudinary.com/dvacublsz/image/upload/v1765164374/GreenGuard/IncidenteAlto/ejemplo9.jpg', 'RE', -12.050500, -77.046500, 3, 3, 2, '2025-12-03 09:00:00', '2025-12-04 17:30:00', 30);

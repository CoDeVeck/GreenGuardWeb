
CREATE TABLE tb_rol(
    id_rol SERIAL PRIMARY KEY,
    descripcion VARCHAR(20) NOT NULL
);

CREATE TABLE tb_distrito(
    id_distrito SERIAL PRIMARY KEY,
    desc_distrito VARCHAR(50) NOT NULL
);

CREATE TABLE tb_tipos_incidentes(
    id_tipo_inci SERIAL PRIMARY KEY,
    desc_tipo_inci VARCHAR(30) NOT NULL
    -- BACHES, POSTE CAIDO, VEREDA RAJADO, PISTAS CON HUECOS, OTROS...
);

CREATE TABLE tb_tipo_clasificacion(
    id_tipo_clasi SERIAL PRIMARY KEY,
    desc_tipo_clasi VARCHAR(30) NOT NULL
    -- Riesgo bajo, Riesgo Medio, Riesgo Alto, Riesgo Crítico
);

CREATE TABLE tb_categoria(
    id_cate SERIAL PRIMARY KEY,
    desc_cate VARCHAR(20) NOT NULL
    -- ROPAS, COMIDAS, ABARROTES, ELECTRODOMESTICOS
);

CREATE TABLE tb_usuario(
    id_usu SERIAL PRIMARY KEY,
    nom_usu VARCHAR(30) NOT NULL,
    ape_pat_usu VARCHAR(30) NOT NULL,
    ape_mat_usu VARCHAR(30) NOT NULL,
    documento_usu VARCHAR(10) NOT NULL UNIQUE,
    correo_usu VARCHAR(50) NOT NULL UNIQUE,
	password_usu VARCHAR(150) NOT NULL,
    telefono_usu VARCHAR(20) NOT NULL,
    genero_usu CHAR(1) CHECK (genero_usu IN ('F','M')),
	imagen_usu VARCHAR(150),
    registro_usu DATE DEFAULT CURRENT_DATE,
    puntos_usu INT DEFAULT 0,
    id_rol INT,
    activo BOOLEAN DEFAULT TRUE,
	FCM_TOKEN VARCHAR(500) NULL,
    FCM_TOKEN_FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES tb_rol(id_rol)
);

CREATE TABLE tb_reporte(
    id_reporte SERIAL PRIMARY KEY,
    num_report VARCHAR(50) NOT NULL UNIQUE,
    id_usu INT,
    detalle_repo VARCHAR(150) NOT NULL,
    imagen_repo VARCHAR(120) NULL,
    estado CHAR(2) CHECK (estado IN ('PE','EP','RE','CA')),    -- pendiente, en proceso, resuelto, cancelado
    latitud NUMERIC(10,6) NOT NULL,
    longitud NUMERIC(10,6) NOT NULL,
    id_tipo_inci INT,
    id_tipo_clasi INT,
	id_distrito INT, 
    repo_registrado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    repo_resuelto TIMESTAMP NULL,
	FOREIGN KEY (id_distrito) REFERENCES tb_distrito(id_distrito),
    FOREIGN KEY (id_tipo_inci) REFERENCES tb_tipos_incidentes(id_tipo_inci),
    FOREIGN KEY (id_tipo_clasi) REFERENCES tb_tipo_clasificacion(id_tipo_clasi),
    FOREIGN KEY (id_usu) REFERENCES tb_usuario(id_usu)
);

CREATE TABLE tb_tienda(
    id_tienda SERIAL PRIMARY KEY,
    nom_tienda VARCHAR(50) NOT NULL,
    id_usu INT REFERENCES tb_usuario(id_usu),
    id_distrito INT REFERENCES tb_distrito(id_distrito)
);

CREATE TABLE tb_cupon(
    id_cupon SERIAL PRIMARY KEY,
    nombre_cupon VARCHAR(50) NOT NULL,
	desc_cupon VARCHAR(50) NOT NULL,
    id_cate INT REFERENCES tb_categoria(id_cate),
    cod_cupon VARCHAR(20) NOT NULL,
    puntos_requeridos INT NOT NULL,
    id_tienda INT REFERENCES tb_tienda(id_tienda),
  	fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE tb_usuario_cupon(
    id_usuario_cupon SERIAL PRIMARY KEY,
    id_cupon INT REFERENCES tb_cupon(id_cupon),
    id_usuario INT REFERENCES tb_usuario(id_usu),
	codigo_cupon VARCHAR(20) NOT NULL UNIQUE,
	qr_verification_code VARCHAR(150) NOT NULL UNIQUE,
    fecha_canje TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    canjeado BOOLEAN DEFAULT FALSE,
	fecha_uso TIMESTAMP NULL,
	estado char(2) CHECK (estado in('AC','VE','CA')) DEFAULT 'CA' --ACTIVO, VENCIDO, CANJEADO
);

CREATE TABLE tb_tipo_notificacion(
	id_tipo_notificacion SERIAL PRIMARY KEY,
	descripcion VARCHAR(50) not null
);

CREATE TABLE tb_notificacion(
    id_notificacion SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL REFERENCES tb_usuario(id_usu),
   	id_tipo_notificacion INT REFERENCES tb_tipo_notificacion,
    titulo VARCHAR(100) NOT NULL,
    mensaje VARCHAR(250) NOT NULL,
    id_reporte INT NULL REFERENCES tb_reporte(id_reporte),
    id_usuario_cupon INT NULL REFERENCES tb_usuario_cupon(id_usuario_cupon),
    id_cupon INT NULL 	REFERENCES tb_cupon(id_cupon),
    leida BOOLEAN DEFAULT FALSE,
    descartada BOOLEAN DEFAULT FALSE
);


export interface UserResponse {
  idUsu: number;
  nomUsu: string;
  apePatUsu: string;
  apeMatUsu: string;
  correoUsu: string;
  telefonoUsu: string;
  imagenUsu: string;
  puntosUsu: number;
  rol: {
    idRol: number;
    descripcionRol: string;
  };
  activo: boolean;
}
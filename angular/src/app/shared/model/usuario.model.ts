import { Distrito } from "./distrito.model";
import { Rol } from "./rol.model";

export interface Usuario {
  idUsuario?: number;       
  nombres: string;
  apeMaterno: string;
  apePaterno: string;
  correo: string;
  clave: string;
  nroDocumento: string;
  direccion: string;
  distrito: Distrito;   
  telefono: string;
  genero: String;
  imagen: String;
  rol?: Rol;                
  fechaRegistro?: string;
  puntosUsuario: number;
  fcmToken: String;
  fcmTokenFecha: String;    
  estado?: boolean;
}
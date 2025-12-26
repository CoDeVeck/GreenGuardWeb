import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '@envs/environment'
import { Distrito } from '../../shared/model/distrito.model';
@Injectable({
  providedIn: 'root'
})
export class DistritoService{
 private url = `${environment.api_URL}/distrito`
  constructor(  private http: HttpClient) { }

  listaDistrito():Observable<Distrito[]>{
     return this.http.get<Distrito[]>(`${this.url}/list`)
  }
}
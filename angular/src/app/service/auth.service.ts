import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { AuthResponse } from '../shared/dto/authresponse';
import { LoginRequest } from '../shared/dto/loginrequest';
import { UserResponse } from '../shared/dto/userresponse';
import { environment } from '../../enviroments/environment';
import { TokenService } from '../shared/dto/tokenservice';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.api_URL}/auth`;
  private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient, 
    private tokenService: TokenService,
    private router: Router
  ) {
    const token = this.tokenService.getToken();
    if (token) {
      this.loadCurrentUser().subscribe({
        error: () => {
          this.logout();
        }
      });
    }
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    // Crear FormData o HttpParams porque el backend usa @RequestParam
    const params = new HttpParams()
      .set('correoUsu', request.correoUsu)
      .set('passwordUsu', request.passwordUsu);

    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, null, { params })
      .pipe(
        tap((response) => {
          if (response.token) {
            this.tokenService.setToken(response.token);
            // Cargar datos del usuario después del login
            this.loadCurrentUser().subscribe();
          }
        })
      );
  }



  logout(): void {
    this.tokenService.removeToken();
    this.currentUserSubject.next(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return this.tokenService.getToken();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/me`);
  }

  getCurrentUserValue(): UserResponse | null {
    return this.currentUserSubject.value;
  }

  private loadCurrentUser(): Observable<UserResponse> {
    return this.getCurrentUser().pipe(
      tap((user) => this.currentUserSubject.next(user))
    );
  }
}
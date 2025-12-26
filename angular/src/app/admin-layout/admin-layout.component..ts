import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterLinkWithHref, RouterOutlet,Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { UserResponse } from '../shared/dto/userresponse';

import { UserService } from '../service/user.service';
import { AlertService } from '../util/alert.service';


@Component({
  selector: 'app-admin-layout',
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkWithHref,
    RouterLinkActive
],
  templateUrl: './admin-layout.component.html',
styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent implements OnInit {
  
  currentUser: UserResponse | null = null;
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtener el usuario actual
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    if (confirm('¿Estás seguro de cerrar sesión?')) {
      this.authService.logout();
    }
  }

  // Método helper para obtener el nombre completo
  getUserFullName(): string {
    if (!this.currentUser) {
      return 'Usuario';
    }
    return `${this.currentUser.nomUsu} ${this.currentUser.apePatUsu}`.trim();
  }

  // Método helper para obtener solo el primer nombre
  getUserFirstName(): string {
    if (!this.currentUser) {
      return 'Usuario';
    }
    return this.currentUser.nomUsu || 'Usuario';
  }
}

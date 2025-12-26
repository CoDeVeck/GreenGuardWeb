import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterLinkWithHref, RouterOutlet,Router } from '@angular/router';

import { UserService } from '../service/user.service';
import { AlertService } from '../util/alert.service';

@Component({
  selector: 'app-admin-layout',
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkWithHref
],
  templateUrl: './admin-layout.component.html',
styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent {

  
}

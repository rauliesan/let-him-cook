import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Revela } from '../../shared/revela/revela';

@Component({
  selector: 'app-contacto',
  imports: [CommonModule, Revela],
  templateUrl: './contacto.html',
  styleUrl: './contacto.scss',
})
export class Contacto {}

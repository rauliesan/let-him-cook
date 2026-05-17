import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AdminService,
  ApiResponse, ApiRequest,
  IaModeloResponse, IaModeloRequest,
  LogroAdminResponse, LogroRequest,
  RecompensaAdminResponse, RecompensaRequest,
  TipoComidaResponse, TipoComidaRequest,
  UsuarioAdminResponse, UsuarioRequest,
  RecetaAdminResponse, RecetaRequest,
} from '../../services/admin.service';

type TabId = 'usuarios' | 'recetas' | 'categorias' | 'logros' | 'recompensas' | 'apis' | 'modelos-ia';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.scss',
})
export class Admin implements OnInit {

  tabActiva = signal<TabId>('usuarios');

  /* ── Datos ── */
  usuarios      = signal<UsuarioAdminResponse[]>([]);
  recetas       = signal<RecetaAdminResponse[]>([]);
  categorias    = signal<TipoComidaResponse[]>([]);
  logros        = signal<LogroAdminResponse[]>([]);
  recompensas   = signal<RecompensaAdminResponse[]>([]);
  apis          = signal<ApiResponse[]>([]);
  modelosIa     = signal<IaModeloResponse[]>([]);

  /* ── Estado UI ── */
  cargando = signal(false);
  error    = signal<string | null>(null);
  exito    = signal<string | null>(null);

  /* ── Modal ── */
  modalAbierto  = signal(false);
  modalTitulo   = signal('');
  modalEntidad  = signal<TabId>('usuarios');
  modalModo     = signal<'crear' | 'editar'>('crear');
  modalEditId   = signal<string | null>(null);

  /* ── Campos del formulario modal (genérico) ── */
  form: Record<string, any> = {};

  /* ── Búsqueda ── */
  busqueda = signal('');

  /* Tabs config */
  tabs: { id: TabId; label: string; icono: string }[] = [
    { id: 'usuarios',      label: 'Usuarios',      icono: '👥' },
    { id: 'recetas',       label: 'Recetas',        icono: '🍳' },
    { id: 'categorias',    label: 'Categorías',     icono: '🏷️' },
    { id: 'logros',        label: 'Logros',         icono: '🏆' },
    { id: 'recompensas',   label: 'Recompensas',    icono: '🎁' },
    { id: 'apis',          label: 'APIs',           icono: '🔑' },
    { id: 'modelos-ia',    label: 'Modelos IA',     icono: '🤖' },
  ];

  /* Contadores para las badges */
  contadores = computed(() => ({
    usuarios:      this.usuarios().length,
    recetas:       this.recetas().length,
    categorias:    this.categorias().length,
    logros:        this.logros().length,
    recompensas:   this.recompensas().length,
    apis:          this.apis().length,
    'modelos-ia':  this.modelosIa().length,
  }));

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.cargarTab('usuarios');
  }

  cambiarTab(tab: TabId) {
    this.tabActiva.set(tab);
    this.busqueda.set('');
    this.cargarTab(tab);
  }

  cargarTab(tab: TabId) {
    this.cargando.set(true);
    this.error.set(null);

    const onError = (err: any) => {
      this.error.set(err.error?.mensaje || err.error?.message || 'Error al cargar datos');
      this.cargando.set(false);
    };

    switch (tab) {
      case 'usuarios':
        this.adminService.getUsuarios().subscribe({
          next: d => { this.usuarios.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'recetas':
        this.adminService.getRecetas().subscribe({
          next: d => { this.recetas.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'categorias':
        this.adminService.getTiposComida().subscribe({
          next: d => { this.categorias.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'logros':
        this.adminService.getLogros().subscribe({
          next: d => { this.logros.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'recompensas':
        this.adminService.getRecompensas().subscribe({
          next: d => { this.recompensas.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'apis':
        this.adminService.getApis().subscribe({
          next: d => { this.apis.set(d); this.cargando.set(false); },
          error: onError,
        });
        break;
      case 'modelos-ia':
        this.adminService.getIaModelos().subscribe({
          next: d => { this.modelosIa.set(d); this.cargando.set(false); },
          error: onError,
        });
        // También cargar APIs para el select del formulario
        this.adminService.getApis().subscribe({
          next: d => this.apis.set(d),
        });
        break;
    }
  }

  /* ── Filtrado por búsqueda ── */
  usuariosFiltrados = computed(() => {
    const q = this.busqueda().toLowerCase();
    if (!q) return this.usuarios();
    return this.usuarios().filter(u =>
      u.nombre.toLowerCase().includes(q) || u.email.toLowerCase().includes(q)
    );
  });

  recetasFiltradas = computed(() => {
    const q = this.busqueda().toLowerCase();
    if (!q) return this.recetas();
    return this.recetas().filter(r =>
      r.nombre.toLowerCase().includes(q) || r.usuarioCreadorNombre.toLowerCase().includes(q)
    );
  });

  /* ── Modal: Abrir ── */
  abrirCrear(entidad: TabId) {
    this.modalEntidad.set(entidad);
    this.modalModo.set('crear');
    this.modalEditId.set(null);
    this.form = {};
    this.modalTitulo.set('Crear ' + this.getLabelEntidad(entidad));
    this.modalAbierto.set(true);
  }

  abrirEditar(entidad: TabId, item: any) {
    this.modalEntidad.set(entidad);
    this.modalModo.set('editar');
    this.modalEditId.set(item.id);
    this.modalTitulo.set('Editar ' + this.getLabelEntidad(entidad));

    // Copiar campos al formulario
    switch (entidad) {
      case 'usuarios':
        this.form = { nombre: item.nombre, email: item.email, password: '' };
        break;
      case 'recetas':
        this.form = {
          nombre: item.nombre, descripcion: item.descripcion || '',
          ingredientes: item.ingredientes, instrucciones: item.instrucciones || '',
          tiempoPreparacion: item.tiempoPreparacion, dificultad: item.dificultad || 'MEDIA',
          calorias: item.calorias, alergenos: item.alergenos || '',
          esPublica: item.esPublica, imagenUrl: item.imagenUrl || '',
          tipoComidaId: item.tipoComidaId || '',
        };
        break;
      case 'categorias':
        this.form = { nombre: item.nombre, descripcion: item.descripcion || '', iconoUrl: item.iconoUrl || '' };
        break;
      case 'logros':
        this.form = { nombre: item.nombre, descripcion: item.descripcion || '', iconoUrl: item.iconoUrl || '' };
        break;
      case 'recompensas':
        this.form = { nombre: item.nombre, descripcion: item.descripcion || '', probabilidad: item.probabilidad };
        break;
      case 'apis':
        this.form = { nombreServicio: item.nombreServicio, endpointUrl: item.endpointUrl || '', apiKey: item.apiKey };
        break;
      case 'modelos-ia':
        this.form = { nombreModelo: item.nombreModelo, apiId: item.apiId };
        break;
    }
    this.modalAbierto.set(true);
  }

  cerrarModal() {
    this.modalAbierto.set(false);
  }

  /* ── Modal: Guardar ── */
  guardar() {
    this.cargando.set(true);
    this.error.set(null);
    const entidad = this.modalEntidad();
    const modo = this.modalModo();
    const id = this.modalEditId();

    const onOk = () => {
      this.cargando.set(false);
      this.cerrarModal();
      this.mostrarExito(modo === 'crear' ? 'Creado correctamente' : 'Actualizado correctamente');
      this.cargarTab(entidad);
    };
    const onErr = (err: any) => {
      this.cargando.set(false);
      this.error.set(err.error?.mensaje || err.error?.message || 'Error al guardar');
    };

    switch (entidad) {
      case 'usuarios':
        if (modo === 'crear') {
          this.adminService.crearUsuario(this.form as UsuarioRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarUsuario(id!, this.form as UsuarioRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'recetas':
        if (modo === 'editar') {
          this.adminService.actualizarReceta(id!, this.form as RecetaRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'categorias':
        if (modo === 'crear') {
          this.adminService.crearTipoComida(this.form as TipoComidaRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarTipoComida(id!, this.form as TipoComidaRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'logros':
        if (modo === 'crear') {
          this.adminService.crearLogro(this.form as LogroRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarLogro(id!, this.form as LogroRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'recompensas':
        if (modo === 'crear') {
          this.adminService.crearRecompensa(this.form as RecompensaRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarRecompensa(id!, this.form as RecompensaRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'apis':
        if (modo === 'crear') {
          this.adminService.crearApi(this.form as ApiRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarApi(id!, this.form as ApiRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
      case 'modelos-ia':
        if (modo === 'crear') {
          this.adminService.crearIaModelo(this.form as IaModeloRequest).subscribe({ next: onOk, error: onErr });
        } else {
          this.adminService.actualizarIaModelo(id!, this.form as IaModeloRequest).subscribe({ next: onOk, error: onErr });
        }
        break;
    }
  }

  /* ── Eliminar ── */
  eliminar(entidad: TabId, id: string, nombre: string) {
    if (!confirm(`¿Eliminar "${nombre}"? Esta acción no se puede deshacer.`)) return;

    this.cargando.set(true);
    const onOk = () => {
      this.cargando.set(false);
      this.mostrarExito('Eliminado correctamente');
      this.cargarTab(entidad);
    };
    const onErr = (err: any) => {
      this.cargando.set(false);
      this.error.set(err.error?.mensaje || err.error?.message || 'Error al eliminar');
    };

    switch (entidad) {
      case 'usuarios':      this.adminService.eliminarUsuario(id).subscribe({ next: onOk, error: onErr }); break;
      case 'recetas':       this.adminService.eliminarReceta(id).subscribe({ next: onOk, error: onErr }); break;
      case 'categorias':    this.adminService.eliminarTipoComida(id).subscribe({ next: onOk, error: onErr }); break;
      case 'logros':        this.adminService.eliminarLogro(id).subscribe({ next: onOk, error: onErr }); break;
      case 'recompensas':   this.adminService.eliminarRecompensa(id).subscribe({ next: onOk, error: onErr }); break;
      case 'apis':          this.adminService.eliminarApi(id).subscribe({ next: onOk, error: onErr }); break;
      case 'modelos-ia':    this.adminService.eliminarIaModelo(id).subscribe({ next: onOk, error: onErr }); break;
    }
  }

  /* ── Helpers ── */
  getLabelEntidad(tab: TabId): string {
    return this.tabs.find(t => t.id === tab)?.label ?? tab;
  }

  mostrarExito(msg: string) {
    this.exito.set(msg);
    setTimeout(() => this.exito.set(null), 3000);
  }

  formatFecha(iso: string | null | undefined): string {
    if (!iso) return '—';
    return new Date(iso).toLocaleDateString('es-ES', {
      day: '2-digit', month: 'short', year: 'numeric',
    });
  }

  truncar(texto: string | null, max = 40): string {
    if (!texto) return '—';
    return texto.length > max ? texto.substring(0, max) + '…' : texto;
  }
}

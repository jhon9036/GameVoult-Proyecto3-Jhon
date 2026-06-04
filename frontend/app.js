// En Docker/Cloud Run el frontend y la API comparten origen → URL relativa.
// En local con Live Server (puerto ≠ 8080) → URL absoluta al backend de desarrollo.
const API_URL = (window.location.hostname === 'localhost' && window.location.port !== '8080')
  ? 'http://localhost:8080/api'
  : '/api';

const DEFAULT_API_KEY = "dev-gamevault-key";
const ADMIN_SESSION_KEY = "gamevaultUserSession";
const LEGACY_ADMIN_SESSION_KEY = "gamevaultAdminSession";

function getAdminSession() {
  try {
    const session = JSON.parse(localStorage.getItem(ADMIN_SESSION_KEY));
    if (session?.expiresAt && new Date(session.expiresAt) <= new Date()) {
      localStorage.removeItem(ADMIN_SESSION_KEY);
      return null;
    }
    return session;
  } catch (_) {
    return null;
  }
}

function getApiKey() {
  return localStorage.getItem("gamevaultApiKey") || DEFAULT_API_KEY;
}

function authHeaders() {
  const session = getAdminSession();
  if (session?.token) {
    return { "Authorization": `Bearer ${session.token}` };
  }
  return { "X-API-Key": getApiKey() };
}

function jsonAuthHeaders() {
  return { "Content-Type": "application/json", ...authHeaders() };
}

// ── Notificaciones (toasts) ──
// Reemplazan los fallos silenciosos: toda operación que termina (o falla)
// avisa al usuario en pantalla en vez de no hacer nada.
function notify(mensaje, tipo = "info") {
  let cont = document.getElementById("toast-container");
  if (!cont) {
    cont = document.createElement("div");
    cont.id = "toast-container";
    document.body.appendChild(cont);
  }
  const toast = document.createElement("div");
  toast.className = `toast toast--${tipo}`;
  toast.setAttribute("role", "status");
  toast.textContent = mensaje;
  cont.appendChild(toast);

  // Forzar reflow para que la transición de entrada se aplique.
  requestAnimationFrame(() => toast.classList.add("toast--visible"));

  setTimeout(() => {
    toast.classList.remove("toast--visible");
    toast.addEventListener("transitionend", () => toast.remove(), { once: true });
  }, 3500);
}

// ── Wrapper de fetch ──
// Centraliza el manejo de errores: lanza una excepción con un mensaje legible
// cuando la respuesta no es OK (incluyendo el 401 por API Key) o si falla la red.
async function request(url, options = {}) {
  let res;
  try {
    res = await fetch(url, options);
  } catch (_networkError) {
    throw new Error("No se pudo conectar con el servidor. Verifica tu conexión.");
  }

  if (!res.ok) {
    if (res.status === 401) {
      throw new Error("No autorizado. Inicia sesion con tu usuario.");
    }
    let mensaje = `Error ${res.status} al procesar la solicitud.`;
    try {
      const data = await res.json();
      if (data && data.error) mensaje = data.error;
    } catch (_) { /* la respuesta puede no traer cuerpo JSON */ }
    throw new Error(mensaje);
  }
  return res;
}

const ICON_CONTROLLER = '<svg class="placeholder-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="2" y="7" width="20" height="13" rx="5"/><path d="M8 12h2m-1-1v2M15 13h.01M17 13h.01"/></svg>';

let videojuegos    = [];
let categorias     = [];
let plataformas    = [];
let wishlistItems  = [];
let editandoId     = null;
let detalleJuegoId = null;

// ── Biblioteca ──
const form             = document.getElementById("game-form");
const formSection      = document.getElementById("form-section");
const gamesList        = document.getElementById("games-list");
const emptyMsg         = document.getElementById("empty-msg");
const categoriaSelect  = document.getElementById("categoriaId");
const plataformaSelect = document.getElementById("plataformaId");
const searchInput      = document.getElementById("search-input");
const filterEstado     = document.getElementById("filter-estado");
const filterCat        = document.getElementById("filter-category");
const clearSearchBtn   = document.getElementById("clear-search");
const clearFiltersBtn  = document.getElementById("clear-filters-btn");
const resultsCount     = document.getElementById("results-count");

// ── Vistas ──
const vistaBiblioteca = document.getElementById("vista-biblioteca");
const vistaDetalle    = document.getElementById("vista-detalle");
const vistaWishlist   = document.getElementById("vista-wishlist");

// ── Detalle ──
const detalleImagen      = document.getElementById("detalle-imagen");
const detallePlaceholder = document.getElementById("detalle-placeholder");

// ── Modal reseña ──
const modalResena       = document.getElementById("modal-resena");
const formResena        = document.getElementById("form-resena");
const puntuacionSlider  = document.getElementById("resena-puntuacion");
const puntuacionDisplay = document.getElementById("puntuacion-display");

// ── Preview imagen en formulario ──
const imagenUrlInput           = document.getElementById("imagenUrl");
const imagenPreview            = document.getElementById("imagen-preview");
const imagenPreviewPlaceholder = document.getElementById("imagen-preview-placeholder");

// ── Modal wishlist ──
const modalWishlist  = document.getElementById("modal-wishlist");
const formWishlist   = document.getElementById("form-wishlist");

const loginScreen    = document.getElementById("login-screen");
const loginForm      = document.getElementById("login-form");
const loginError     = document.getElementById("login-error");
const loginTitle     = document.getElementById("login-title");
const loginToggle    = document.getElementById("login-toggle");
const loginRegisterFields = document.getElementById("login-register-fields");
const adminSessionEl = document.getElementById("admin-session");
const adminUserLabel = document.getElementById("admin-user-label");
const logoutBtn      = document.getElementById("btn-logout");

// ── Auth: nuevos elementos (registro profesional) ──
const authSubtitle      = document.getElementById("auth-subtitle");
const authSwitchText    = document.getElementById("auth-switch-text");
const authSubmit        = document.getElementById("auth-submit");
const authSubmitLabel   = authSubmit.querySelector(".auth-submit-label");
const tabLogin          = document.getElementById("tab-login");
const tabRegister       = document.getElementById("tab-register");
const inputUsername     = document.getElementById("login-username");
const inputPassword     = document.getElementById("login-password");
const inputDisplayName  = document.getElementById("login-display-name");
const inputConfirm      = document.getElementById("login-password-confirm");
const confirmField      = document.getElementById("confirm-field");
const togglePasswordBtn = document.getElementById("toggle-password");
const authStrength      = document.getElementById("auth-strength");
const authStrengthLabel = document.getElementById("auth-strength-label");
const authCaps          = document.getElementById("auth-caps");

// Reglas de validación (deben coincidir con el backend AuthController)
const USERNAME_REGEX = /^[A-Za-z0-9_.-]{3,30}$/;
const PASSWORD_MIN = 6;
const PASSWORD_MAX = 80;

let authMode = "login";

// ════════════════════════════════════════════════════════
//  INICIALIZACIÓN
// ════════════════════════════════════════════════════════

document.addEventListener("DOMContentLoaded", async () => {
  localStorage.removeItem(LEGACY_ADMIN_SESSION_KEY);
  configurarLogin();
  if (!getAdminSession()) {
    mostrarLogin();
  } else {
    mostrarAppAutenticada();
    await cargarDatosIniciales();
  }

  // Navegación por tabs
  document.querySelectorAll(".nav-tab").forEach(tab => {
    tab.addEventListener("click", () => cambiarVistaPrincipal(tab.dataset.vista));
  });

  // Pills de estadísticas — filtran la biblioteca al hacer clic
  document.querySelectorAll(".stat-pill").forEach(pill => {
    pill.addEventListener("click", () => {
      document.querySelectorAll(".stat-pill").forEach(p => p.classList.remove("active"));
      pill.classList.add("active");
      filterEstado.value = pill.dataset.estado;
      renderizar();
    });
  });

  // Formulario toggle
  document.getElementById("btn-abrir-formulario").addEventListener("click", abrirFormulario);
  document.getElementById("btn-exportar-csv").addEventListener("click", exportarCsv);
  document.getElementById("reload-btn").addEventListener("click", cargarVideojuegos);
  document.getElementById("cancel-btn").addEventListener("click", () => {
    if (editandoId && !confirm("\u00BFDescartar los cambios de edici\u00F3n?")) return;
    resetForm();
  });

  // Preview de imagen en tiempo real
  imagenUrlInput.addEventListener("input", actualizarPreviewImagen);

  searchInput.addEventListener("input", () => {
    clearSearchBtn.style.display = searchInput.value ? "block" : "none";
    renderizar();
  });
  clearSearchBtn.addEventListener("click", () => {
    searchInput.value = "";
    clearSearchBtn.style.display = "none";
    renderizar();
  });
  filterEstado.addEventListener("change", () => {
    sincronizarPillActiva(filterEstado.value);
    renderizar();
  });
  filterCat.addEventListener("change", renderizar);
  clearFiltersBtn.addEventListener("click", limpiarFiltros);

  // Detalle
  document.getElementById("btn-volver").addEventListener("click", mostrarBiblioteca);
  document.getElementById("detalle-btn-editar").addEventListener("click", () => {
    mostrarBiblioteca();
    editar(detalleJuegoId);
  });
  document.getElementById("detalle-btn-eliminar").addEventListener("click", async () => {
    if (!confirm("\u00BFEliminar este juego?")) return;
    try {
      await request(`${API_URL}/videojuegos/${detalleJuegoId}`, { method: "DELETE", headers: authHeaders() });
    } catch (e) {
      notify(`No se pudo eliminar el juego: ${e.message}`, "error");
      return;
    }
    notify("Juego eliminado.", "success");
    mostrarBiblioteca();
    await cargarVideojuegos();
  });

  // Modal reseña
  document.getElementById("btn-abrir-modal-resena").addEventListener("click", () => {
    formResena.reset();
    puntuacionDisplay.textContent = "5";
    modalResena.style.display = "flex";
  });
  document.getElementById("btn-cerrar-modal").addEventListener("click", () => {
    modalResena.style.display = "none";
  });
  modalResena.addEventListener("click", (e) => {
    if (e.target === modalResena) modalResena.style.display = "none";
  });
  puntuacionSlider.addEventListener("input", () => {
    puntuacionDisplay.textContent = puntuacionSlider.value;
  });
  formResena.addEventListener("submit", guardarResena);

  // Modal wishlist
  document.getElementById("btn-abrir-modal-wishlist").addEventListener("click", () => {
    formWishlist.reset();
    document.getElementById("wl-error-titulo").textContent = "";
    document.querySelector('input[name="wl-prioridad"][value="MEDIA"]').checked = true;
    modalWishlist.style.display = "flex";
  });
  document.getElementById("btn-cerrar-modal-wl").addEventListener("click", () => {
    modalWishlist.style.display = "none";
  });
  modalWishlist.addEventListener("click", (e) => {
    if (e.target === modalWishlist) modalWishlist.style.display = "none";
  });
  formWishlist.addEventListener("submit", guardarWishlistItem);

  document.getElementById("wl-filter-prioridad").addEventListener("change", renderizarWishlist);

  form.addEventListener("submit", guardarVideojuego);
});

async function cargarDatosIniciales() {
  await Promise.all([cargarCategorias(), cargarPlataformas()]);
  await cargarVideojuegos();
  await cargarEstadisticas();
}

function configurarLogin() {
  loginForm.addEventListener("submit", enviarFormularioAuth);
  loginToggle.addEventListener("click", alternarModoAuth);
  tabLogin.addEventListener("click", () => cambiarModoAuth("login"));
  tabRegister.addEventListener("click", () => cambiarModoAuth("register"));
  logoutBtn.addEventListener("click", cerrarSesionAdmin);

  // Mostrar / ocultar contraseña
  togglePasswordBtn.addEventListener("click", alternarVisibilidadPassword);

  // Validación en vivo (solo limpia errores y refresca la fuerza al escribir)
  inputUsername.addEventListener("input", () => limpiarErrorCampo(inputUsername));
  inputDisplayName.addEventListener("input", () => limpiarErrorCampo(inputDisplayName));
  inputConfirm.addEventListener("input", () => limpiarErrorCampo(inputConfirm));
  inputPassword.addEventListener("input", () => {
    limpiarErrorCampo(inputPassword);
    if (authMode === "register") actualizarFuerzaPassword();
  });

  // Aviso de Bloq Mayús en los campos de contraseña
  [inputPassword, inputConfirm].forEach(el => {
    el.addEventListener("keyup", detectarBloqMayus);
    el.addEventListener("blur", () => authCaps.hidden = true);
  });

  actualizarModoAuth();
}

function detectarBloqMayus(e) {
  const activo = typeof e.getModifierState === "function" && e.getModifierState("CapsLock");
  authCaps.hidden = !activo;
}

function mostrarLogin() {
  document.body.classList.add("auth-locked");
  loginScreen.style.display = "grid";
  adminSessionEl.style.display = "none";
  loginError.textContent = "";
  authCaps.hidden = true;
  ocultarPassword();
  [inputUsername, inputPassword, inputDisplayName, inputConfirm].forEach(limpiarErrorCampo);
}

function mostrarAppAutenticada() {
  const session = getAdminSession();
  document.body.classList.remove("auth-locked");
  loginScreen.style.display = "none";
  adminSessionEl.style.display = "flex";
  const role = session?.role === "ADMIN" ? "Admin" : "Usuario";
  const name = session?.displayName || session?.username || "Usuario";
  adminUserLabel.textContent = `${role}: ${name}`;
}

function alternarModoAuth() {
  cambiarModoAuth(authMode === "login" ? "register" : "login");
}

function cambiarModoAuth(modo) {
  if (modo === authMode) return;
  authMode = modo;
  loginError.textContent = "";
  authCaps.hidden = true;
  ocultarPassword();
  [inputUsername, inputPassword, inputDisplayName, inputConfirm].forEach(limpiarErrorCampo);
  actualizarModoAuth();

  // Lleva el foco al primer campo relevante del nuevo modo.
  const primero = modo === "register" ? inputDisplayName : inputUsername;
  requestAnimationFrame(() => primero.focus());
}

function actualizarModoAuth() {
  const creandoCuenta = authMode === "register";

  loginTitle.textContent   = creandoCuenta ? "Crea tu cuenta" : "Bienvenido de nuevo";
  authSubtitle.textContent = creandoCuenta
    ? "Empieza a organizar tu colección en segundos."
    : "Inicia sesión para acceder a tu biblioteca.";

  tabLogin.classList.toggle("active", !creandoCuenta);
  tabRegister.classList.toggle("active", creandoCuenta);
  tabLogin.setAttribute("aria-selected", String(!creandoCuenta));
  tabRegister.setAttribute("aria-selected", String(creandoCuenta));

  loginRegisterFields.style.display = creandoCuenta ? "block" : "none";
  confirmField.style.display        = creandoCuenta ? "block" : "none";
  authStrength.style.display        = creandoCuenta ? "block" : "none";

  authSubmitLabel.textContent = creandoCuenta ? "Crear cuenta" : "Iniciar sesión";
  authSwitchText.textContent  = creandoCuenta ? "¿Ya tienes una cuenta?" : "¿No tienes cuenta todavía?";
  loginToggle.textContent     = creandoCuenta ? "Inicia sesión" : "Créala gratis";

  inputPassword.autocomplete = creandoCuenta ? "new-password" : "current-password";
  if (creandoCuenta) actualizarFuerzaPassword();
}

function aplicarVisibilidadPassword(mostrar) {
  const tipo = mostrar ? "text" : "password";
  inputPassword.type = tipo;
  inputConfirm.type = tipo;
  togglePasswordBtn.setAttribute("aria-label", mostrar ? "Ocultar contraseña" : "Mostrar contraseña");
  togglePasswordBtn.querySelector(".auth-eye-show").style.display = mostrar ? "none" : "block";
  togglePasswordBtn.querySelector(".auth-eye-hide").style.display = mostrar ? "block" : "none";
}

function alternarVisibilidadPassword() {
  aplicarVisibilidadPassword(inputPassword.type === "password");
}

function ocultarPassword() {
  aplicarVisibilidadPassword(false);
}

// Puntuación 0-4 según longitud y variedad de caracteres.
function calcularFuerzaPassword(pwd) {
  if (!pwd) return 0;
  let score = 0;
  if (pwd.length >= PASSWORD_MIN) score++;
  if (pwd.length >= 10) score++;
  if (/[a-z]/.test(pwd) && /[A-Z0-9]/.test(pwd)) score++;
  if (/[^A-Za-z0-9]/.test(pwd)) score++;
  return Math.min(score, 4);
}

function actualizarFuerzaPassword() {
  const pwd = inputPassword.value;
  const corta = pwd.length > 0 && pwd.length < PASSWORD_MIN;
  const score = corta ? 1 : calcularFuerzaPassword(pwd);

  const niveles = [
    { label: "Mínimo 6 caracteres", color: "var(--text-3)" },
    { label: corta ? "Demasiado corta" : "Débil", color: "var(--red)" },
    { label: "Aceptable", color: "var(--amber)" },
    { label: "Buena", color: "var(--green)" },
    { label: "Fuerte", color: "var(--accent)" },
  ];

  const nivel = niveles[pwd.length === 0 ? 0 : score];
  authStrength.dataset.score = pwd.length === 0 ? "0" : String(score);
  authStrength.style.setProperty("--strength-color", nivel.color);
  authStrengthLabel.textContent = nivel.label;
  authStrengthLabel.style.color = nivel.color;
}

function mostrarErrorCampo(input, mensaje) {
  const field = input.closest(".auth-field");
  if (!field) return;
  field.classList.add("has-error");
  field.classList.remove("is-valid");
  const span = field.querySelector(".auth-field-error");
  if (span) span.textContent = mensaje;
  input.setAttribute("aria-invalid", "true");
}

function limpiarErrorCampo(input) {
  const field = input.closest(".auth-field");
  if (!field) return;
  field.classList.remove("has-error");
  const span = field.querySelector(".auth-field-error");
  if (span) span.textContent = "";
  input.removeAttribute("aria-invalid");
}

// Valida en cliente antes de llamar al backend; devuelve true si todo está OK.
function validarFormularioAuth(username, password, displayName, confirm) {
  let ok = true;

  if (!username) {
    mostrarErrorCampo(inputUsername, "Escribe tu nombre de usuario.");
    ok = false;
  } else if (authMode === "register" && !USERNAME_REGEX.test(username)) {
    mostrarErrorCampo(inputUsername, "3 a 30 caracteres: letras, números, . _ -");
    ok = false;
  }

  if (!password) {
    mostrarErrorCampo(inputPassword, "Escribe tu contraseña.");
    ok = false;
  } else if (authMode === "register" && (password.length < PASSWORD_MIN || password.length > PASSWORD_MAX)) {
    mostrarErrorCampo(inputPassword, `Debe tener entre ${PASSWORD_MIN} y ${PASSWORD_MAX} caracteres.`);
    ok = false;
  }

  if (authMode === "register") {
    if (!confirm) {
      mostrarErrorCampo(inputConfirm, "Repite la contraseña.");
      ok = false;
    } else if (confirm !== password) {
      mostrarErrorCampo(inputConfirm, "Las contraseñas no coinciden.");
      ok = false;
    }
  }

  return ok;
}

function setAuthCargando(cargando) {
  authSubmit.disabled = cargando;
  authSubmit.classList.toggle("is-loading", cargando);
  [tabLogin, tabRegister, loginToggle].forEach(el => { el.disabled = cargando; });
}

async function enviarFormularioAuth(e) {
  e.preventDefault();
  loginError.textContent = "";

  const username = inputUsername.value.trim();
  const password = inputPassword.value;
  const displayName = inputDisplayName.value.trim();
  const confirm = inputConfirm.value;

  if (!validarFormularioAuth(username, password, displayName, confirm)) {
    return;
  }

  const endpoint = authMode === "register" ? "register" : "login";
  const payload = authMode === "register"
    ? { username, password, displayName }
    : { username, password };

  setAuthCargando(true);
  try {
    const res = await request(`${API_URL}/auth/${endpoint}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    const session = await res.json();
    localStorage.setItem(ADMIN_SESSION_KEY, JSON.stringify(session));
    loginForm.reset();
    mostrarAppAutenticada();
    notify(authMode === "register" ? "¡Cuenta creada! Bienvenido a GameVault." : "Sesión iniciada.", "success");
    await cargarDatosIniciales();
  } catch (err) {
    // Si el backend indica usuario duplicado, lo marcamos en el campo.
    if (/ya existe/i.test(err.message)) {
      mostrarErrorCampo(inputUsername, "Ese usuario ya está en uso.");
    } else {
      loginError.textContent = err.message;
    }
  } finally {
    setAuthCargando(false);
  }
}

function cerrarSesionAdmin() {
  localStorage.removeItem(ADMIN_SESSION_KEY);
  mostrarLogin();
  notify("Sesion cerrada.", "info");
}

// ════════════════════════════════════════════════════════
//  NAVEGACIÓN
// ════════════════════════════════════════════════════════

function cambiarVistaPrincipal(vista) {
  document.querySelectorAll(".nav-tab").forEach(t => t.classList.remove("active"));
  document.querySelector(`[data-vista="${vista}"]`).classList.add("active");

  vistaBiblioteca.style.display = vista === "biblioteca" ? "block" : "none";
  vistaDetalle.style.display    = "none";
  vistaWishlist.style.display   = vista === "wishlist"   ? "block" : "none";

  if (vista === "wishlist") cargarWishlist();
}

// ════════════════════════════════════════════════════════
//  CARGA DE DATOS
// ════════════════════════════════════════════════════════

async function cargarCategorias() {
  let res;
  try {
    res = await request(`${API_URL}/categorias`);
  } catch (e) {
    notify(`No se pudieron cargar las categorías: ${e.message}`, "error");
    return;
  }
  categorias = await res.json();

  const wlCatSelect = document.getElementById("wl-categoriaId");
  categoriaSelect.innerHTML = '<option value="">Sin categoría</option>';
  filterCat.innerHTML       = '<option value="">Todas las categorías</option>';
  wlCatSelect.innerHTML     = '<option value="">Sin categoría</option>';
  categorias.forEach(c => {
    const opt = `<option value="${c.id}">${c.nombre}</option>`;
    categoriaSelect.innerHTML += opt;
    filterCat.innerHTML       += opt;
    wlCatSelect.innerHTML     += opt;
  });
}

async function cargarPlataformas() {
  let res;
  try {
    res = await request(`${API_URL}/plataformas`);
  } catch (e) {
    notify(`No se pudieron cargar las plataformas: ${e.message}`, "error");
    return;
  }
  plataformas = await res.json();

  const wlPlatSelect = document.getElementById("wl-plataformaId");
  plataformaSelect.innerHTML = '<option value="">Sin plataforma</option>';
  wlPlatSelect.innerHTML     = '<option value="">Sin plataforma</option>';
  plataformas.forEach(p => {
    const opt = `<option value="${p.id}">${p.nombre}</option>`;
    plataformaSelect.innerHTML += opt;
    wlPlatSelect.innerHTML     += opt;
  });
}

async function cargarVideojuegos() {
  let res;
  try {
    res = await request(`${API_URL}/videojuegos`, { headers: authHeaders() });
  } catch (e) {
    gamesList.innerHTML = `<p style="color:#f87171;padding:20px;text-align:center">
      ${e.message} Intenta recargar la página.</p>`;
    return;
  }
  videojuegos = await res.json();
  renderizar();
  await cargarEstadisticas();
}

async function cargarEstadisticas() {
  let res;
  try {
    res = await request(`${API_URL}/videojuegos/estadisticas`, { headers: authHeaders() });
  } catch (_) {
    return; // las estadísticas son secundarias: si fallan, no interrumpimos la vista
  }
  const stats = await res.json();

  const claves = ["TOTAL", "PENDIENTE", "JUGANDO", "TERMINADO", "FAVORITO"];
  claves.forEach(k => {
    const el = document.getElementById(`num-${k}`);
    if (el) el.textContent = stats[k] ?? 0;
  });
}

async function exportarCsv() {
  const params = new URLSearchParams();
  const titulo = searchInput.value.trim();

  if (titulo) params.set("titulo", titulo);
  if (filterEstado.value) params.set("estado", filterEstado.value);
  if (filterCat.value) params.set("categoriaId", filterCat.value);

  const query = params.toString();
  const url = `${API_URL}/videojuegos/export/csv${query ? `?${query}` : ""}`;

  // El endpoint requiere sesión de usuario, así que se descarga vía fetch con el token.
  // Una navegación con window.location no puede enviar la cabecera Authorization.
  try {
    const res = await request(url, { headers: authHeaders() });
    const blob = await res.blob();
    const enlace = document.createElement("a");
    enlace.href = URL.createObjectURL(blob);
    enlace.download = "videojuegos.csv";
    document.body.appendChild(enlace);
    enlace.click();
    enlace.remove();
    URL.revokeObjectURL(enlace.href);
  } catch (e) {
    notify(`No se pudo exportar el CSV: ${e.message}`, "error");
  }
}

// ════════════════════════════════════════════════════════
//  RENDERIZADO BIBLIOTECA
// ════════════════════════════════════════════════════════

function renderizar() {
  const q      = searchInput.value.toLowerCase().trim();
  const estado = filterEstado.value;
  const catId  = filterCat.value;
  const hayFiltros = q || estado || catId;

  const lista = videojuegos.filter(j => {
    const matchTitulo = !q     || j.titulo.toLowerCase().includes(q);
    const matchEstado = !estado || j.estado === estado;
    const matchCat    = !catId  || String(j.categoria?.id) === catId;
    return matchTitulo && matchEstado && matchCat;
  });

  resultsCount.textContent = hayFiltros
    ? `(${lista.length} de ${videojuegos.length})`
    : `(${videojuegos.length})`;

  clearFiltersBtn.style.display = hayFiltros ? "inline-block" : "none";

  gamesList.innerHTML = "";

  if (lista.length === 0) {
    if (videojuegos.length === 0) {
      gamesList.innerHTML = `
        <div style="text-align:center;padding:56px 20px;grid-column:1/-1">
          <div style="margin-bottom:16px;display:flex;justify-content:center;color:var(--text-3)">${ICON_CONTROLLER.replace('class="placeholder-icon"','style="width:48px;height:48px;opacity:0.28"')}</div>
          <p style="font-size:1rem;font-weight:700;color:var(--text-2);margin-bottom:6px">Tu biblioteca est\u00E1 vac\u00EDa</p>
          <p style="font-size:13px;color:var(--text-3)">Agrega tu primer videojuego con el bot\u00F3n de arriba.</p>
        </div>`;
    } else {
      emptyMsg.style.display = "block";
    }
    return;
  }

  emptyMsg.style.display = "none";

  lista.forEach(j => {
    const card = document.createElement("div");
    card.className = "game-card";

    const portada = j.imagenUrl
      ? `<img class="card-cover" src="${j.imagenUrl}" alt="${j.titulo}"
             onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">
         <div class="card-cover-placeholder" style="display:none">${ICON_CONTROLLER}</div>`
      : `<div class="card-cover-placeholder">${ICON_CONTROLLER}</div>`;

    card.innerHTML = `
      ${portada}
      <div class="card-body">
        <div class="card-title" title="${j.titulo}">${j.titulo}</div>
        <div class="card-meta">${j.plataforma?.nombre || '\u2014'} \u00B7 ${j.anio || ''}</div>
        <div class="card-meta">${j.categoria?.nombre || '\u2014'}</div>
        <span class="estado-badge estado-${j.estado}">${estadoLabel(j.estado)}</span>
        <div class="card-actions">
          <button class="edit-btn"   onclick="event.stopPropagation();editar(${j.id})">Editar</button>
          <button class="delete-btn" onclick="event.stopPropagation();eliminar(${j.id})">Eliminar</button>
        </div>
      </div>`;

    card.addEventListener("click", () => verDetalle(j.id));
    gamesList.appendChild(card);
  });
}

function estadoLabel(estado) {
  const labels = { PENDIENTE: "Pendiente", JUGANDO: "Jugando", TERMINADO: "Terminado", FAVORITO: "Favorito" };
  return labels[estado] || estado;
}

// ════════════════════════════════════════════════════════
//  VISTA DETALLE
// ════════════════════════════════════════════════════════

async function verDetalle(id) {
  detalleJuegoId = id;
  const j = videojuegos.find(v => v.id === id);
  if (!j) return;

  // Imagen o placeholder
  if (j.imagenUrl) {
    detalleImagen.src = j.imagenUrl;
    detalleImagen.alt = j.titulo;
    detalleImagen.style.display = "block";
    detallePlaceholder.style.display = "none";
    detalleImagen.onerror = () => {
      detalleImagen.style.display = "none";
      detallePlaceholder.style.display = "flex";
    };
  } else {
    detalleImagen.style.display = "none";
    detallePlaceholder.style.display = "flex";
  }

  document.getElementById("detalle-titulo").textContent    = j.titulo;
  document.getElementById("detalle-anio").textContent      = j.anio || "\u2014";
  document.getElementById("detalle-categoria").textContent = j.categoria?.nombre  || "\u2014";
  document.getElementById("detalle-plataforma").textContent= j.plataforma?.nombre || "\u2014";
  document.getElementById("detalle-descripcion").textContent = j.descripcion || "";

  const badge = document.getElementById("detalle-estado");
  badge.textContent  = estadoLabel(j.estado);
  badge.className    = `estado-badge estado-${j.estado}`;

  vistaBiblioteca.style.display = "none";
  vistaDetalle.style.display    = "block";
  window.scrollTo({ top: 0, behavior: "smooth" });

  await cargarResenas(id);
}

function mostrarBiblioteca() {
  vistaDetalle.style.display    = "none";
  vistaBiblioteca.style.display = "block";
  detalleJuegoId = null;
}

// ════════════════════════════════════════════════════════
//  RESEÑAS
// ════════════════════════════════════════════════════════

async function cargarResenas(videojuegoId) {
  const lista    = document.getElementById("resenas-lista");
  const emptyRes = document.getElementById("resenas-empty");
  lista.innerHTML = '<p style="color:#64748b;font-size:13px">Cargando rese\u00F1as...</p>';

  let res;
  try {
    res = await request(`${API_URL}/resenas/videojuego/${videojuegoId}`, { headers: authHeaders() });
  } catch (e) {
    lista.innerHTML = `<p style="color:#f87171;font-size:13px">No se pudieron cargar las rese\u00F1as: ${e.message}</p>`;
    return;
  }
  const resenas = await res.json();

  lista.innerHTML = "";

  if (resenas.length === 0) {
    emptyRes.style.display = "block";
    document.getElementById("detalle-promedio").textContent = "\u2014";
    return;
  }

  emptyRes.style.display = "none";

  const promedio = (resenas.reduce((s, r) => s + r.puntuacion, 0) / resenas.length).toFixed(1);
  document.getElementById("detalle-promedio").textContent = `${promedio} \u2605`;

  resenas.forEach(r => {
    const div = document.createElement("div");
    div.className = "resena-card";
    div.innerHTML = `
      <div class="resena-score">${r.puntuacion}</div>
      <div class="resena-body">
        <div class="resena-autor">${r.autor || "An\u00F3nimo"}</div>
        <div class="resena-comentario">${r.comentario || ""}</div>
      </div>
      <button class="resena-delete" title="Eliminar rese\u00F1a" onclick="eliminarResena(${r.id})">&times;</button>`;
    lista.appendChild(div);
  });
}

async function guardarResena(e) {
  e.preventDefault();
  const data = {
    autor:        document.getElementById("resena-autor").value.trim(),
    puntuacion:   Number(puntuacionSlider.value),
    comentario:   document.getElementById("resena-comentario").value.trim() || null,
    videojuegoId: detalleJuegoId,
  };

  try {
    await request(`${API_URL}/resenas`, {
      method: "POST",
      headers: jsonAuthHeaders(),
      body: JSON.stringify(data),
    });
  } catch (err) {
    notify(`No se pudo guardar la reseña: ${err.message}`, "error");
    return;
  }
  modalResena.style.display = "none";
  notify("Reseña publicada.", "success");
  await cargarResenas(detalleJuegoId);
}

async function eliminarResena(id) {
  if (!confirm("\u00BFEliminar esta rese\u00F1a?")) return;
  try {
    await request(`${API_URL}/resenas/${id}`, { method: "DELETE", headers: authHeaders() });
  } catch (e) {
    notify(`No se pudo eliminar la rese\u00F1a: ${e.message}`, "error");
    return;
  }
  await cargarResenas(detalleJuegoId);
}

// ════════════════════════════════════════════════════════
//  CRUD VIDEOJUEGO (formulario)
// ════════════════════════════════════════════════════════

// ════════════════════════════════════════════════════════
//  FORMULARIO: ABRIR / CERRAR / VALIDAR
// ════════════════════════════════════════════════════════

function abrirFormulario() {
  formSection.style.display = "block";
  document.getElementById("btn-abrir-formulario").style.display = "none";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function cerrarFormulario() {
  formSection.style.display = "none";
  document.getElementById("btn-abrir-formulario").style.display = "inline-block";
}

function actualizarPreviewImagen() {
  const url = imagenUrlInput.value.trim();
  if (url) {
    imagenPreview.src = url;
    imagenPreview.style.display = "block";
    imagenPreviewPlaceholder.style.display = "none";
    imagenPreview.onerror = () => {
      imagenPreview.style.display = "none";
      imagenPreviewPlaceholder.style.display = "flex";
    };
  } else {
    imagenPreview.style.display = "none";
    imagenPreviewPlaceholder.style.display = "flex";
  }
}

function validarFormulario() {
  let valido = true;

  const titulo = document.getElementById("titulo").value.trim();
  const anio   = document.getElementById("anio").value;
  const errorTitulo = document.getElementById("error-titulo");
  const errorAnio   = document.getElementById("error-anio");
  const grupoTitulo = document.getElementById("titulo").closest(".field-group");
  const grupoAnio   = document.getElementById("anio").closest(".field-group");

  if (!titulo) {
    errorTitulo.textContent = "El t\u00EDtulo es obligatorio.";
    grupoTitulo.classList.add("has-error");
    valido = false;
  } else {
    errorTitulo.textContent = "";
    grupoTitulo.classList.remove("has-error");
  }

  if (!anio) {
    errorAnio.textContent = "El a\u00F1o es obligatorio.";
    grupoAnio.classList.add("has-error");
    valido = false;
  } else if (Number(anio) < 1970 || Number(anio) > 2100) {
    errorAnio.textContent = "El a\u00F1o debe estar entre 1970 y 2100.";
    grupoAnio.classList.add("has-error");
    valido = false;
  } else {
    errorAnio.textContent = "";
    grupoAnio.classList.remove("has-error");
  }

  return valido;
}

async function guardarVideojuego(e) {
  e.preventDefault();
  if (!validarFormulario()) return;

  const estadoRadio = document.querySelector('input[name="estado"]:checked');
  const catId       = categoriaSelect.value;
  const platId      = plataformaSelect.value;

  const data = {
    titulo:      document.getElementById("titulo").value.trim(),
    anio:        Number(document.getElementById("anio").value),
    descripcion: document.getElementById("descripcion").value.trim() || null,
    imagenUrl:   imagenUrlInput.value.trim() || null,
    estado:      estadoRadio?.value || "PENDIENTE",
    categoria:   catId  ? { id: Number(catId)  } : null,
    plataforma:  platId ? { id: Number(platId) } : null,
  };

  const url    = editandoId ? `${API_URL}/videojuegos/${editandoId}` : `${API_URL}/videojuegos`;
  const method = editandoId ? "PUT" : "POST";

  const msg = document.getElementById("message");
  const eraEdicion = editandoId !== null;

  try {
    await request(url, {
      method,
      headers: jsonAuthHeaders(),
      body: JSON.stringify(data),
    });
  } catch (err) {
    msg.style.color = "#f87171";
    msg.textContent = err.message;
    return;
  }

  msg.style.color = "#4ade80";
  msg.textContent = eraEdicion ? "Juego actualizado." : "Juego agregado.";
  resetForm();
  await cargarVideojuegos();
  setTimeout(() => { msg.textContent = ""; }, 3000);
}

function editar(id) {
  const j = videojuegos.find(v => v.id === id);
  if (!j) return;

  document.getElementById("form-title").textContent    = "Editar videojuego";
  document.getElementById("titulo").value              = j.titulo;
  document.getElementById("anio").value                = j.anio   || "";
  document.getElementById("descripcion").value         = j.descripcion || "";
  imagenUrlInput.value                                 = j.imagenUrl   || "";
  categoriaSelect.value  = j.categoria?.id  || "";
  plataformaSelect.value = j.plataforma?.id || "";

  const radio = document.querySelector(`input[name="estado"][value="${j.estado}"]`);
  if (radio) radio.checked = true;

  // Modo edición visual
  formSection.classList.add("editing");
  document.getElementById("form-mode-badge").style.display = "inline-block";
  document.getElementById("btn-submit-form").textContent = "Guardar cambios";

  actualizarPreviewImagen();

  editandoId = id;
  abrirFormulario();
}

async function eliminar(id) {
  if (!confirm("\u00BFEliminar este juego?")) return;
  try {
    await request(`${API_URL}/videojuegos/${id}`, { method: "DELETE", headers: authHeaders() });
  } catch (e) {
    notify(`No se pudo eliminar el juego: ${e.message}`, "error");
    return;
  }
  notify("Juego eliminado.", "success");
  await cargarVideojuegos();
}

function resetForm() {
  form.reset();
  document.getElementById("form-title").textContent          = "Agregar videojuego";
  document.getElementById("btn-submit-form").textContent     = "Guardar";
  document.getElementById("form-mode-badge").style.display   = "none";
  document.getElementById("error-titulo").textContent        = "";
  document.getElementById("error-anio").textContent          = "";
  document.getElementById("titulo").closest(".field-group").classList.remove("has-error");
  document.getElementById("anio").closest(".field-group").classList.remove("has-error");
  document.querySelector('input[name="estado"][value="PENDIENTE"]').checked = true;
  formSection.classList.remove("editing");
  imagenPreview.style.display = "none";
  imagenPreviewPlaceholder.style.display = "flex";
  editandoId = null;
  cerrarFormulario();
}

function limpiarFiltros() {
  searchInput.value  = "";
  filterEstado.value = "";
  filterCat.value    = "";
  clearSearchBtn.style.display = "none";
  sincronizarPillActiva("");
  renderizar();
}

function sincronizarPillActiva(estado) {
  document.querySelectorAll(".stat-pill").forEach(p => {
    p.classList.toggle("active", p.dataset.estado === estado);
  });
}

// ════════════════════════════════════════════════════════
//  WISHLIST
// ════════════════════════════════════════════════════════

async function cargarWishlist() {
  let res;
  try {
    res = await request(`${API_URL}/wishlist`, { headers: authHeaders() });
  } catch (e) {
    notify(`No se pudo cargar la wishlist: ${e.message}`, "error");
    return;
  }
  wishlistItems = await res.json();
  renderizarWishlist();
}

function renderizarWishlist() {
  const filtroPrioridad = document.getElementById("wl-filter-prioridad").value;
  const lista = filtroPrioridad
    ? wishlistItems.filter(w => w.prioridad === filtroPrioridad)
    : wishlistItems;

  const grid  = document.getElementById("wishlist-list");
  const empty = document.getElementById("wishlist-empty");
  const count = document.getElementById("wishlist-count");

  count.textContent = filtroPrioridad
    ? `(${lista.length} de ${wishlistItems.length})`
    : `(${wishlistItems.length})`;

  grid.innerHTML = "";
  empty.style.display = lista.length === 0 ? "block" : "none";

  lista.forEach(w => {
    const div = document.createElement("div");
    div.className = `wl-card prioridad-${w.prioridad}`;

    const plat = w.plataforma?.nombre || w.plataformaNombre || "\u2014";
    const cat  = w.categoria?.nombre  || w.categoriaNombre  || "\u2014";
    const prioLabel = { ALTA: "Alta", MEDIA: "Media", BAJA: "Baja" };

    div.innerHTML = `
      <div class="wl-card-header">
        <div class="wl-titulo">${w.titulo}</div>
        <button class="wl-delete" title="Eliminar" onclick="eliminarWishlistItem(${w.id})">&times;</button>
      </div>
      <span class="wl-badge prioridad-badge-${w.prioridad}">${prioLabel[w.prioridad] || w.prioridad}</span>
      <div class="wl-meta">${plat} \u00B7 ${cat}</div>
      ${w.notas ? `<div class="wl-notas">"${w.notas}"</div>` : ""}
    `;
    grid.appendChild(div);
  });
}

async function guardarWishlistItem(e) {
  e.preventDefault();

  const titulo = document.getElementById("wl-titulo").value.trim();
  const errorEl = document.getElementById("wl-error-titulo");

  if (!titulo) {
    errorEl.textContent = "El t\u00EDtulo es obligatorio.";
    return;
  }
  errorEl.textContent = "";

  const prioridad = document.querySelector('input[name="wl-prioridad"]:checked')?.value || "MEDIA";
  const catId     = document.getElementById("wl-categoriaId").value;
  const platId    = document.getElementById("wl-plataformaId").value;

  const data = {
    titulo,
    prioridad,
    notas:     document.getElementById("wl-notas").value.trim() || null,
    categoria: catId  ? { id: Number(catId)  } : null,
    plataforma: platId ? { id: Number(platId) } : null,
  };

  try {
    await request(`${API_URL}/wishlist`, {
      method: "POST",
      headers: jsonAuthHeaders(),
      body: JSON.stringify(data),
    });
  } catch (err) {
    errorEl.textContent = err.message;
    notify(`No se pudo agregar a la wishlist: ${err.message}`, "error");
    return;
  }
  modalWishlist.style.display = "none";
  notify("Agregado a la wishlist.", "success");
  await cargarWishlist();
}

async function eliminarWishlistItem(id) {
  if (!confirm("\u00BFQuitar este juego de la wishlist?")) return;
  try {
    await request(`${API_URL}/wishlist/${id}`, { method: "DELETE", headers: authHeaders() });
  } catch (e) {
    notify(`No se pudo quitar de la wishlist: ${e.message}`, "error");
    return;
  }
  await cargarWishlist();
}

/**
 * common.js - Funciones compartidas para el frontend
 */

// Configuración global
const CONFIG = {
    API_URL: "/dsaApp",
    SESSION_TIMEOUT: 3600000,
    SESSION_KEY: "loggedInUser",
    SESSION_TIMESTAMP_KEY: "sessionTimestamp"
};

/**
 * Muestra una notificación toast al usuario
 * @param {string} mensaje - Mensaje a mostrar
 * @param {string} tipo - Tipo: "exito", "error", "info", "warning"
 */
function mostrarNotificacion(mensaje, tipo = "info") {
    // Remover notificación existente si hay
    $("#toast-notification").remove();
    
    // Configuración según el tipo
    const configs = {
        exito: {
            colorClass: "text-green-500 border-green-500/30 bg-green-900/30",
            icon: "check_circle",
            textColor: "text-green-200"
        },
        error: {
            colorClass: "text-red-500 border-red-500/30 bg-red-900/30",
            icon: "error",
            textColor: "text-red-200"
        },
        info: {
            colorClass: "text-blue-500 border-blue-500/30 bg-blue-900/30",
            icon: "info",
            textColor: "text-blue-200"
        },
        warning: {
            colorClass: "text-yellow-500 border-yellow-500/30 bg-yellow-900/30",
            icon: "warning",
            textColor: "text-yellow-200"
        }
    };

    const config = configs[tipo] || configs.info;
    
    const toastHtml = `
        <div id="toast-notification" class="toast-animate fixed top-5 right-5 z-50 flex items-center p-4 mb-4 ${config.textColor} border ${config.colorClass.split(' ')[1]} rounded-lg shadow-lg bg-black/80 backdrop-blur-md" role="alert">
            <div class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 ${config.colorClass} rounded-lg">
                <span class="material-symbols-outlined text-sm">${config.icon}</span>
            </div>
            <div class="ml-3 text-sm font-normal">${mensaje}</div>
            <button type="button" class="ml-auto -mx-1.5 -my-1.5 ${config.textColor} hover:text-white rounded-lg focus:ring-2 p-1.5 hover:bg-white/10 inline-flex items-center justify-center h-8 w-8" onclick="$(this).parent().remove()">
                <span class="sr-only">Close</span>
                <span class="material-symbols-outlined text-sm">close</span>
            </button>
        </div>
    `;
    
    $("body").append(toastHtml);
    
    // Auto hide después de 5 segundos
    setTimeout(() => {
        $("#toast-notification").fadeOut(300, function() { $(this).remove(); });
    }, 5000);
}

/**
 * Valida el formato de un email
 * @param {string} email - Email a validar
 * @returns {boolean} true si es válido
 */
function validarEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Valida que la contraseña sea fuerte
 * @param {string} password - Contraseña a validar
 * @returns {boolean} true si es fuerte (min 8 chars, mayúscula, minúscula, número)
 */
function validarPasswordFuerte(password) {
    const regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    return regex.test(password);
}

/**
 * Evalúa la fuerza de una contraseña
 * @param {string} password - Contraseña a evaluar
 * @returns {object} {nivel: number (1-4), texto: string, color: string}
 */
function evaluarFuerzaPassword(password) {
    if (!password || password.length === 0) {
        return { nivel: 0, texto: "", color: "" };
    }
    
    if (password.length < 4) {
        return { nivel: 1, texto: "Muy débil", color: "red" };
    }
    
    if (password.length < 8) {
        return { nivel: 2, texto: "Débil", color: "yellow" };
    }
    
    if (validarPasswordFuerte(password)) {
        return { nivel: 4, texto: "Fuerte", color: "green" };
    }
    
    return { nivel: 3, texto: "Media", color: "yellow" };
}

/**
 * Verifica si hay una sesión activa y si no ha expirado
 * @returns {string|null} username si hay sesión válida, null si no
 */
function obtenerSesionActiva() {
    const username = localStorage.getItem(CONFIG.SESSION_KEY);
    const timestamp = localStorage.getItem(CONFIG.SESSION_TIMESTAMP_KEY);
    
    if (!username) {
        return null;
    }
    
    // Si no hay timestamp, lo creamos ahora (retrocompatibilidad)
    if (!timestamp) {
        localStorage.setItem(CONFIG.SESSION_TIMESTAMP_KEY, Date.now().toString());
        return username;
    }
    
    // Verificar si la sesión ha expirado
    const tiempoTranscurrido = Date.now() - parseInt(timestamp);
    if (tiempoTranscurrido > CONFIG.SESSION_TIMEOUT) {
        // Sesión expirada
        cerrarSesion();
        return null;
    }
    
    // Actualizar timestamp (renovar sesión)
    localStorage.setItem(CONFIG.SESSION_TIMESTAMP_KEY, Date.now().toString());
    return username;
}

/**
 * Inicia una sesión guardando el username
 * @param {string} username - Nombre de usuario
 */
function iniciarSesion(username) {
    localStorage.setItem(CONFIG.SESSION_KEY, username);
    localStorage.setItem(CONFIG.SESSION_TIMESTAMP_KEY, Date.now().toString());
}

/**
 * Cierra la sesión eliminando los datos del localStorage
 */
function cerrarSesion() {
    localStorage.removeItem(CONFIG.SESSION_KEY);
    localStorage.removeItem(CONFIG.SESSION_TIMESTAMP_KEY);
}

/**
 * Maneja errores de las llamadas a la API
 * @param {Response} response - Respuesta del fetch
 * @param {string} contexto - Contexto de la operación (ej: "login", "compra")
 * @returns {Promise} Promise rechazado con mensaje de error apropiado
 */
async function manejarErrorAPI(response, contexto = "") {
    let mensaje = "Error desconocido";
    
    try {
        // Intentar leer el cuerpo de la respuesta
        const errorText = await response.text();
        mensaje = errorText || `Error ${response.status}`;
    } catch (e) {
        mensaje = `Error ${response.status}`;
    }
    
    // Mensajes específicos por código de estado
    switch (response.status) {
        case 400:
            mensaje = "Datos inválidos o incompletos";
            break;
        case 401:
            mensaje = "Credenciales incorrectas";
            break;
        case 402:
            mensaje = "Saldo insuficiente";
            break;
        case 404:
            mensaje = "Recurso no encontrado";
            break;
        case 409:
            mensaje = "El usuario ya existe";
            break;
        case 410:
            mensaje = "El correo ya está registrado";
            break;
        case 500:
            mensaje = "Error interno del servidor. Intenta de nuevo más tarde.";
            break;
    }
    
    return Promise.reject(new Error(mensaje));
}

/**
 * Toggle para mostrar/ocultar contraseña
 * @param {jQuery} inputElement - Input de password
 * @param {jQuery} iconElement - Icono de visibilidad
 */
function togglePasswordVisibility(inputElement, iconElement) {
    const currentType = inputElement.attr("type");
    if (currentType === "password") {
        inputElement.attr("type", "text");
        iconElement.text("visibility");
    } else {
        inputElement.attr("type", "password");
        iconElement.text("visibility_off");
    }
}

/**
 * Muestra un loading state en un botón
 * @param {jQuery} button - Botón a modificar
 * @param {boolean} loading - true para mostrar loading, false para ocultar
 * @param {string} textoOriginal - Texto original del botón (opcional)
 */
function setButtonLoading(button, loading, textoOriginal = null) {
    if (loading) {
        button.prop('disabled', true).addClass('opacity-75 cursor-not-allowed');
        if (textoOriginal) {
            button.data('original-text', button.html());
            button.html(`
                <span class="inline-flex items-center gap-2">
                    <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                        <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <span>Cargando...</span>
                </span>
            `);
        }
    } else {
        button.prop('disabled', false).removeClass('opacity-75 cursor-not-allowed');
        const originalText = button.data('original-text');
        if (originalText) {
            button.html(originalText);
        }
    }
}

/**
 * Transición suave entre páginas
 * @param {string} url - URL de destino
 */
function transicionPagina(url) {
    $("body").removeClass("loaded");
    setTimeout(() => {
        window.location.href = url;
    }, 500);
}

/**
 * Obtiene el icono apropiado para el tipo de objeto
 * @param {string} tipo - Tipo de objeto (ESPADA, ESCUDO, POCION, etc.)
 * @returns {string} Clase CSS del icono Font Awesome
 */
function getIconForType(tipo) {
    const iconMap = {
        "ESPADA": "fas fa-khanda",
        "ESCUDO": "fas fa-shield-alt",
        "POCION": "fas fa-flask",
        "ARMADURA": "fas fa-vest",
        "ANILLO": "fas fa-ring",
        "AMULETO": "fas fa-gem",
        "ARCO": "fas fa-bow-arrow",
        "HACHA": "fas fa-axe",
        "LANZA": "fas fa-spear"
    };
    
    return iconMap[tipo] || "fas fa-box";
}

// Exportar configuración
window.CONFIG = CONFIG;

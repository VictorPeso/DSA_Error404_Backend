$(document).ready(function(){
    // Verificar si ya hay sesión activa
    const sesionActiva = obtenerSesionActiva();
    if (sesionActiva) {
        transicionPagina("shop.html");
        return;
    }

    // Password Strength Meter
    $("#passwordField").on("input", function () {
        const password = $(this).val();
        actualizarBarraFuerza(password);
    });

    // Toggle Passwords
    $("#toggle-password-1").click(function () {
        const field = $("#passwordField");
        const icon = $(this).find("span");
        togglePasswordVisibility(field, icon);
    });

    $("#toggle-password-2").click(function () {
        const field = $("#passwordField2");
        const icon = $(this).find("span");
        togglePasswordVisibility(field, icon);
    });

    // Register Handler
    $("#register-btn").click(function(e){
        e.preventDefault();
        handleRegister();
    });

    // Enter key para submit
    $("input").keypress(function(e){
        if (e.which === 13) { // Enter key
            handleRegister();
        }
    });
});

/**
 * Actualiza la barra de fuerza de contraseña
 */
function actualizarBarraFuerza(password) {
    const bar = $("#strengthBar");
    const container = $("#strengthContainer");
    
    const fuerza = evaluarFuerzaPassword(password);
    
    // Mostrar/ocultar contenedor
    if (password) {
        container.removeClass('hidden');
    } else {
        container.addClass('hidden');
        return;
    }

    // Actualizar barra
    bar.removeClass("w-1/4 w-2/4 w-3/4 w-full bg-red-500 bg-yellow-500 bg-green-500");
    
    switch(fuerza.nivel) {
        case 0:
            bar.addClass("w-0");
            break;
        case 1:
            bar.addClass("w-1/4 bg-red-500");
            break;
        case 2:
            bar.addClass("w-2/4 bg-yellow-500");
            break;
        case 3:
            bar.addClass("w-3/4 bg-yellow-500");
            break;
        case 4:
            bar.addClass("w-full bg-green-500");
            break;
    }
}

/**
 * Maneja el proceso de registro
 */
async function handleRegister() {
    const username = $("input[name='username']").val().trim();
    const email = $("input[name='email']").val().trim();
    const password = $("input[name='password']").val();
    const confirmPassword = $("input[name='confirm_password']").val();

    // Validaciones
    if (!username || !email || !password || !confirmPassword) {
        mostrarNotificacion("Por favor, completa todos los campos.", "error");
        return;
    }

    if (!validarEmail(email)) {
        mostrarNotificacion("El formato del correo no es válido.", "error");
        return;
    }

    if (password !== confirmPassword) {
        mostrarNotificacion("Las contraseñas no coinciden.", "error");
        $("input[name='password']").val("");
        $("input[name='confirm_password']").val("");
        actualizarBarraFuerza("");
        return;
    }

    if (!validarPasswordFuerte(password)) {
        mostrarNotificacion("Contraseña débil: debe tener mayúscula, minúscula, número y mínimo 8 caracteres.", "error");
        return;
    }

    const btn = $("#register-btn");
    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${CONFIG.API_URL}/game/users/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "nombre": username,
                "password": password,
                "email": email
            })
        });

        if (!response.ok) {
            throw await manejarErrorAPI(response, "register");
        }

        const data = await response.json();
        
        mostrarNotificacion(`Usuario registrado correctamente: ${username}`, "exito");
        
        // Redirigir al login después de 2 segundos
        setTimeout(() => {
            transicionPagina("login.html");
        }, 2000);

    } catch (error) {
        mostrarNotificacion(error.message, "error");
        setButtonLoading(btn, false);
    }
}

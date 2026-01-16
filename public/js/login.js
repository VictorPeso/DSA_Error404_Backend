$(document).ready(function(){
    // Verificar si ya hay sesión activa
    const sesionActiva = obtenerSesionActiva();
    if (sesionActiva) {
        transicionPagina("shop.html");
        return;
    }

    // Password Visibility Toggle
    $("#toggle-password").click(function(){
        const input = $("input[name='password']");
        const icon = $(this).find("span");
        togglePasswordVisibility(input, icon);
    });

    // Login Handler
    $("#login-btn").click(function(e){
        e.preventDefault();
        handleLogin();
    });

    // Enter key para submit
    $("input").keypress(function(e){
        if (e.which === 13) { // Enter key
            handleLogin();
        }
    });
});

/**
 * Maneja el proceso de login
 */
async function handleLogin() {
    const username = $("input[name='username']").val().trim();
    const password = $("input[name='password']").val();

    // Validaciones básicas
    if (!username || !password) {
        mostrarNotificacion("Por favor, completa todos los campos.", "error");
        return;
    }

    const btn = $("#login-btn");
    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${CONFIG.API_URL}/game/users/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                "nombre": username,
                "password": password
            })
        });

        if (!response.ok) {
            throw await manejarErrorAPI(response, "login");
        }

        const data = await response.json();
        
        // Iniciar sesión
        iniciarSesion(data.username);
        
        mostrarNotificacion(`¡Bienvenido ${data.username}!`, "exito");
        
        // Redirigir después de un breve delay
        setTimeout(() => {
            transicionPagina("shop.html");
        }, 1000);

    } catch (error) {
        mostrarNotificacion(error.message, "error");
        setButtonLoading(btn, false);
    }
}

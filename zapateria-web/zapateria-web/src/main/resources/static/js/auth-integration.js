// Ejemplo de integración con el backend desde zapateria-web
// Guarda esto en: src/main/resources/static/js/auth-integration.js

const API_BASE_URL = 'http://localhost:8081/api/auth';

/**
 * Realiza login del usuario
 * @param {string} email - Email del usuario
 * @param {string} password - Contraseña del usuario
 * @returns {Promise} Promesa con la respuesta del servidor
 */
window.loginUser = async function(email, password) {
    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            // Guardar datos del usuario en sessionStorage (incluyendo role)
            sessionStorage.setItem('user', JSON.stringify({
                id: data.id,
                email: data.email,
                firstName: data.firstName,
                lastName: data.lastName,
                role: data.role
            }));
            sessionStorage.setItem('loggedIn', 'true');
            return {
                success: true,
                user: data,
                message: data.message
            };
        } else {
            return {
                success: false,
                message: data.message || 'Error en el login'
            };
        }
    } catch (error) {
        console.error('Error:', error);
        return {
            success: false,
            message: 'Error de conexión con el servidor'
        };
    }
}
function registeruser() {
    const userData = {
        firstName: document.getElementById("firstName").value,
        lastName: document.getElementById("lastName").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        phoneNumber: document.getElementById("phoneNumber").value,
        role: document.getElementById("role").value,
        address: document.getElementById("address").value,
        city: document.getElementById("city").value,
        postalCode: document.getElementById("postalCode").value
    };

    window.registerUser(userData).then(result => {
        if (result.success) {
            alert("Usuario registrado correctamente");

            document.querySelector("#formUsuario form").reset();
            document.getElementById("formUsuario").style.display = "none";

        } else {
            alert("Error: " + result.message);
        }
    });
}
/**
 * Registra un nuevo usuario
 * @param {Object} userData - Objeto con los datos del usuario
 * @returns {Promise} Promesa con la respuesta del servidor
 */
window.registerUser = async function(userData) {
    try {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: userData.email,
                password: userData.password,
                firstName: userData.firstName,
                lastName: userData.lastName,
                phoneNumber: userData.phoneNumber || '',
                address: userData.address || '',
                city: userData.city || '',
                postalCode: userData.postalCode || ''
            })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            return {
                success: true,
                user: data,
                message: data.message
            };
        } else {
            return {
                success: false,
                message: data.message || 'Error en el registro'
            };
        }
    } catch (error) {
        console.error('Error:', error);
        return {
            success: false,
            message: 'Error de conexión con el servidor'
        };
    }
};

/**
 * Valida si un email ya está registrado
 * @param {string} email - Email a validar
 * @returns {Promise<boolean>} true si existe, false si está disponible
 */
window.checkEmailExists = async function(email) {
    try {
        const response = await fetch(`${API_BASE_URL}/check-email/${email}`);
        return await response.json();
    } catch (error) {
        console.error('Error:', error);
        return false;
    }
};

/**
 * Obtiene los datos del usuario actual desde sessionStorage
 * @returns {Object|null} Datos del usuario o null si no está autenticado
 */
window.getCurrentUser = function() {
    const userStr = sessionStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
};

/**
 * Verifica si el usuario está autenticado
 * @returns {boolean} true si está autenticado, false en caso contrario
 */
window.isUserLoggedIn = function() {
    return sessionStorage.getItem('loggedIn') === 'true';
};

/**
 * Cierra la sesión del usuario
 */
window.logout = function() {
    sessionStorage.removeItem('user');
    sessionStorage.removeItem('loggedIn');
    console.log('Sesión cerrada');
    window.location.href = '/login';
};

/**
 * Ejemplo de uso en el formulario de login
 */
window.handleLoginSubmit = function(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    window.loginUser(email, password).then(result => {
        if (result.success) {
            alert('¡Login exitoso! Bienvenido ' + result.user.firstName);
            // Redirigir según el rol
            window.redirectByRole();
        } else {
            alert('Error: ' + result.message);
        }
    });
};

/**
 * Ejemplo de uso en el formulario de registro
 */
window.handleRegisterSubmit = function(event) {
    event.preventDefault();

    const userData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        address: document.getElementById('address').value,
        city: document.getElementById('city').value,
        postalCode: document.getElementById('postalCode').value
    };

    window.registerUser(userData).then(result => {
        if (result.success) {
            alert('¡Registro exitoso! Ya puedes iniciar sesión');
            // Limpiar formulario
            event.target.reset();
            // Redirigir a login
            window.location.href = '/login';
        } else {
            alert('Error: ' + result.message);
        }
    });
};

/**
 * Valida email en tiempo real
 */
window.validateEmailRealtime = async function(email) {
    if (!email) return;

    const exists = await window.checkEmailExists(email);
    const emailInput = document.getElementById('email');

    if (exists) {
        emailInput.classList.add('is-invalid');
        emailInput.classList.remove('is-valid');
        document.getElementById('emailFeedback').textContent = 'Este email ya está registrado';
    } else {
        emailInput.classList.add('is-valid');
        emailInput.classList.remove('is-invalid');
        document.getElementById('emailFeedback').textContent = 'Email disponible';
    }
};

/**
 * Redirige al usuario según su rol
 */
window.redirectByRole = function() {
    if (window.isUserLoggedIn()) {
        const user = window.getCurrentUser();
        if (user && user.role) {
            if (user.role === 'ADMIN') {
                window.location.href = '/admin/dashboard';
            } else {
                window.location.href = '/';
            }
        }
    }
};

/**
 * Verifica si el usuario es administrador
 * @returns {boolean} true si es admin, false en caso contrario
 */
window.isAdmin = function() {
    if (window.isUserLoggedIn()) {
        const user = window.getCurrentUser();
        return user && user.role === 'ADMIN';
    }
    return false;
};

// Exportar funciones si se usa como módulo
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        loginUser,
        registerUser,
        checkEmailExists,
        getCurrentUser,
        isUserLoggedIn,
        logout,
        handleLoginSubmit,
        handleRegisterSubmit,
        validateEmailRealtime,
        redirectByRole,
        isAdmin
    };
}


// ================= LISTAR USUARIOS =================
async function loadUsers() {
    const res = await fetch(`${API_BASE_URL}/users`);
    const users = await res.json();

const table = document.getElementById("usersTable");
            table.innerHTML = "";

            users.forEach(u => {

                table.innerHTML += `
                    <tr>
                        <td>${u.firstName} ${u.lastName}</td>
                        <td>${u.email}</td>
                        <td>${u.role}</td>
                        <td style="text-align:center;">

                            <button class="btn-action btn-edit"
                                onclick="openEditUser(${u.id}, '${u.firstName}', '${u.lastName}', '${u.email}', '${u.role}')">
                                Editar
                            </button>

                            <button class="btn-action btn-delete"
                                onclick="deleteUser(${u.id})">
                                Eliminar
                            </button>

                        </td>
                    </tr>
                `;
            });
}
function openEditUser(id, firstName, lastName, email, role) {

    const modal = document.getElementById("editModal");

    const fName = document.getElementById("editFirstName");
    const lName = document.getElementById("editLastName");
    const mail = document.getElementById("editEmail");
    const rol = document.getElementById("editRole");

    if (!fName || !lName || !mail || !rol) {
        console.error("Inputs del modal no encontrados");
        return;
    }

    document.getElementById("editId").value = id;
    fName.value = firstName;
    lName.value = lastName;
    mail.value = email;
    rol.value = role;

    modal.style.display = "flex";
}
function closeModal() {
    document.getElementById("editModal").style.display = "none";
}

// ================= ELIMINAR =================
async function deleteUser(id) {
    if (!confirm("¿Eliminar usuario?")) return;

    await fetch(`${API_BASE_URL}/users/${id}`, {
        method: "DELETE"
    });

    loadUsers();
}
function updateUser() {

    const id = document.getElementById("editId").value;

    const userData = {
        firstName: document.getElementById("editFirstName").value,
        lastName: document.getElementById("editLastName").value,
        email: document.getElementById("editEmail").value,
        role: document.getElementById("editRole").value
    };

    fetch(`${API_BASE_URL}/users/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userData)
    })
    .then(res => res.json())
    .then(() => {
        alert("Usuario actualizado");
        closeModal();
        loadUsers();
    });
}
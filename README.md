# <font color="#ff0"> **Aplicación Web MVC con Springboot** </font>
## Sistema ToDoList 
###### _Autor_: Nayeli Leiva
###### _Fecha_: 10/06/2025
###### _Materia_: Metodologías ágiles
Este proyecto consiste en el desarrollo de una aplicación web **ToDoList** usando **Spring Boot**, **Thymeleaf**, **Bootstrap** y JPA. La aplicación permite a los usuarios registrar tareas, gestionar sus cuentas y, en el caso de los administradores, gestionar el acceso de otros usuarios además de ver las lista de usuarios registrados. El sistema implementa roles diferenciados, autenticación y autorización, una interfaz moderna y diversas funcionalidades clave.

---

## Funcionalidades Principales

### Barra de Menú

Todas las páginas, salvo las de **login** y **registro**, comparten una **barra de navegación (navbar)** construida con Bootstrap. Esta barra se sitúa en la parte superior e incluye:

- **ToDoList**: Enlace a la página *Acerca de*, visible para todos los usuarios.
- **Tareas**: Enlace directo a la lista de tareas pendientes del usuario autenticado.
- **Usuario logeado**: En la esquina derecha aparece el nombre del usuario. Al hacer clic se despliega un menú con las siguientes opciones:
    - **Cuenta**: Página con información del usuario registrado en ese momento.
    - **Cerrar sesión**: Finaliza la sesión actual y redirige a la página de login.

Si el usuario no ha iniciado sesión, en lugar del menú desplegable aparecen los enlaces a **Login** y **Registro**, adaptando dinámicamente la interfaz mediante Thymeleaf.

---

## Gestión de Usuarios

### Listado de Usuarios

La URL `/registrados` permite acceder a un listado de usuarios registrados al usuario Administrador. Cada entrada muestra:

- Identificador del usuario.
- Correo electrónico.
- Enlace para acceder a la descripción detallada de cada usuario.

### Descripción de Usuario

Cada usuario tiene una página accesible mediante `/cuenta` donde se muestra toda su información personal, exceptuando la contraseña.

---

## Usuario Administrador

### Registro como Administrador

Durante el registro, los usuarios tienen la opción de activarse como **administradores** mediante un **checkbox**. Este checkbox solo aparece si aún no existe un administrador en el sistema, asegurando que haya **únicamente un administrador activo**.

### Funcionalidades Exclusivas

El administrador accede directamente a la lista de usuarios tras iniciar sesión. Desde ahí puede:

- **Visualizar descripciones** de usuarios.
- **Bloquear o desbloquear** cuentas de usuario mediante botones asociados.

Al bloquear un usuario, se modifica un atributo booleano `bloqueado` en su entidad. Si un usuario bloqueado intenta iniciar sesión, el sistema lo redirige con un mensaje de error indicando que su acceso ha sido restringido.

### Controlador de Administración

Para gestionar estas funcionalidades se creó una clase **`UsuarioAdminController`** que maneja:

- Visualización de usuarios.
- Descripción de usuarios.
- Cambios de estado en los atributos `bloqueado` y `administrador`.

Este controlador está protegido para que **solo el administrador** tenga acceso. Las rutas protegidas retornan un **código HTTP 403 (No autorizado)** si un usuario no administrador intenta acceder, y se muestra un mensaje amigable usando la vista de error personalizada.

---

## Seguridad y Protección

Para proteger rutas y vistas se implementaron reglas como:

- Páginas de tareas solo accesibles a usuarios autenticados.
- Vista y control de usuarios únicamente disponible para administradores.
- Gestión de acceso bloqueado para prevenir login de usuarios inactivos.

---

## Interfaz de Usuario

El frontend de la aplicación fue construido con **Bootstrap**, ofreciendo una experiencia de usuario limpia y responsiva. Se emplearon formularios y botones estilizados para mejorar la usabilidad, y se incluyeron alertas claras en caso de errores o restricciones.
## Enlace trello
[Enlace externo a trello](https://trello.com/invite/b/683c4289952b062577c95cbd/ATTI0e94bae214f5177354a6dd4c7a21d15dDB880E49/my-trello-board)



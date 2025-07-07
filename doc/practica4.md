# Documentación Versión 1.3.0

- **Aplicación:** ToDoApp
- **Equipo:** Equipo 3
- **Versión Actual:** 1.3.0
- **Fecha de Release:** 7/6/2025

[![GitHub release](https://img.shields.io/github/v/release/ToDoListEquipo1/todolist_Equipo1)](https://github.com/ToDoListEquipo1/todolist_Equipo1/releases)
[![Docker image](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://hub.docker.com/r/aidmora/mads-todolist-grupo03/tags)
[![Github Project](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/orgs/ToDoListEquipo1/projects/1/views/1)

## Cambios de Versión 1.3.0

### 1. **Mejoras en la Entidad Tarea**

#### Cambios Realizados

- **Adición del campo `descripcion`** en la entidad `Tarea`
- Actualización del constructor para incluir descripción
- Modificación de métodos `equals()` y `hashCode()` para incluir el nuevo campo

#### Archivos Modificados

```java
src/main/java/example/ToDoApp/model/Tarea.java
src/main/java/example/ToDoApp/dto/TareaData.java
```

#### Código Modificado

```java
// Nuevo constructor con descripción
public Tarea(Usuario usuario, String titulo, String descripcion) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    setUsuario(usuario);
}

// Nuevo campo en la entidad
private String descripcion;
```

### 2. **Mejoras en la Interfaz de Usuario**

#### Cambios Realizados

- **Rediseño completo de la lista de tareas** (`listaTareas.html`)
- Integración de Bootstrap Icons
- Mejora de la experiencia visual y funcional
- Estado vacío para cuando no hay tareas

#### Características Implementadas

- ✅ Iconos en botones de acción (editar/borrar)
- ✅ Hover effects en filas de tabla
- ✅ Badges para conteo de tareas e IDs
- ✅ Mensaje amigable cuando no hay tareas
- ✅ Estilos modernos con sombras y bordes redondeados

#### Archivos Modificados

```jva
src/main/resources/templates/listaTareas.html
```

### 3. **Entidad Equipos**

#### Nueva Funcionalidad

- **Adición del campo `descripcion`** a la tabla `equipos`
- Preparación para futuras funcionalidades de gestión de equipos

---

## Esquemas de Base de Datos

### **Esquema Versión 1.2.0**

```sql
-- Tabla usuarios
CREATE TABLE public.usuarios (
    id integer NOT NULL,
    apellido character varying(255),
    bloqueado boolean,
    email character varying(255),
    es_admin boolean NOT NULL,
    fecha_nacimiento date,
    nombre character varying(255),
    password character varying(255)
);

-- Tabla tareas (sin descripción)
CREATE TABLE public.tareas (
    id bigint NOT NULL,
    titulo character varying(255),
    usuario_id integer
);

-- Tabla equipos (sin descripción)
CREATE TABLE public.equipos (
    id bigint NOT NULL,
    nombre character varying(255)
);
```

### **Esquema Versión 1.3.0**

```sql
-- Tabla usuarios (sin cambios)
CREATE TABLE public.usuarios (
    id integer NOT NULL,
    apellido character varying(255),
    bloqueado boolean,
    email character varying(255),
    es_admin boolean NOT NULL,
    fecha_nacimiento date,
    nombre character varying(255),
    password character varying(255)
);

-- Tabla tareas (CON descripción)
CREATE TABLE public.tareas (
    id bigint NOT NULL,
    descripcion character varying(255),  -- ⭐ NUEVO CAMPO
    titulo character varying(255),
    usuario_id integer
);

-- Tabla equipos (CON descripción)
CREATE TABLE public.equipos (
    id bigint NOT NULL,
    nombre character varying(255),
    descripcion character varying(255)  -- ⭐ NUEVO CAMPO
);
```

### **Diferencias Entre Versiones**

| Tabla | Campo | Versión 1.2.0 | Versión 1.3.0 |
|-------|-------|----------------|----------------|
| `tareas` | `descripcion` | ❌ No existe | ✅ `character varying(255)` |
| `equipos` | `descripcion` | ❌ No existe | ✅ `character varying(255)` |

---

## Script de Migración (1.2.0 → 1.3.0)

### **Archivo de Migración:**

`sql/schema-1.2.0-1.3.0.sql`

### **Contenido del Script:**

```sql
-- Migración de la versión 1.2.0 a 1.3.0
-- Añadir campo descripción a la tabla equipos
ALTER TABLE public.equipos
ADD COLUMN descripcion character varying(255);
```

---

## 🚀 Detalles del Despliegue de Producción

### **Configuración de Base de Datos**

#### **Desarrollo:**

```properties
# Archivo: application-postgres-dev.properties
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
DB_USER=mads
DB_PASSWD=mads

spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/mads
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### **Producción:**

```properties
# Archivo: application-postgres.properties
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
DB_USER=mads
DB_PASSWD=mads

spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/mads
spring.jpa.hibernate.ddl-auto=validate  # ⚠️ Importante: validate en producción
spring.jpa.show-sql=true
```

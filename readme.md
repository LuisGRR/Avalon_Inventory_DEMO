# Avalon Inventory

## Descripción
Avalon Inventory es un sistema de gestión de inventario desarrollado en **Java 17** utilizando **Spring Boot** y el paradigma **Domain-Driven Design (DDD)**. La aplicación está contenedorizada con **Docker** y se puede desplegar fácilmente con **Docker Compose**.

El sistema implementa **autenticación y autorización con JWT**, permitiendo validar usuarios y roles. Se crea un usuario **ADMIN** por defecto con un rol **ADMIN** que tiene todos los permisos. La aplicación valida los permisos asignados a cada usuario y restringe el acceso a las funcionalidades según su rol.

## Tecnologías utilizadas
- **Java 17**
- **Spring Boot**
- **Domain-Driven Design (DDD)**
- **JWT (JSON Web Token)**
- **PostgreSQL**
- **Docker & Docker Compose**

---

## Instalación y despliegue

### Prerrequisitos
Asegúrate de tener instalados los siguientes componentes en tu sistema:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Construcción y despliegue del proyecto

1. Clona el repositorio:
   ```sh
   git clone https://github.com/tu-usuario/avalon-inventory.git
   cd avalon-inventory
   ```

2. Compila el proyecto y genera el JAR:
   ```sh
   ./mvnw clean package
   ```

3. Construye la imagen Docker:
   ```sh
   docker build -t avalon-inventory .
   ```

4. Inicia el entorno con Docker Compose:
   ```sh
   docker-compose up -d
   ```

5. La aplicación estará disponible en `http://localhost:9080`

---

## Configuración de entorno
El sistema utiliza variables de entorno para la conexión con la base de datos PostgreSQL:

```sh
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/db_avalon_inventory
SPRING_DATASOURCE_USERNAME=merlin
SPRING_DATASOURCE_PASSWORD=avalonpassword
SPRING_SECURITY_JWT_SECRET=clave-secreta
```

---

## Seguridad y Autenticación
Avalon Inventory utiliza **JWT (JSON Web Token)** para la autenticación y autorización de usuarios. El sistema maneja **roles y permisos**, asegurando que cada usuario solo pueda acceder a las funcionalidades permitidas.

- **Usuario y rol por defecto:**
  - Se crea un usuario `ADMIN` por defecto.
  - Se genera un rol `ADMIN` con acceso total al sistema.
  - Todos los usuarios deben autenticarse con JWT para acceder a las rutas protegidas.

---

## Estructura del proyecto
El proyecto sigue el paradigma **Domain-Driven Design (DDD)** y está organizado de la siguiente manera:

```
avalon-inventory/
├── src/main/java/com/avalon/inventory/
│   ├── application/   # Casos de uso y servicios
│   ├── domain/        # Lógica de negocio y entidades
│   ├── infrastructure/# Adaptadores y configuraciones
│   │   └─── security/      # Configuración de seguridad y JWT
│   ├── presentation/  
│   │   └─── controller/ # Controladores REST
│   ├── AvalonInventoryApplication.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml  # Dependencias y configuración de Maven
└── README.md
```

---

## Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/Avalon_Inventory.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 9080
```

---

## Docker Compose

```yaml
version: "3.88"
services:
  app:
    image: avalon-inventory
    build:
      context: .
      dockerfile: Dockerfile
    container_name: avalon-inventory-container
    ports:
      - "9080:9080"
    restart: always
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/db_avalon_inventory
      SPRING_DATASOURCE_USERNAME: merlin
      SPRING_DATASOURCE_PASSWORD: avalonpassword
      SPRING_SECURITY_JWT_SECRET: clave-secreta
    depends_on:
      - postgres

  postgres:
    image: postgres
    restart: always
    ports:
      - "5433:5432"
    environment:
      - DATABASE_HOST=127.0.0.1
      - POSTGRES_USER=merlin
      - POSTGRES_PASSWORD=avalonpassword
      - POSTGRES_DB=db_avalon_inventory
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
    driver: local
```

---

## Comandos útiles

### Parar y eliminar los contenedores
```sh
docker-compose down
```

### Ver logs de la aplicación
```sh
docker logs -f avalon-inventory-container
```

### Conectarse al contenedor de PostgreSQL
```sh
docker exec -it $(docker ps -qf "name=postgres") psql -U merlin -d db_avalon_inventory
```

---

## Licencia
Este proyecto está bajo la licencia MIT. Para más detalles, consulta el archivo `LICENSE`.

---

## Contacto
- **Autor**: Luis Gerardo Rivera Rivera
- **Email**: tuemail@example.com
- **Repositorio**: [GitHub](https://github.com/tu-usuario/avalon-inventory)


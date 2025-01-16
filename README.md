# Proyecto msvc-Items

Este proyecto es parte de una serie educativa sobre Spring Cloud. El objetivo es aprender a crear y gestionar microservicios utilizando Spring Cloud.

## Requisitos

- Java 21 o superior
- Maven 3.6.3 o superior
- Spring Boot 2.5.4 o superior

## Instalación

1. Navega al directorio del proyecto:
	```bash
	cd msvc-items
	```
2. Compila el proyecto usando Maven:
	```bash
	mvn clean install
	```

## Ejecución

Para ejecutar el proyecto, utiliza el siguiente comando:
```bash
mvn spring-boot:run
```

## Endpoints

El microservicio expone los siguientes endpoints:

- `GET /items`: Obtiene una lista de todos los ítems.
- `GET /items/{id}`: Obtiene un ítem por su ID.
- `POST /items`: Crea un nuevo ítem.
- `PUT /items/{id}`: Actualiza un ítem existente.
- `DELETE /items/{id}`: Elimina un ítem por su ID.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o un pull request para discutir cualquier cambio que desees realizar.

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

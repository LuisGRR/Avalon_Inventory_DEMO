# Usar una imagen base de Java 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY target/Avalon_Inventory-0.0.1.jar app.jar

# Exponer el puerto en el que la aplicación escuchará
EXPOSE 9080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
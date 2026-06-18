# Usa Temurin JDK 21 en Alpine (u otra variante que prefieras)
FROM eclipse-temurin:21-jdk-alpine

# Crea y entra al directorio de la app
WORKDIR /app

# Copia todo el proyecto (incluye mvnw y .mvn/)
COPY . .

# Asegura permisos de ejecución en el Maven Wrapper y convierte los saltos de línea a formato Unix
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# Construye la app
RUN ./mvnw \
      -DoutputFile=target/mvn-dependency-list.log \
      -B \
      -DskipTests \
      clean dependency:list install

# Al arrancar, busca dinámicamente el JAR en target/
CMD ["sh", "-c", "java -jar target/*.jar"]
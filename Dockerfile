
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn -B package -DskipTests

FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/BloodBankManagementSystem.war /usr/local/tomcat/webapps/ROOT.war

# Render assigns the HTTP port through the PORT environment variable.
# Update Tomcat's HTTP connector when the container starts.
RUN printf '#!/bin/sh\nset -eu\nPORT="${PORT:-10000}"\nsed -i "s/port=\"8080\"/port=\"${PORT}\"/" /usr/local/tomcat/conf/server.xml\nexec /usr/local/tomcat/bin/catalina.sh run\n' > /usr/local/tomcat/bin/render-start.sh \
    && chmod +x /usr/local/tomcat/bin/render-start.sh

EXPOSE 10000

CMD ["/usr/local/tomcat/bin/render-start.sh"]

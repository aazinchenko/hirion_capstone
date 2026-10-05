FROM mcr.microsoft.com/playwright/java:v1.55.0-noble

WORKDIR /work

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src

ENTRYPOINT ["mvn", "-B", "test"]
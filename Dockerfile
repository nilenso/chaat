FROM clojure:temurin-17-lein-2.10.0-jammy

RUN mkdir -p /app
WORKDIR /app

COPY project.clj /app
RUN lein deps

COPY . /app
RUN lein uberjar

EXPOSE 3000
WORKDIR /app/target/uberjar
CMD ["java", "-jar", "chaat-0.1.0-SNAPSHOT-standalone.jar"]
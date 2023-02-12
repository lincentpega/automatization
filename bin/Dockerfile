# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy as base
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src

FROM base as development
CMD ["./mvnw", "spring-boot:run"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:17-jre-jammy as production
ARG APP_PORT
ARG REDIS_HOST
ARG REDIS_PORT
ARG BOT_PATH
EXPOSE 8000

RUN apt-get update && apt-get -y install gnupg \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update && apt-get -y install google-chrome-stable unzip \
    && wget https://chromedriver.storage.googleapis.com/108.0.5359.71/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip && mv chromedriver /opt

COPY --from=build /app/target/java-wildberries-self-buy-*.jar /java-wildberries-self-buy.jar

CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.redis.port=${REDIS_PORT}", "-Dspring.redis.host=${REDIS_HOST}", "-Dserver.port=${APP_PORT}", "-Dtelegram.bot-path=${BOT_PATH}", "-jar", "/java-wildberries-self-buy.jar"]
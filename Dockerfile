FROM eclipse-temurin:17-jre-alpine

RUN apk update && apk add --no-cache bash unzip

WORKDIR /app

COPY ./target/universal/minesweeper-web-1.0-SNAPSHOT.zip /app/

RUN unzip minesweeper-web-1.0-SNAPSHOT.zip

COPY entrypoint.sh /app/

EXPOSE 9000

ENTRYPOINT [ "/app/entrypoint.sh" ]

FROM ubuntu

WORKDIR /usr/src/app

RUN apt-get update
RUN apt-get install -y maven default-jre

COPY pom.xml .

RUN ls -al

RUN mvn clean install

COPY src src

RUN ls -al

RUN mvn package

CMD ["java", "-jar", "target/app-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]

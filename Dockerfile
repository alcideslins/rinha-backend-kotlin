 FROM maven:3-eclipse-temurin-21-alpine AS build
 RUN mkdir -p /usr/src/project
 COPY . /usr/src/project
 WORKDIR /usr/src/project
 
 RUN mvn package -DskipTests

 RUN jdeps --ignore-missing-deps -q  \
   --recursive  \
   --multi-release 21  \
   --print-module-deps  \
   --class-path 'target/dependency/*'  \
   target/rinha-app*.jar > deps.info \
      && jlink \
     --add-modules $(cat deps.info) \
     --strip-debug \
     --compress 2 \
     --no-header-files \
     --no-man-pages \
     --output /customjre
 
 FROM alpine:3.19.1
 ENV JAVA_HOME /customjre
 ENV PATH $JAVA_HOME/bin:$PATH
 COPY --from=build /customjre $JAVA_HOME
 RUN mkdir /project
 COPY --from=build /usr/src/project/target/*.jar /project/app.jar
 WORKDIR /project
 ENTRYPOINT java -jar app.jar

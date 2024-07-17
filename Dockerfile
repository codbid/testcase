FROM openjdk:22
LABEL authors="codbid"

COPY target/testcase-0.0.1-SNAPSHOT.jar /testcase.jar

ENTRYPOINT ["java", "-jar", "/testcase.jar"]
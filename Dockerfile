#aqui é o estágio de build, vai compilar usando a jdk do lab
FROM jenkins/jenkins:latest AS builder

#a imagem base usa o usuario jenkins, e o root é usado somente para compilar
USER root
WORKDIR /build
COPY Calculator.java .
RUN mkdir out && javac -encoding UTF-8 --release 17 -d out Calculator.java

#copia somente o bytecode necessaria para executar
FROM jenkins/jenkins:latest
WORKDIR /app
COPY --from=builder --chown=jenkins:jenkins /build/out/Calculator.class .
ENV JAVA_OPTS="-Dfile.encoding=UTF-8"

#servidor nao roda como root
USER jenkins
EXPOSE 8000

CMD ["java", "Calculator"]

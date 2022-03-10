FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

RUN apt-get -y update

# RUN DEBIAN_FRONTEND=noninteractive
RUN apt-get install -y libxext-dev libxrender-dev libxtst-dev

RUN export uid=1001 gid=1001 && \
    mkdir -p /home/developer && \
    echo "developer:x:${uid}:${gid}:Developer,,,:/home/developer:/bin/bash" >> /etc/passwd && \
    echo "developer:x:${uid}:" >> /etc/group && \
    echo "developer ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers && \
    chown developer:developer -R /home/developer

USER developer
ENV HOME /home/developer
WORKDIR /home/developer

EXPOSE 8888

COPY ./target/mono-0.0.1-SNAPSHOT.jar ./app.jar
ENTRYPOINT ["java", "-jar", "./app.jar"]
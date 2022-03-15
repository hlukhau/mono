FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

RUN apt-get -y update
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

# before that you need to do several linux command on the linux computer with docker
# xauth list $DISPLAY
# export DISPLAY=unix:0
# xhost +local:all
# sudo xhost +
ENV DISPLAY unix:0
ENV XAUTHORITY /tmp/.docker.xauth
ENV HOSTNAME mono-docker


EXPOSE 8888


COPY ./target/mono-0.0.1-SNAPSHOT.jar ./app.jar
COPY ./configuration.json ./configuration.json

USER root
RUN chmod 777 ./configuration.json
USER developer

ENTRYPOINT ["java", "-jar", "./app.jar"]
#BUILD COMMAND: docker build ./ -t mono
#LOGIN COMMAND: docker login https://docker.io -u hlukhau -p vo1vo2vo1vo2
#PUSH  COMMAND: docker push hlukhau/mono:latest
#RUN COMMAND:   docker run --rm -e DISPLAY=unix:0.0 -e XAUTHORITY=/tmp/.docker.xauth -e HOSTNAME=mono01 -v /tmp/.X11-unix:/tmp/.X11-unix --net=host mono:latest -d
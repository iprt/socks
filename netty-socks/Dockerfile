FROM openjdk:17-jdk

MAINTAINER "devops@kubectl.net"

LABEL email="devops@kubectl.net" \
      author="zhenjie zhu"

# random_file
RUN dd if=/dev/urandom of=/random_file bs=1K count=1

WORKDIR /opt/app

ADD build/libs/socks5.jar socks.jar

EXPOSE 1080

CMD java $JAVA_OPTIONS -jar socks.jar

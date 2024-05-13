# Netty Sock5

## reference

https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/socksproxy

## how to build

> prepare java development kit

### build `commons.jar`

`windows`

```bat
gradlew.bat commons:clean commons:build
```

`linux`

```shell
chmod +x gradlew

./gradlew commons:clean commons:build
```

### build `socks5.jar`

`windows`

```bat
gradlew.bat netty:clean netty:build
```

`linux`

```shell
chmod +x gradlew

./gradlew netty:clean netty:build
```

## how to run

find `socks5.jar` from directory `build/libs/`

start on default port `1080`

```shell
java -jar socks5.jar
```

or start on port which you want

```shell
java -jar -Dport=1088 socks5.jar
```

use `epoll`

```shell
java -jar -DuseEpoll=true socks5.jar
```


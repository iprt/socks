# Netty Sock5

## how to build

> prepare java development kit



`windows`

```bat
gradlew.bat clean build
```

`linux`

```shell
chmod +x gradlew

./gradlew clean build
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


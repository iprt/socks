dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
    }
}

rootProject.name = "socks"

include("commons")
include("netty-http-proxy")
include("netty-socks")
include("netty-tcp-proxy")
include("netty-tcp-dns-proxy")

include("advance:server", "advance:client")

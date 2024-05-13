#!/bin/bash
# shellcheck disable=SC2164
SHELL_FOLDER=$(cd "$(dirname "$0")" && pwd)
cd "$SHELL_FOLDER"

# shellcheck disable=SC1090
source <(curl -sSL https://code.kubectl.net/devops/build-project/raw/branch/main/func/log.sh)

cd ../
log "build" "step1 build jar"
bash <(curl -SL https://code.kubectl.net/devops/build-project/raw/branch/main/gradle/build.sh) \
  -c "gradle-8.4_cache" \
  -i "gradle:8.4" \
  -x "gradle clean :commons:build -x test && gradle :netty:clean :netty:build"

jar_name="socks.jar"

cd netty-socks/

if [ ! -f "build/libs/$jar_name" ]; then
  log "verify" "gradle build failed"
  exit 1
fi

log "build" "step2 build & push image"
image_name="registry.cn-shanghai.aliyuncs.com/iproute/netty-socks5"

bash <(curl -SL https://code.kubectl.net/devops/build-project/raw/branch/main/docker/build.sh) \
  -i "$image_name" \
  -v "latest" \
  -r "false" \
  -p "true"

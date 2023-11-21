package org.iproute.server;

import lombok.extern.slf4j.Slf4j;
import org.iproute.commons.advance.HostPortConfig;

/**
 * ServerMain
 *
 * @author zhuzhenjie
 */
@Slf4j
public class ServerMain {
    static final int PORT = Integer.parseInt(System.getProperty("port",
            String.valueOf(
                    HostPortConfig.get().getPort()
            )));

    public static void main(String[] args) {
        log.info("ServerMain listening port is {}", PORT);
    }
}

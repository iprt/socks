package org.iproute.client;

import lombok.extern.slf4j.Slf4j;

/**
 * ClientMain
 *
 * @author zhuzhenjie
 */
@Slf4j
public class ClientMain {

    static final int PORT = Integer.parseInt(System.getProperty("port", "1080"));

    public static void main(String[] args) throws Exception {
        log.info("listening port is  {}", PORT);
    }

}

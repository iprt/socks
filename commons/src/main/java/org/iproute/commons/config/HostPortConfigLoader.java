package org.iproute.commons.config;

import lombok.Getter;
import org.iproute.commons.protocol.HostPort;

import java.util.Properties;

/**
 * HostPortConfig
 *
 * @author zhuzhenjie
 */
@Getter
public class HostPortConfigLoader {

    private static HostPortConfigLoader instance;

    private HostPort hostPort;

    private HostPortConfigLoader() {
        Properties properties = new Properties();
        try {
            properties.load(HostPortConfigLoader.class.getResourceAsStream("server_config.properties"));

            String host = properties.getProperty("host");
            String port = properties.getProperty("port");

            hostPort = HostPort.builder()
                    .host(host).port(Integer.parseInt(port))
                    .build();

        } catch (Exception e) {
            this.hostPort = HostPort.defaultServerConfig();
        }
    }

    public static HostPortConfigLoader instance() {
        if (instance == null) {
            instance = new HostPortConfigLoader();
        }
        return instance;
    }

}

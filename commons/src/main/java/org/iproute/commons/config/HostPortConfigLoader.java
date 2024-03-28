package org.iproute.commons.config;

import com.alibaba.fastjson2.JSON;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.iproute.commons.protocol.HostPort;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

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
        InputStream in = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream(
                    "server_config.json"
            );
            if (Objects.isNull(in)) {
                this.hostPort = HostPort.defaultServerConfig();
                return;
            }
            List<String> lines = IOUtils.readLines(in, Charset.defaultCharset());
            String configJson = lines.stream().reduce((a, b) -> a + b).orElseGet(() -> "{}");
            this.hostPort = JSON.parseObject(configJson, HostPort.class);
        } catch (Exception e) {
            this.hostPort = HostPort.defaultServerConfig();
        }
        try {
            IOUtils.close(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static HostPortConfigLoader instance() {
        if (instance == null) {
            instance = new HostPortConfigLoader();
        }
        return instance;
    }

}

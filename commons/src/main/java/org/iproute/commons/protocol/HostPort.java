package org.iproute.commons.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * HostPort
 *
 * @author zhuzhenjie
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class HostPort {
    private String host;
    private int port;

    public static HostPort defaultServerConfig() {
        return HostPort.builder()
                .host("127.0.0.1").port(10888)
                .build();
    }

}

package org.iproute.commons.advance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * AdvanceConfig
 *
 * @author zhuzhenjie
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public final class HostPortConfig {
    private String host;
    private int port;

    public static HostPortConfig get() {
        return HostPortConfig.builder()
                .host("127.0.0.1")
                .port(10888)
                .build();
    }

}

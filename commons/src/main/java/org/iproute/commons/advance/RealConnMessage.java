package org.iproute.commons.advance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * RealConnMessage
 * <p>
 * 真实连接的信息
 *
 * @author zhuzhenjie
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class RealConnMessage {
    private String host;
    private int port;
}

package org.iproute.commons.protocol;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Msg
 *
 * @author zhuzhenjie
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class Msg {
    private boolean success;

    private HostPort hostPort;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static Msg fromJson(String json) {
        try {
            return JSON.parseObject(json, Msg.class);
        } catch (Exception e) {
            return Msg.builder().success(false).build();
        }
    }

}

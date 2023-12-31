package org.iproute.commons.protocol;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Msg
 * <p>
 * //TODO: encrypt & decrypt
 *
 * @author zhuzhenjie
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Data
public class Msg {
    private String connectTag;
    private HostPort hostPort;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public static Msg fromJson(String json) {
        try {
            return JSON.parseObject(json, Msg.class);
        } catch (Exception e) {
            return Msg.builder().connectTag(ConnectTag.OTHER).build();
        }
    }

    public interface ConnectTag {
        String TRY = "try";
        String SUCCESS = "success";
        String FAILURE = "failure";
        String OTHER = "other";
    }

}

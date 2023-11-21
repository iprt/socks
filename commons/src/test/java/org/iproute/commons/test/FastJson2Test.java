package org.iproute.commons.test;

import com.alibaba.fastjson2.JSON;
import org.iproute.commons.advance.HostPortConfig;
import org.junit.jupiter.api.Test;

/**
 * FastJson2Test
 *
 * @author zhuzhenjie
 */
public class FastJson2Test {

    @Test
    public void serialize() {
        System.out.println(
                JSON.toJSON(HostPortConfig.get())
        );
    }

    @Test
    public void deserialize() {
        final String json = """
                {"host":"127.0.0.1","port":10888}
                """;

        HostPortConfig config = JSON.parseObject(json, HostPortConfig.class);

        System.out.println(config);
    }
}

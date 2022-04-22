package io.terminus.debugger.client.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author stan
 * @date 2022/4/22
 */
@Slf4j
class MacAddressUtilTest {

    /**
     * 测试获取当前电脑的唯一标识
     */
    @Test
    public void testGetMacAddress() {
        String m1 = MacAddressUtil.getLocalMacAddress();
        log.debug(m1);
        String m2 = MacAddressUtil.getLocalMacAddress();
        log.debug(m2);
        Assertions.assertEquals(m1, m2);
    }

}

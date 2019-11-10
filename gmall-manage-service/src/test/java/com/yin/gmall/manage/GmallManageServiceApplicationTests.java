package com.yin.gmall.manage;

import com.yin.gmall.utl.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

@SpringBootTest
class GmallManageServiceApplicationTests {

    @Autowired
    RedisUtil redisUtil;

    @Test
    void contextLoads() {
        Jedis jedis = redisUtil.getJedis();

        jedis.set("test","testValue");
        System.out.println(jedis);
    }

}

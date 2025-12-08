package com.sprint.mission.discodeit.config;

import org.springframework.stereotype.Component;

@Component("keyGen")
public class CacheKeyGenerator {
    public String hashKey(Object key) {
        return Integer.toHexString(key.hashCode()); // record 거나 hashCode가 선언되어 있을때만 가능!
    }

    public String hashKey(Object... params) {
        int hash = 0;
        for(Object key : params){
            hash += key.hashCode();
        }
        return Integer.toHexString(hash);
    }
}

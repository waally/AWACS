package io.awacs.plugin.test;

import com.alibaba.fastjson.JSON;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestReport {

    private String service;

    private long time;

    public TestReport setService(String service) {
        this.service = service;
        return this;
    }


    public TestReport setTime(long time) {
        this.time = time;
        return this;
    }

    public String getService() {
        return service;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

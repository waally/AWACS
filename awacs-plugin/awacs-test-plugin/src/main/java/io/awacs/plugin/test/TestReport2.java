package io.awacs.plugin.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangli on 2016/10/26.
 */
public class TestReport2 {

    private String className;

    private Long time;

    private List<TestReport2> subs = new ArrayList<>();

    private TestReport2 parent ;

    public boolean isRoot(){
        return parent==null;
    }

    public TestReport2(Long time,String className){
        this.time = time;
        this.className = className;
    }

    public TestReport2 push(TestReport2 info){
        subs.add(info);
        info.parent = this;
        return info;
    }

    public TestReport2 pop(Long time){
        this.time = time - this.time;
        return this.parent;
    }

    @Override
    public String toString() {
        return "{'className':'" + className + "', 'subs':'" + subs + "', 'time':'" + time + "'}";
    }

}

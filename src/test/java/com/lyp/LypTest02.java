package com.lyp;

import com.lyp.test.SpringBuildService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LypTest02 {

    @Autowired
    private SpringBuildService buildService;

    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Test
    public void test() throws ClassNotFoundException {
        System.out.println(buildService.getService(Class.forName("com.lyp.test.entity.Borrw")).list());
    }
}

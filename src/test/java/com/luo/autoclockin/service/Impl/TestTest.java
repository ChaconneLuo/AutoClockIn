package com.luo.autoclockin.service.Impl;

import com.luo.autoclockin.config.SpringConfig;
import com.luo.autoclockin.service.StudentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class TestTest {
    @Autowired
    private StudentService studentService;

    @Test
    public void Test() throws InterruptedException {
        studentService.AllClockIn();
    }
}

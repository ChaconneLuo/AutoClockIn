package com.luo.autoclockin.service.Task;

import com.luo.autoclockin.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class StartTask {

    private final StudentService studentService;

    @PostConstruct
    public void reload() throws InterruptedException {
        //load();                           //第一次启动时是否先完成一次打卡操作
    }

    @Scheduled(cron = "0 15 7,17 * * ?") //学校服务器时间不准，延迟每天7：15与17：15打卡
    public void load() throws InterruptedException {
        studentService.AllClockIn();
    }

}

package com.luo.autoclockin.controller;

import com.luo.autoclockin.entity.Student;
import com.luo.autoclockin.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class InformationController {
    private final StudentService studentService;

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/upload", params = {"stu_id", "url"})
    public String Upload(Student stu){
        if (studentService.AddUser(stu)) {
            return "success";
        }else{
            return "failure";
        }
    }

    @RequestMapping(value = "/hand")
    public String Hand() throws InterruptedException {
        studentService.AllClockIn();
        return "success";
    }
}

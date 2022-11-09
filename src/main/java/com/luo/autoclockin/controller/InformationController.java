package com.luo.autoclockin.controller;

import com.luo.autoclockin.entity.Student;
import com.luo.autoclockin.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Controller
@RequiredArgsConstructor
public class InformationController {
    private final StudentService studentService;

    @RequestMapping("/index")
    public ModelAndView index(){
        return new ModelAndView("/index.html");
    }

    @RequestMapping(value = "/upload", params = {"stu_id", "url"})
    public ModelAndView Upload(Student stu){
        ModelAndView modelAndView;
        if (studentService.AddUser(stu)) {
            modelAndView = new ModelAndView("/success.html");
        }else{

            modelAndView = new ModelAndView("/failure.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/hand")
    public ModelAndView Hand(Student stu) throws InterruptedException {
        ModelAndView modelAndView;
        studentService.AllClockIn();
        modelAndView = new ModelAndView("/success.html");

        return modelAndView;
    }
}

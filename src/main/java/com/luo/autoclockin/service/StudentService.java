package com.luo.autoclockin.service;

import com.luo.autoclockin.entity.Student;

public interface StudentService {
    public boolean AddUser(Student stu);
    public void AllClockIn() throws InterruptedException;
    public boolean check(String stu_id,String url);
}

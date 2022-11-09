package com.luo.autoclockin.mapper;

import com.luo.autoclockin.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    void insert(Student stu);
    Student select(String stu_id);
    List<Student> getAllStudent();
}

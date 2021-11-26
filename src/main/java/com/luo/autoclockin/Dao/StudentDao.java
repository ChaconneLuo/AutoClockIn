package com.luo.autoclockin.Dao;

import com.luo.autoclockin.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentDao {

    @Insert("insert into data (stu_id,url) values (#{stu_id},#{url})")
    public void insert(Student stu);

    @Select("select * from data where stu_id = #{stu_id}")
//    @Results(id = "search",value = {@Result(column = "stu_id",property = "stu_id"),@Result(column = "url",property = "url")})
    public Student search(String stu_id);

    @Update("update data set url = #{stu.url}")
    public void update(Student stu);

    @Select("select * from data")
    public List<Student> getAllStudent();
}

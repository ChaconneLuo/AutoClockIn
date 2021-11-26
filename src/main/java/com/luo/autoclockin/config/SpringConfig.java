package com.luo.autoclockin.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(value = "com.luo.autoclockin", excludeFilters = @ComponentScan.Filter(Controller.class))
@Import(JdbcConfig.class)
public class SpringConfig {

}

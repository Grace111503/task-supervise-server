package com.enterprise.tasksuperviseserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 任务督办服务启动类
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class TaskSuperviseServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskSuperviseServerApplication.class, args);
	}

}

package com.umc.approval;

import com.umc.approval.global.type.SocialType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApprovalApplication.class, args);
	}

}

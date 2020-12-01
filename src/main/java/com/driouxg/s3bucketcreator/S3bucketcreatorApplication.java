package com.driouxg.s3bucketcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(scanBasePackages = "com.driouxg.s3bucketcreator")
public class S3bucketcreatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(S3bucketcreatorApplication.class, args);
  }
}

package com.driouxg.s3bucketcreator.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Data
public class S3Config {

  private String url;
  private List<String> keysToBeCreated;
  private String bucketName;
}

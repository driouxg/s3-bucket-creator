package com.driouxg.s3bucketcreator.config;

import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public AmazonS3 amazonS3(S3Config s3Config, @Value("cloud.aws.region.static") String region,
      @Value("cloud.aws.credentials.access-key") String accessKey,
      @Value("cloud.aws.credentials.secret-key") String secretKey) {
    System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");

    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    EndpointConfiguration epc = new EndpointConfiguration(s3Config.getUrl(), region);

    return AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(epc)
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .enablePathStyleAccess()
        .build();
  }
}

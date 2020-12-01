package com.driouxg.s3bucketcreator.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.driouxg.s3bucketcreator.config.S3Config;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

  private AmazonS3 amazonS3;
  private S3Config s3Config;
  private RetryTemplate retryTemplate;

  public DashboardController(S3Config s3Config, AmazonS3 amazonS3, RetryTemplate retryTemplate) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
    this.retryTemplate = retryTemplate;
    configureS3();
  }

  private void configureS3() {
    retryTemplate.execute(arg -> {
      LOGGER.info("Creating bucket: " + s3Config.getBucketName());
      amazonS3.createBucket(s3Config.getBucketName());
      LOGGER.info("Creating keys: " + s3Config.getKeysToBeCreated());
      createFolders();
      LOGGER.info("Created buckets and keys.");
      amazonS3.listBuckets().forEach(b -> System.out.println(b.getName()));

      arg.setExhaustedOnly();

      return null;
    });
  }

  private void createFolders() {
    s3Config.getKeysToBeCreated().forEach(b -> {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(0);
      // create empty content
      InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
      // create a PutObjectRequest passing the folder name suffixed by /
      PutObjectRequest putObjectRequest = new PutObjectRequest(s3Config.getBucketName(),
          "/" + b + "/", emptyContent, metadata);
      // send request to S3 to create folder
      amazonS3.putObject(putObjectRequest);
    });
  }

  @GetMapping("/")
  public ModelAndView getDashboardView() {
    ModelAndView modelAndView = new ModelAndView("dashboard");
    modelAndView.addObject("message", "Spring Boot with AWS");
    modelAndView.addObject("bucketName", s3Config.getBucketName());
    modelAndView
        .addObject("bucketLocation", amazonS3.getBucketLocation(s3Config.getBucketName()));
    modelAndView
        .addObject("availableFiles",
            amazonS3.listObjects(s3Config.getBucketName()).getObjectSummaries());
    return modelAndView;
  }
}

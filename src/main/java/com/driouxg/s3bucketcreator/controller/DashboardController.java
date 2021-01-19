package com.driouxg.s3bucketcreator.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.driouxg.s3bucketcreator.config.S3Config;
import com.driouxg.s3bucketcreator.service.FolderCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

  private AmazonS3 amazonS3;
  private S3Config s3Config;

  public DashboardController(S3Config s3Config, AmazonS3 amazonS3, FolderCreator folderCreator) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
    folderCreator.configureS3();
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

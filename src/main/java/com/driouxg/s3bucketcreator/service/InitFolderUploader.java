package com.driouxg.s3bucketcreator.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.driouxg.s3bucketcreator.config.S3Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Uploads the initialization folder "init" that is found in the home directory.
 */
@Component
public class InitFolderUploader {

  private static final Logger LOGGER = LoggerFactory.getLogger(InitFolderUploader.class);

  private AmazonS3 amazonS3;
  private S3Config s3Config;

  public InitFolderUploader(AmazonS3 amazonS3, S3Config s3Config) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
  }

  public void uploadInitFolder() {
    File file = new File("~/init/");

    if (file.exists()) {
      LOGGER.info("init folder exists. Uploading contents to S3 bucket.");
      go();
    } else {
      LOGGER.info("init folder does not exist.");
    }
  }

  private void go() {
    List<String> filePaths = getAllFilePaths("~/init/", new ArrayList<>());

    for (String path : filePaths) {
      try {
        PutObjectRequest request = new PutObjectRequest(s3Config.getBucketName(), path,
            new FileInputStream(new File(path)), new ObjectMetadata());
        amazonS3.putObject(request);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private List<String> getAllFilePaths(String path, List<String> paths) {
    File currentDirectory = new File(path);
    File[] filesList = currentDirectory.listFiles();

    if (filesList == null) {
      return paths;
    }

    for (File file : filesList) {
      if (file.isFile()) {
        paths.add(file.getPath());
      }
    }

    for (File file : filesList) {
      if (file.isDirectory()) {
        getAllFilePaths(file.getPath(), paths);
      }
    }

    return paths;
  }
}

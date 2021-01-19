package com.driouxg.s3bucketcreator.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.driouxg.s3bucketcreator.config.S3Config;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class FolderCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(FolderCreator.class);

  private AmazonS3 amazonS3;
  private S3Config s3Config;
  private RetryTemplate retryTemplate;
  private InitFolderUploader initFolderUploader;

  public FolderCreator(AmazonS3 amazonS3, S3Config s3Config,
      RetryTemplate retryTemplate, InitFolderUploader initFolderUploader) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
    this.retryTemplate = retryTemplate;
    this.initFolderUploader = initFolderUploader;
  }

  public void configureS3() {
    retryTemplate.execute(arg -> {
      if (arg.getLastThrowable() != null) {
        LOGGER.error("Retrying s3 initialization due to:", arg.getLastThrowable());
      }

      LOGGER.info("Creating bucket: " + s3Config.getBucketName());
      amazonS3.createBucket(s3Config.getBucketName());
      LOGGER.info("Creating keys: " + s3Config.getKeysToBeCreated());
      createFolders();
      LOGGER.info("Created buckets and keys.");

      initFolderUploader.uploadInitFolder();

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
}

# S3 Bucket Creator

## Overview

This application allows you to initialize an S3 bucket and its subdirectories for testing purposes. This application has been containerized and can be used with docker or docker-compose. This application also provides a ui dashboard into the S3 server that it connects to. This application will retry indefinitely to create the bucket and subdirectories until it is successful.

## Official GitHub repository

https://github.com/driouxg/s3-bucket-creator

## DockerHub Link

https://hub.docker.com/repository/docker/driouxg/s3-bucket-creator

## UI Dashboard

The ui dashboard is reachable at the configured host `localhost:SERVER_PORT`. So, if you configured the SERVER_PORT environment variable to `8000` the ui dashboard would be visible at `localhost:8000`.

## Init Folder

You can initialize your S3 bucket with files on creation by mounting files into the `~/init/` directory.

```dockerfile
version '3.7'

services:
    amazon-web-services:
      image: localstack/localstack-full
      ports:
      - "4566:4566"
      - "3000:3000"
      volumes:
      - ./amazon-web-services/init/:~/init/
```

## Docker Compose

This application can be used with docker compose. Example docker-compose usage with localstack S3 instance:

```dockerfile
version: '3.7'

services:
    amazon-web-services:
      image: localstack/localstack-full
      ports:
      - "4566:4566"
      - "3000:3000"
      environment:
      - SERVICES=s3
      - DEFAULT_REGION=us-west-2
      - PORT_WEB_UI=3000

    s3-bucket-creator:
      image: driouxg/s3-bucket-creator:
      ports:
      - "8080:8080"
      environment:
      - AWS_S3_URL=amazon-web-services:4566
      - AWS_BUCKET_NAME=myBucketName
      - AWS_KEYS_TO_BE_CREATED=subdirectory1,subdirectory1/nestedSubdirectory
      - AWS_ACCESS_KEY=test
      - AWS_SECRET_KEY=test

```

## Environment Variables

| Environment Variable   | Default                      | Type            | Description                                       |
|----------------------- |------------------------------|-----------------|---------------------------------------------------|
| AWS_BUCKET_REGION      | us-west-2                    | String          | The region that the aws client will attempt to connect to the aws resource  |
| AWS_ACCESS_KEY         | test                         | String          | The access key used to authenticate with the aws s3 endpoint           |
| AWS_SECRET_KEY         | test                         | String          | The secret key used to authenticate with the aws s3 endpoint  |
| AWS_S3_URL             | localhost:4566               | String          | The url for the s3 service                                    |
| AWS_BUCKET_NAME        | home                         | String          | The name of the bucket that will be created in the s3 endpoint |
| AWS_KEYS_TO_BE_CREATED | bucket1,bucket1/subDirectory | List of Strings | The subdirectories that will be created inside of the bucket |
| SERVER_PORT            | 8080                         | Integer         | The port for the spring boot application to run on |
package com.hayba.microservices.twitter.to.kafka.service;

import com.hayba.microservices.config.TwitterToKafkaServiceConfigData;
import com.hayba.microservices.twitter.to.kafka.service.init.StreamInitializer;
import com.hayba.microservices.twitter.to.kafka.service.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = "com.hayba.microservices")
@RequiredArgsConstructor
public class TwitterToKafkaServiceApplication implements CommandLineRunner {
  private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
  private final StreamRunner streamRunner;
  private final StreamInitializer streamInitializer;


  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("App starts......");
    streamInitializer.init();
    streamRunner.start();
  }
}

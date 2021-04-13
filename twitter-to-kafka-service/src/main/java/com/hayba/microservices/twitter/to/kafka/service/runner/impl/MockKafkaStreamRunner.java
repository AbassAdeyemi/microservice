package com.hayba.microservices.twitter.to.kafka.service.runner.impl;

import com.hayba.microservices.config.TwitterToKafkaServiceConfigData;
import com.hayba.microservices.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.hayba.microservices.twitter.to.kafka.service.runner.StreamRunner;
//import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "true")
public class MockKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

    private final TwitterToKafkaServiceConfigData configData;
    private final TwitterKafkaStatusListener statusListener;
    

    @Autowired
    public MockKafkaStreamRunner(TwitterToKafkaServiceConfigData configData,
			TwitterKafkaStatusListener statusListener) {
		this.configData = configData;
		this.statusListener = statusListener;
	}

	private static final Random RANDOM = new Random();

    private final String [] WORDS = new String[] {
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetuer",
            "adipiscing",
            "elit",
            "Maecenas",
            "porttitor",
            "congue",
            "massa",
            "Fusce",
            "posuere",
            "magna",
            "sed",
            "pulvinar",
            "ultricies",
            "purus",
            "lectus",
            "malesuada",
            "libero"

    };

    private static final String tweetAsRawJson = "{" +
            "\"created_at\":\"{0}\"," +
            "\"id\":\"{1}\"," +
            "\"text\":\"{2}\"," +
            "\"user\":{\"id\":\"{3}\"}" +
            "}";

    private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    @Override
    public void start() throws TwitterException {
        String [] keywords = configData.getTwitterKeywords().toArray(new String[]{});
        int minTweetLength = configData.getMockTweetMinLength();
        int maxTweetLength = configData.getMockTweetMaxLength();
        long sleepTimeMs = configData.getMockSleepMs();
    LOG.info("Starting mock filtering twitter streams for keyword: {}", Arrays.toString(keywords));

        simulateTwitterStream(keywords, minTweetLength, maxTweetLength, sleepTimeMs);
        return;
    }

    private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetLength, long sleepTimeMs) {
        Executors.newSingleThreadExecutor().submit(() -> {

            try{
            while(true) {
                String formattedTweetAsRawJson = getFormattedTweets(keywords, minTweetLength, maxTweetLength);
                Status status = TwitterObjectFactory.createStatus(formattedTweetAsRawJson);
                statusListener.onStatus(status);
                sleep(sleepTimeMs);
                }
            }
            catch (TwitterException e) {
                LOG.error("Error creating twitter status !!!");
            }
        });

    }

    private void sleep(long sleepTimeMs) {
        try{
            Thread.sleep(sleepTimeMs);
        }
        catch (InterruptedException ex ) {
            throw new RuntimeException("Error while sleeping on creating new status!!");
        }
    }

    private String getFormattedTweets(String [] keywords, int minTweetLength, int maxTweetLength) {
    String[] params =
        new String[] {
          ZonedDateTime.now()
              .format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
          String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
          getRandomTweetContent(keywords, minTweetLength, maxTweetLength),
          String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE))
        };
        String tweet = tweetAsRawJson;

        for(int i=0; i<params.length; i++) {
            tweet = tweet.replace("{"+i+"}", params[i]);
        }

        return tweet;
    }

    private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetLength) {
        StringBuilder tweet = new StringBuilder();
        int tweetLength = RANDOM.nextInt(maxTweetLength - minTweetLength + 1) + minTweetLength;
        constructRandomTweet(keywords, tweet, tweetLength);

        return tweet.toString().trim();
    }

    private void constructRandomTweet(String[] keywords, StringBuilder tweet, int tweetLength) {
        for(int i =0; i<tweetLength; i++) {
            tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
            if(i == tweetLength /2) {
                tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
            }
        }
    }
}
package com.santiago.bookstore.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A custom Logback appender that sends logs to AWS CloudWatch Logs using AWS SDK v2.
 * It uses a background thread to send logs in batches to avoid blocking the application.
 */
public class CloudWatchAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String logGroupName;
    private String logStreamName;
    private String region;

    private CloudWatchLogsClient client;
    private final BlockingQueue<InputLogEvent> queue = new LinkedBlockingQueue<>(1000);
    private volatile boolean running = true;
    private Thread worker;
    private String sequenceToken;

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public void setLogStreamName(String logStreamName) {
        this.logStreamName = logStreamName;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void start() {
        try {
            client = CloudWatchLogsClient.builder()
                    .region(Region.of(region != null ? region : "us-east-1"))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();

            // Create Log Group if not exists
            try {
                client.createLogGroup(CreateLogGroupRequest.builder().logGroupName(logGroupName).build());
            } catch (ResourceAlreadyExistsException e) {
                // ignore
            }

            // Create Log Stream if not exists
            try {
                client.createLogStream(CreateLogStreamRequest.builder().logGroupName(logGroupName).logStreamName(logStreamName).build());
            } catch (ResourceAlreadyExistsException e) {
                // ignore
            }

            worker = new Thread(this::processQueue);
            worker.setDaemon(true);
            worker.start();

            super.start();
        } catch (Exception e) {
            addError("Failed to initialize CloudWatchAppender", e);
        }
    }

    @Override
    public void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
        }
        if (client != null) {
            client.close();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) return;

        String message = new String(encoder.encode(eventObject), StandardCharsets.UTF_8);
        InputLogEvent logEvent = InputLogEvent.builder()
                .message(message)
                .timestamp(eventObject.getTimeStamp())
                .build();

        queue.offer(logEvent);
    }

    // We need an encoder to format the message
    private ch.qos.logback.core.encoder.Encoder<ILoggingEvent> encoder;

    public void setEncoder(ch.qos.logback.core.encoder.Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    private void processQueue() {
        while (running) {
            try {
                InputLogEvent event = queue.take();
                LinkedList<InputLogEvent> batch = new LinkedList<>();
                batch.add(event);
                queue.drainTo(batch, 19); // Max 20 events per batch for simplicity

                PutLogEventsRequest.Builder requestBuilder = PutLogEventsRequest.builder()
                        .logGroupName(logGroupName)
                        .logStreamName(logStreamName)
                        .logEvents(batch);

                if (sequenceToken != null) {
                    requestBuilder.sequenceToken(sequenceToken);
                }

                try {
                    PutLogEventsResponse response = client.putLogEvents(requestBuilder.build());
                    sequenceToken = response.nextSequenceToken();
                } catch (InvalidSequenceTokenException e) {
                    sequenceToken = e.expectedSequenceToken();
                    // Retry with new token
                    requestBuilder.sequenceToken(sequenceToken);
                    PutLogEventsResponse response = client.putLogEvents(requestBuilder.build());
                    sequenceToken = response.nextSequenceToken();
                } catch (DataAlreadyAcceptedException e) {
                    sequenceToken = e.expectedSequenceToken();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                addError("Failed to send logs to CloudWatch", e);
                // Backoff could be implemented here
            }
        }
    }
}

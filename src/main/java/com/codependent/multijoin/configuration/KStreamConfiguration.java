package com.codependent.multijoin.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.time.Duration;
import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.*;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class KStreamConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = Map.of(
                APPLICATION_ID_CONFIG, "multijoin",
                BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, String> joinedKStream(StreamsBuilder kStreamBuilder) {
        KStream<String, String> streamX = kStreamBuilder.stream("x");
        streamX.peek((k, v) -> logger.info("x - key {} - value {}", k, v));
        KStream<String, String> streamY = kStreamBuilder.stream("y");
        streamY.peek((k, v) -> logger.info("y - key {} - value {}", k, v));
        KStream<String, String> streamZ = kStreamBuilder.stream("z");
        streamZ.peek((k, v) -> logger.info("z - key {} - value {}", k, v));

        //JoinWindows joinWindow = JoinWindows.of(Duration.ofSeconds(5)).grace(Duration.ofSeconds(1));
        JoinWindows joinWindow = JoinWindows.ofTimeDifferenceAndGrace(Duration.ofSeconds(5), Duration.ofSeconds(10));

        KStream<String, String> stream = streamX
                .outerJoin(streamY, (k, s, s2) -> {
                    logger.info("Join x + y: key {} - values {}-{}", k, s, s2);
                    return s + s2;
                }, joinWindow)
                .outerJoin(streamZ, (k, s, s2) -> {
                    logger.info("Join xy + z: key {} - values {}-{}", k, s, s2);
                    return s + s2;
                }, joinWindow);
        stream.foreach((key, value) -> logger.info("Merged key {} - value {}", key, value));
        stream.to("merged");
        return stream;
    }

}

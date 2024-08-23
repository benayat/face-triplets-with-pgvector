package org.benaya.ai.parquet_to_postgres_triplets.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
@Slf4j
public class ArrowReaderConfig {

    @Bean
    public FileInputStream fileInputStream() {
        try {
            return new FileInputStream("src/main/resources/parquet/parquet_triplets.parquet");
        } catch (Exception e) {
            log.error("Error while reading file: {}", e.getMessage());
            return null;
        }
    }
    @Bean
    public SeekableReadChannel seekableReadChannel(FileInputStream fileInputStream) {
        return new SeekableReadChannel(fileInputStream.getChannel());
    }
    @Bean
    public RootAllocator rootAllocator() {
        return new RootAllocator(Long.MAX_VALUE);
    }
    @Bean
    public ArrowFileReader arrowReaderConfig() {
        return new ArrowFileReader(seekableReadChannel(fileInputStream()), rootAllocator());
    }
}

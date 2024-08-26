package org.benaya.ai.parquet_to_postgres_triplets.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.ipc.ArrowFileReader;
import org.apache.arrow.vector.ipc.SeekableReadChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class ArrowReaderConfig {
    @Value("${arrow.folder-path}")
    private String arrowFolderPath;

    @PostConstruct
    public void init() {
        if (arrowFolderPath == null || arrowFolderPath.isEmpty()) {
            throw new IllegalArgumentException("arrow.folder-path property is not set in application.yml");
        }
    }
    @Bean
    public List<Path> arrowFilePaths() throws IOException {
        try (Stream<Path> paths = Files.walk(Path.of(arrowFolderPath))) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".arrow"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }
    @Bean
    public RootAllocator rootAllocator() {
        return new RootAllocator(Long.MAX_VALUE);
    }
}

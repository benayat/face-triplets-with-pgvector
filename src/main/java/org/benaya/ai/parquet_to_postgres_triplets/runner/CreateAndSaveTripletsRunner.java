package org.benaya.ai.parquet_to_postgres_triplets.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benaya.ai.parquet_to_postgres_triplets.service.TripletService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAndSaveTripletsRunner implements ApplicationRunner {

    private final TripletService tripletService;
        @Override
        public void run(ApplicationArguments args) throws Exception {
            log.info("in CreateAndSaveTripletsRunner, Creating and saving triplets");
            tripletService.processAllLabelToEmbeddings();
        }
}

//package org.benaya.ai.parquet_to_postgres_triplets.runner;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.benaya.ai.parquet_to_postgres_triplets.service.ArrowService;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class loadArrowFileRunner implements ApplicationRunner {
//
//    private final ArrowService arrowService;
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        // TODO Auto-generated method stub
//        log.info("Loading Arrow file");
//        arrowService.readArrowFilesWriteToDb();
//    }
//}

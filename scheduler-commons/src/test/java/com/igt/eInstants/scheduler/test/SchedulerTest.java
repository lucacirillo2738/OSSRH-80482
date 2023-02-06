package com.igt.eInstants.scheduler.test;

import com.igt.eInstants.scheduler.JobScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SchedulerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchedulerTest {

    @Autowired
    private JobScheduler jobScheduler;

    @Test
    public void test() throws InterruptedException {
        jobScheduler.startScheduledJob("TEST", "*/1 * * * * *", () -> {
            System.out.println(String.format("Hi it's {}", System.currentTimeMillis()));
            return RepeatStatus.FINISHED;
        });
        Thread.sleep(5000);
    }
}

package me.tudu.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Task getTaskSample1() {
        return new Task().id(1L).title("title1").description("description1").priority("priority1").status("status1").category("category1");
    }

    public static Task getTaskSample2() {
        return new Task().id(2L).title("title2").description("description2").priority("priority2").status("status2").category("category2");
    }

    public static Task getTaskRandomSampleGenerator() {
        return new Task()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .priority(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString());
    }
}

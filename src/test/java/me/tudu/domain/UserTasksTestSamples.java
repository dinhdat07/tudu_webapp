package me.tudu.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserTasksTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserTasks getUserTasksSample1() {
        return new UserTasks().id(1L).privilege("privilege1");
    }

    public static UserTasks getUserTasksSample2() {
        return new UserTasks().id(2L).privilege("privilege2");
    }

    public static UserTasks getUserTasksRandomSampleGenerator() {
        return new UserTasks().id(longCount.incrementAndGet()).privilege(UUID.randomUUID().toString());
    }
}

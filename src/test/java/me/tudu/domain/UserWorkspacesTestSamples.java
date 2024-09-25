package me.tudu.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserWorkspacesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserWorkspaces getUserWorkspacesSample1() {
        return new UserWorkspaces().id(1L).privilege("privilege1");
    }

    public static UserWorkspaces getUserWorkspacesSample2() {
        return new UserWorkspaces().id(2L).privilege("privilege2");
    }

    public static UserWorkspaces getUserWorkspacesRandomSampleGenerator() {
        return new UserWorkspaces().id(longCount.incrementAndGet()).privilege(UUID.randomUUID().toString());
    }
}

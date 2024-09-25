package me.tudu.domain;

import static me.tudu.domain.TaskTestSamples.*;
import static me.tudu.domain.UserTasksTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserTasksTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTasks.class);
        UserTasks userTasks1 = getUserTasksSample1();
        UserTasks userTasks2 = new UserTasks();
        assertThat(userTasks1).isNotEqualTo(userTasks2);

        userTasks2.setId(userTasks1.getId());
        assertThat(userTasks1).isEqualTo(userTasks2);

        userTasks2 = getUserTasksSample2();
        assertThat(userTasks1).isNotEqualTo(userTasks2);
    }

    @Test
    void taskTest() {
        UserTasks userTasks = getUserTasksRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        userTasks.setTask(taskBack);
        assertThat(userTasks.getTask()).isEqualTo(taskBack);

        userTasks.task(null);
        assertThat(userTasks.getTask()).isNull();
    }
}

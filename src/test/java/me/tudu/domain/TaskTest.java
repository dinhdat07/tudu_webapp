package me.tudu.domain;

import static me.tudu.domain.TaskTestSamples.*;
import static me.tudu.domain.WorkspaceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void workspaceTest() {
        Task task = getTaskRandomSampleGenerator();
        Workspace workspaceBack = getWorkspaceRandomSampleGenerator();

        task.setWorkspace(workspaceBack);
        assertThat(task.getWorkspace()).isEqualTo(workspaceBack);

        task.workspace(null);
        assertThat(task.getWorkspace()).isNull();
    }
}

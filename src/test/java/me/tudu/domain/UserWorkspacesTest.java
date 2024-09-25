package me.tudu.domain;

import static me.tudu.domain.UserWorkspacesTestSamples.*;
import static me.tudu.domain.WorkspaceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserWorkspacesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserWorkspaces.class);
        UserWorkspaces userWorkspaces1 = getUserWorkspacesSample1();
        UserWorkspaces userWorkspaces2 = new UserWorkspaces();
        assertThat(userWorkspaces1).isNotEqualTo(userWorkspaces2);

        userWorkspaces2.setId(userWorkspaces1.getId());
        assertThat(userWorkspaces1).isEqualTo(userWorkspaces2);

        userWorkspaces2 = getUserWorkspacesSample2();
        assertThat(userWorkspaces1).isNotEqualTo(userWorkspaces2);
    }

    @Test
    void workspaceTest() {
        UserWorkspaces userWorkspaces = getUserWorkspacesRandomSampleGenerator();
        Workspace workspaceBack = getWorkspaceRandomSampleGenerator();

        userWorkspaces.setWorkspace(workspaceBack);
        assertThat(userWorkspaces.getWorkspace()).isEqualTo(workspaceBack);

        userWorkspaces.workspace(null);
        assertThat(userWorkspaces.getWorkspace()).isNull();
    }
}

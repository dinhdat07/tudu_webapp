package me.tudu.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserWorkspacesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserWorkspacesDTO.class);
        UserWorkspacesDTO userWorkspacesDTO1 = new UserWorkspacesDTO();
        userWorkspacesDTO1.setId(1L);
        UserWorkspacesDTO userWorkspacesDTO2 = new UserWorkspacesDTO();
        assertThat(userWorkspacesDTO1).isNotEqualTo(userWorkspacesDTO2);
        userWorkspacesDTO2.setId(userWorkspacesDTO1.getId());
        assertThat(userWorkspacesDTO1).isEqualTo(userWorkspacesDTO2);
        userWorkspacesDTO2.setId(2L);
        assertThat(userWorkspacesDTO1).isNotEqualTo(userWorkspacesDTO2);
        userWorkspacesDTO1.setId(null);
        assertThat(userWorkspacesDTO1).isNotEqualTo(userWorkspacesDTO2);
    }
}

package me.tudu.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkspaceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkspaceDTO.class);
        WorkspaceDTO workspaceDTO1 = new WorkspaceDTO();
        workspaceDTO1.setId(1L);
        WorkspaceDTO workspaceDTO2 = new WorkspaceDTO();
        assertThat(workspaceDTO1).isNotEqualTo(workspaceDTO2);
        workspaceDTO2.setId(workspaceDTO1.getId());
        assertThat(workspaceDTO1).isEqualTo(workspaceDTO2);
        workspaceDTO2.setId(2L);
        assertThat(workspaceDTO1).isNotEqualTo(workspaceDTO2);
        workspaceDTO1.setId(null);
        assertThat(workspaceDTO1).isNotEqualTo(workspaceDTO2);
    }
}

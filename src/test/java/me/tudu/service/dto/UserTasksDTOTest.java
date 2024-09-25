package me.tudu.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.tudu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserTasksDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTasksDTO.class);
        UserTasksDTO userTasksDTO1 = new UserTasksDTO();
        userTasksDTO1.setId(1L);
        UserTasksDTO userTasksDTO2 = new UserTasksDTO();
        assertThat(userTasksDTO1).isNotEqualTo(userTasksDTO2);
        userTasksDTO2.setId(userTasksDTO1.getId());
        assertThat(userTasksDTO1).isEqualTo(userTasksDTO2);
        userTasksDTO2.setId(2L);
        assertThat(userTasksDTO1).isNotEqualTo(userTasksDTO2);
        userTasksDTO1.setId(null);
        assertThat(userTasksDTO1).isNotEqualTo(userTasksDTO2);
    }
}

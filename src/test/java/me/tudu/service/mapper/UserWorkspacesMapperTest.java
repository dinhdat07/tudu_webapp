package me.tudu.service.mapper;

import static me.tudu.domain.UserWorkspacesAsserts.*;
import static me.tudu.domain.UserWorkspacesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserWorkspacesMapperTest {

    private UserWorkspacesMapper userWorkspacesMapper;

    @BeforeEach
    void setUp() {
        userWorkspacesMapper = new UserWorkspacesMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserWorkspacesSample1();
        var actual = userWorkspacesMapper.toEntity(userWorkspacesMapper.toDto(expected));
        assertUserWorkspacesAllPropertiesEquals(expected, actual);
    }
}

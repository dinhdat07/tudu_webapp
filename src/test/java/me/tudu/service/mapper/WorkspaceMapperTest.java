package me.tudu.service.mapper;

import static me.tudu.domain.WorkspaceAsserts.*;
import static me.tudu.domain.WorkspaceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkspaceMapperTest {

    private WorkspaceMapper workspaceMapper;

    @BeforeEach
    void setUp() {
        workspaceMapper = new WorkspaceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkspaceSample1();
        var actual = workspaceMapper.toEntity(workspaceMapper.toDto(expected));
        assertWorkspaceAllPropertiesEquals(expected, actual);
    }
}

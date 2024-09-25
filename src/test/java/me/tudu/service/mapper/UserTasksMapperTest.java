package me.tudu.service.mapper;

import static me.tudu.domain.UserTasksAsserts.*;
import static me.tudu.domain.UserTasksTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTasksMapperTest {

    private UserTasksMapper userTasksMapper;

    @BeforeEach
    void setUp() {
        userTasksMapper = new UserTasksMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserTasksSample1();
        var actual = userTasksMapper.toEntity(userTasksMapper.toDto(expected));
        assertUserTasksAllPropertiesEquals(expected, actual);
    }
}

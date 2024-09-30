package me.tudu.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAllPropertiesEquals(Task expected, Task actual) {
        assertTaskAutoGeneratedPropertiesEquals(expected, actual);
        assertTaskAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAllUpdatablePropertiesEquals(Task expected, Task actual) {
        assertTaskUpdatableFieldsEquals(expected, actual);
        assertTaskUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskAutoGeneratedPropertiesEquals(Task expected, Task actual) {
        assertThat(expected)
            .as("Verify Task auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskUpdatableFieldsEquals(Task expected, Task actual) {
        assertThat(expected)
            .as("Verify Task relevant properties")
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()))
            .satisfies(e -> assertThat(e.getDescription()).as("check description").isEqualTo(actual.getDescription()))
            .satisfies(e -> assertThat(e.getDueDate()).as("check dueDate").isEqualTo(actual.getDueDate()))
            .satisfies(e -> assertThat(e.getPriority()).as("check priority").isEqualTo(actual.getPriority()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()))
            .satisfies(e -> assertThat(e.getCategory()).as("check category").isEqualTo(actual.getCategory()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getUpdatedAt()).as("check updatedAt").isEqualTo(actual.getUpdatedAt()))
            .satisfies(e -> assertThat(e.getPrivilege()).as("check privilege").isEqualTo(actual.getPrivilege()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTaskUpdatableRelationshipsEquals(Task expected, Task actual) {
        assertThat(expected)
            .as("Verify Task relationships")
            .satisfies(e -> assertThat(e.getWorkspace()).as("check workspace").isEqualTo(actual.getWorkspace()));
    }
}

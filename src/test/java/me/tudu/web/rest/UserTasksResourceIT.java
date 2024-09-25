package me.tudu.web.rest;

import static me.tudu.domain.UserTasksAsserts.*;
import static me.tudu.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import me.tudu.IntegrationTest;
import me.tudu.domain.UserTasks;
import me.tudu.repository.UserRepository;
import me.tudu.repository.UserTasksRepository;
import me.tudu.repository.search.UserTasksSearchRepository;
import me.tudu.service.dto.UserTasksDTO;
import me.tudu.service.mapper.UserTasksMapper;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserTasksResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserTasksResourceIT {

    private static final String DEFAULT_PRIVILEGE = "AAAAAAAAAA";
    private static final String UPDATED_PRIVILEGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-tasks/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserTasksRepository userTasksRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTasksMapper userTasksMapper;

    @Autowired
    private UserTasksSearchRepository userTasksSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserTasksMockMvc;

    private UserTasks userTasks;

    private UserTasks insertedUserTasks;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTasks createEntity() {
        return new UserTasks().privilege(DEFAULT_PRIVILEGE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTasks createUpdatedEntity() {
        return new UserTasks().privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        userTasks = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserTasks != null) {
            userTasksRepository.delete(insertedUserTasks);
            userTasksSearchRepository.delete(insertedUserTasks);
            insertedUserTasks = null;
        }
    }

    @Test
    @Transactional
    void createUserTasks() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);
        var returnedUserTasksDTO = om.readValue(
            restUserTasksMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userTasksDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserTasksDTO.class
        );

        // Validate the UserTasks in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserTasks = userTasksMapper.toEntity(returnedUserTasksDTO);
        assertUserTasksUpdatableFieldsEquals(returnedUserTasks, getPersistedUserTasks(returnedUserTasks));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserTasks = returnedUserTasks;
    }

    @Test
    @Transactional
    void createUserTasksWithExistingId() throws Exception {
        // Create the UserTasks with an existing ID
        userTasks.setId(1L);
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserTasksMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userTasksDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUserTasks() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);

        // Get all the userTasksList
        restUserTasksMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTasks.getId().intValue())))
            .andExpect(jsonPath("$.[*].privilege").value(hasItem(DEFAULT_PRIVILEGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserTasks() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);

        // Get the userTasks
        restUserTasksMockMvc
            .perform(get(ENTITY_API_URL_ID, userTasks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userTasks.getId().intValue()))
            .andExpect(jsonPath("$.privilege").value(DEFAULT_PRIVILEGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserTasks() throws Exception {
        // Get the userTasks
        restUserTasksMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserTasks() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userTasksSearchRepository.save(userTasks);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());

        // Update the userTasks
        UserTasks updatedUserTasks = userTasksRepository.findById(userTasks.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserTasks are not directly saved in db
        em.detach(updatedUserTasks);
        updatedUserTasks.privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(updatedUserTasks);

        restUserTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userTasksDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userTasksDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserTasksToMatchAllProperties(updatedUserTasks);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserTasks> userTasksSearchList = Streamable.of(userTasksSearchRepository.findAll()).toList();
                UserTasks testUserTasksSearch = userTasksSearchList.get(searchDatabaseSizeAfter - 1);

                assertUserTasksAllPropertiesEquals(testUserTasksSearch, updatedUserTasks);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userTasksDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userTasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userTasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userTasksDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUserTasksWithPatch() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userTasks using partial update
        UserTasks partialUpdatedUserTasks = new UserTasks();
        partialUpdatedUserTasks.setId(userTasks.getId());

        partialUpdatedUserTasks.createdAt(UPDATED_CREATED_AT);

        restUserTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserTasks.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserTasks))
            )
            .andExpect(status().isOk());

        // Validate the UserTasks in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserTasksUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserTasks, userTasks),
            getPersistedUserTasks(userTasks)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserTasksWithPatch() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userTasks using partial update
        UserTasks partialUpdatedUserTasks = new UserTasks();
        partialUpdatedUserTasks.setId(userTasks.getId());

        partialUpdatedUserTasks.privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);

        restUserTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserTasks.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserTasks))
            )
            .andExpect(status().isOk());

        // Validate the UserTasks in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserTasksUpdatableFieldsEquals(partialUpdatedUserTasks, getPersistedUserTasks(partialUpdatedUserTasks));
    }

    @Test
    @Transactional
    void patchNonExistingUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userTasksDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userTasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userTasksDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserTasks() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        userTasks.setId(longCount.incrementAndGet());

        // Create the UserTasks
        UserTasksDTO userTasksDTO = userTasksMapper.toDto(userTasks);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTasksMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userTasksDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserTasks in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserTasks() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);
        userTasksRepository.save(userTasks);
        userTasksSearchRepository.save(userTasks);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userTasks
        restUserTasksMockMvc
            .perform(delete(ENTITY_API_URL_ID, userTasks.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userTasksSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserTasks() throws Exception {
        // Initialize the database
        insertedUserTasks = userTasksRepository.saveAndFlush(userTasks);
        userTasksSearchRepository.save(userTasks);

        // Search the userTasks
        restUserTasksMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userTasks.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTasks.getId().intValue())))
            .andExpect(jsonPath("$.[*].privilege").value(hasItem(DEFAULT_PRIVILEGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return userTasksRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserTasks getPersistedUserTasks(UserTasks userTasks) {
        return userTasksRepository.findById(userTasks.getId()).orElseThrow();
    }

    protected void assertPersistedUserTasksToMatchAllProperties(UserTasks expectedUserTasks) {
        assertUserTasksAllPropertiesEquals(expectedUserTasks, getPersistedUserTasks(expectedUserTasks));
    }

    protected void assertPersistedUserTasksToMatchUpdatableProperties(UserTasks expectedUserTasks) {
        assertUserTasksAllUpdatablePropertiesEquals(expectedUserTasks, getPersistedUserTasks(expectedUserTasks));
    }
}

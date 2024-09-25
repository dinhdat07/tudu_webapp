package me.tudu.web.rest;

import static me.tudu.domain.UserWorkspacesAsserts.*;
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
import me.tudu.domain.UserWorkspaces;
import me.tudu.repository.UserRepository;
import me.tudu.repository.UserWorkspacesRepository;
import me.tudu.repository.search.UserWorkspacesSearchRepository;
import me.tudu.service.dto.UserWorkspacesDTO;
import me.tudu.service.mapper.UserWorkspacesMapper;
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
 * Integration tests for the {@link UserWorkspacesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserWorkspacesResourceIT {

    private static final String DEFAULT_PRIVILEGE = "AAAAAAAAAA";
    private static final String UPDATED_PRIVILEGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-workspaces";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-workspaces/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserWorkspacesRepository userWorkspacesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserWorkspacesMapper userWorkspacesMapper;

    @Autowired
    private UserWorkspacesSearchRepository userWorkspacesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserWorkspacesMockMvc;

    private UserWorkspaces userWorkspaces;

    private UserWorkspaces insertedUserWorkspaces;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserWorkspaces createEntity() {
        return new UserWorkspaces().privilege(DEFAULT_PRIVILEGE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserWorkspaces createUpdatedEntity() {
        return new UserWorkspaces().privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        userWorkspaces = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserWorkspaces != null) {
            userWorkspacesRepository.delete(insertedUserWorkspaces);
            userWorkspacesSearchRepository.delete(insertedUserWorkspaces);
            insertedUserWorkspaces = null;
        }
    }

    @Test
    @Transactional
    void createUserWorkspaces() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);
        var returnedUserWorkspacesDTO = om.readValue(
            restUserWorkspacesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userWorkspacesDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserWorkspacesDTO.class
        );

        // Validate the UserWorkspaces in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserWorkspaces = userWorkspacesMapper.toEntity(returnedUserWorkspacesDTO);
        assertUserWorkspacesUpdatableFieldsEquals(returnedUserWorkspaces, getPersistedUserWorkspaces(returnedUserWorkspaces));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserWorkspaces = returnedUserWorkspaces;
    }

    @Test
    @Transactional
    void createUserWorkspacesWithExistingId() throws Exception {
        // Create the UserWorkspaces with an existing ID
        userWorkspaces.setId(1L);
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserWorkspacesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userWorkspacesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUserWorkspaces() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);

        // Get all the userWorkspacesList
        restUserWorkspacesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userWorkspaces.getId().intValue())))
            .andExpect(jsonPath("$.[*].privilege").value(hasItem(DEFAULT_PRIVILEGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserWorkspaces() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);

        // Get the userWorkspaces
        restUserWorkspacesMockMvc
            .perform(get(ENTITY_API_URL_ID, userWorkspaces.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userWorkspaces.getId().intValue()))
            .andExpect(jsonPath("$.privilege").value(DEFAULT_PRIVILEGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserWorkspaces() throws Exception {
        // Get the userWorkspaces
        restUserWorkspacesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserWorkspaces() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userWorkspacesSearchRepository.save(userWorkspaces);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());

        // Update the userWorkspaces
        UserWorkspaces updatedUserWorkspaces = userWorkspacesRepository.findById(userWorkspaces.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserWorkspaces are not directly saved in db
        em.detach(updatedUserWorkspaces);
        updatedUserWorkspaces.privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(updatedUserWorkspaces);

        restUserWorkspacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userWorkspacesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userWorkspacesDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserWorkspacesToMatchAllProperties(updatedUserWorkspaces);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserWorkspaces> userWorkspacesSearchList = Streamable.of(userWorkspacesSearchRepository.findAll()).toList();
                UserWorkspaces testUserWorkspacesSearch = userWorkspacesSearchList.get(searchDatabaseSizeAfter - 1);

                assertUserWorkspacesAllPropertiesEquals(testUserWorkspacesSearch, updatedUserWorkspaces);
            });
    }

    @Test
    @Transactional
    void putNonExistingUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userWorkspacesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userWorkspacesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userWorkspacesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userWorkspacesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUserWorkspacesWithPatch() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userWorkspaces using partial update
        UserWorkspaces partialUpdatedUserWorkspaces = new UserWorkspaces();
        partialUpdatedUserWorkspaces.setId(userWorkspaces.getId());

        restUserWorkspacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserWorkspaces.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserWorkspaces))
            )
            .andExpect(status().isOk());

        // Validate the UserWorkspaces in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserWorkspacesUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserWorkspaces, userWorkspaces),
            getPersistedUserWorkspaces(userWorkspaces)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserWorkspacesWithPatch() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userWorkspaces using partial update
        UserWorkspaces partialUpdatedUserWorkspaces = new UserWorkspaces();
        partialUpdatedUserWorkspaces.setId(userWorkspaces.getId());

        partialUpdatedUserWorkspaces.privilege(UPDATED_PRIVILEGE).createdAt(UPDATED_CREATED_AT);

        restUserWorkspacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserWorkspaces.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserWorkspaces))
            )
            .andExpect(status().isOk());

        // Validate the UserWorkspaces in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserWorkspacesUpdatableFieldsEquals(partialUpdatedUserWorkspaces, getPersistedUserWorkspaces(partialUpdatedUserWorkspaces));
    }

    @Test
    @Transactional
    void patchNonExistingUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userWorkspacesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userWorkspacesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userWorkspacesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserWorkspaces() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        userWorkspaces.setId(longCount.incrementAndGet());

        // Create the UserWorkspaces
        UserWorkspacesDTO userWorkspacesDTO = userWorkspacesMapper.toDto(userWorkspaces);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserWorkspacesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userWorkspacesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserWorkspaces in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUserWorkspaces() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);
        userWorkspacesRepository.save(userWorkspaces);
        userWorkspacesSearchRepository.save(userWorkspaces);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userWorkspaces
        restUserWorkspacesMockMvc
            .perform(delete(ENTITY_API_URL_ID, userWorkspaces.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userWorkspacesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUserWorkspaces() throws Exception {
        // Initialize the database
        insertedUserWorkspaces = userWorkspacesRepository.saveAndFlush(userWorkspaces);
        userWorkspacesSearchRepository.save(userWorkspaces);

        // Search the userWorkspaces
        restUserWorkspacesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + userWorkspaces.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userWorkspaces.getId().intValue())))
            .andExpect(jsonPath("$.[*].privilege").value(hasItem(DEFAULT_PRIVILEGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return userWorkspacesRepository.count();
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

    protected UserWorkspaces getPersistedUserWorkspaces(UserWorkspaces userWorkspaces) {
        return userWorkspacesRepository.findById(userWorkspaces.getId()).orElseThrow();
    }

    protected void assertPersistedUserWorkspacesToMatchAllProperties(UserWorkspaces expectedUserWorkspaces) {
        assertUserWorkspacesAllPropertiesEquals(expectedUserWorkspaces, getPersistedUserWorkspaces(expectedUserWorkspaces));
    }

    protected void assertPersistedUserWorkspacesToMatchUpdatableProperties(UserWorkspaces expectedUserWorkspaces) {
        assertUserWorkspacesAllUpdatablePropertiesEquals(expectedUserWorkspaces, getPersistedUserWorkspaces(expectedUserWorkspaces));
    }
}

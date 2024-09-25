package me.tudu.service;

import java.util.Optional;
import me.tudu.domain.UserWorkspaces;
import me.tudu.repository.UserWorkspacesRepository;
import me.tudu.repository.search.UserWorkspacesSearchRepository;
import me.tudu.service.dto.UserWorkspacesDTO;
import me.tudu.service.mapper.UserWorkspacesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link me.tudu.domain.UserWorkspaces}.
 */
@Service
@Transactional
public class UserWorkspacesService {

    private static final Logger LOG = LoggerFactory.getLogger(UserWorkspacesService.class);

    private final UserWorkspacesRepository userWorkspacesRepository;

    private final UserWorkspacesMapper userWorkspacesMapper;

    private final UserWorkspacesSearchRepository userWorkspacesSearchRepository;

    public UserWorkspacesService(
        UserWorkspacesRepository userWorkspacesRepository,
        UserWorkspacesMapper userWorkspacesMapper,
        UserWorkspacesSearchRepository userWorkspacesSearchRepository
    ) {
        this.userWorkspacesRepository = userWorkspacesRepository;
        this.userWorkspacesMapper = userWorkspacesMapper;
        this.userWorkspacesSearchRepository = userWorkspacesSearchRepository;
    }

    /**
     * Save a userWorkspaces.
     *
     * @param userWorkspacesDTO the entity to save.
     * @return the persisted entity.
     */
    public UserWorkspacesDTO save(UserWorkspacesDTO userWorkspacesDTO) {
        LOG.debug("Request to save UserWorkspaces : {}", userWorkspacesDTO);
        UserWorkspaces userWorkspaces = userWorkspacesMapper.toEntity(userWorkspacesDTO);
        userWorkspaces = userWorkspacesRepository.save(userWorkspaces);
        userWorkspacesSearchRepository.index(userWorkspaces);
        return userWorkspacesMapper.toDto(userWorkspaces);
    }

    /**
     * Update a userWorkspaces.
     *
     * @param userWorkspacesDTO the entity to save.
     * @return the persisted entity.
     */
    public UserWorkspacesDTO update(UserWorkspacesDTO userWorkspacesDTO) {
        LOG.debug("Request to update UserWorkspaces : {}", userWorkspacesDTO);
        UserWorkspaces userWorkspaces = userWorkspacesMapper.toEntity(userWorkspacesDTO);
        userWorkspaces = userWorkspacesRepository.save(userWorkspaces);
        userWorkspacesSearchRepository.index(userWorkspaces);
        return userWorkspacesMapper.toDto(userWorkspaces);
    }

    /**
     * Partially update a userWorkspaces.
     *
     * @param userWorkspacesDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserWorkspacesDTO> partialUpdate(UserWorkspacesDTO userWorkspacesDTO) {
        LOG.debug("Request to partially update UserWorkspaces : {}", userWorkspacesDTO);

        return userWorkspacesRepository
            .findById(userWorkspacesDTO.getId())
            .map(existingUserWorkspaces -> {
                userWorkspacesMapper.partialUpdate(existingUserWorkspaces, userWorkspacesDTO);

                return existingUserWorkspaces;
            })
            .map(userWorkspacesRepository::save)
            .map(savedUserWorkspaces -> {
                userWorkspacesSearchRepository.index(savedUserWorkspaces);
                return savedUserWorkspaces;
            })
            .map(userWorkspacesMapper::toDto);
    }

    /**
     * Get all the userWorkspaces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserWorkspacesDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserWorkspaces");
        return userWorkspacesRepository.findAll(pageable).map(userWorkspacesMapper::toDto);
    }

    /**
     * Get one userWorkspaces by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserWorkspacesDTO> findOne(Long id) {
        LOG.debug("Request to get UserWorkspaces : {}", id);
        return userWorkspacesRepository.findById(id).map(userWorkspacesMapper::toDto);
    }

    /**
     * Delete the userWorkspaces by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserWorkspaces : {}", id);
        userWorkspacesRepository.deleteById(id);
        userWorkspacesSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the userWorkspaces corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserWorkspacesDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of UserWorkspaces for query {}", query);
        return userWorkspacesSearchRepository.search(query, pageable).map(userWorkspacesMapper::toDto);
    }
}

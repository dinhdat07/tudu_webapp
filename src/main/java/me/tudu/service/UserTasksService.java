package me.tudu.service;

import java.util.Optional;
import me.tudu.domain.UserTasks;
import me.tudu.repository.UserTasksRepository;
import me.tudu.repository.search.UserTasksSearchRepository;
import me.tudu.service.dto.UserTasksDTO;
import me.tudu.service.mapper.UserTasksMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link me.tudu.domain.UserTasks}.
 */
@Service
@Transactional
public class UserTasksService {

    private static final Logger LOG = LoggerFactory.getLogger(UserTasksService.class);

    private final UserTasksRepository userTasksRepository;

    private final UserTasksMapper userTasksMapper;

    private final UserTasksSearchRepository userTasksSearchRepository;

    public UserTasksService(
        UserTasksRepository userTasksRepository,
        UserTasksMapper userTasksMapper,
        UserTasksSearchRepository userTasksSearchRepository
    ) {
        this.userTasksRepository = userTasksRepository;
        this.userTasksMapper = userTasksMapper;
        this.userTasksSearchRepository = userTasksSearchRepository;
    }

    /**
     * Save a userTasks.
     *
     * @param userTasksDTO the entity to save.
     * @return the persisted entity.
     */
    public UserTasksDTO save(UserTasksDTO userTasksDTO) {
        LOG.debug("Request to save UserTasks : {}", userTasksDTO);
        UserTasks userTasks = userTasksMapper.toEntity(userTasksDTO);
        userTasks = userTasksRepository.save(userTasks);
        userTasksSearchRepository.index(userTasks);
        return userTasksMapper.toDto(userTasks);
    }

    /**
     * Update a userTasks.
     *
     * @param userTasksDTO the entity to save.
     * @return the persisted entity.
     */
    public UserTasksDTO update(UserTasksDTO userTasksDTO) {
        LOG.debug("Request to update UserTasks : {}", userTasksDTO);
        UserTasks userTasks = userTasksMapper.toEntity(userTasksDTO);
        userTasks = userTasksRepository.save(userTasks);
        userTasksSearchRepository.index(userTasks);
        return userTasksMapper.toDto(userTasks);
    }

    /**
     * Partially update a userTasks.
     *
     * @param userTasksDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserTasksDTO> partialUpdate(UserTasksDTO userTasksDTO) {
        LOG.debug("Request to partially update UserTasks : {}", userTasksDTO);

        return userTasksRepository
            .findById(userTasksDTO.getId())
            .map(existingUserTasks -> {
                userTasksMapper.partialUpdate(existingUserTasks, userTasksDTO);

                return existingUserTasks;
            })
            .map(userTasksRepository::save)
            .map(savedUserTasks -> {
                userTasksSearchRepository.index(savedUserTasks);
                return savedUserTasks;
            })
            .map(userTasksMapper::toDto);
    }

    /**
     * Get all the userTasks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserTasksDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserTasks");
        return userTasksRepository.findAll(pageable).map(userTasksMapper::toDto);
    }

    /**
     * Get one userTasks by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserTasksDTO> findOne(Long id) {
        LOG.debug("Request to get UserTasks : {}", id);
        return userTasksRepository.findById(id).map(userTasksMapper::toDto);
    }

    /**
     * Delete the userTasks by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserTasks : {}", id);
        userTasksRepository.deleteById(id);
        userTasksSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the userTasks corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserTasksDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of UserTasks for query {}", query);
        return userTasksSearchRepository.search(query, pageable).map(userTasksMapper::toDto);
    }
}

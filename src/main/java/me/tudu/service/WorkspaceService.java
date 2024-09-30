package me.tudu.service;

import java.util.Optional;
import me.tudu.domain.Workspace;
import me.tudu.repository.WorkspaceRepository;
import me.tudu.repository.search.WorkspaceSearchRepository;
import me.tudu.service.dto.WorkspaceDTO;
import me.tudu.service.mapper.WorkspaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link me.tudu.domain.Workspace}.
 */
@Service
@Transactional
public class WorkspaceService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceService.class);

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceMapper workspaceMapper;

    private final WorkspaceSearchRepository workspaceSearchRepository;

    public WorkspaceService(
        WorkspaceRepository workspaceRepository,
        WorkspaceMapper workspaceMapper,
        WorkspaceSearchRepository workspaceSearchRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMapper = workspaceMapper;
        this.workspaceSearchRepository = workspaceSearchRepository;
    }

    /**
     * Save a workspace.
     *
     * @param workspaceDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkspaceDTO save(WorkspaceDTO workspaceDTO) {
        LOG.debug("Request to save Workspace : {}", workspaceDTO);
        Workspace workspace = workspaceMapper.toEntity(workspaceDTO);
        workspace = workspaceRepository.save(workspace);
        workspaceSearchRepository.index(workspace);
        return workspaceMapper.toDto(workspace);
    }

    /**
     * Update a workspace.
     *
     * @param workspaceDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkspaceDTO update(WorkspaceDTO workspaceDTO) {
        LOG.debug("Request to update Workspace : {}", workspaceDTO);
        Workspace workspace = workspaceMapper.toEntity(workspaceDTO);
        workspace = workspaceRepository.save(workspace);
        workspaceSearchRepository.index(workspace);
        return workspaceMapper.toDto(workspace);
    }

    /**
     * Partially update a workspace.
     *
     * @param workspaceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkspaceDTO> partialUpdate(WorkspaceDTO workspaceDTO) {
        LOG.debug("Request to partially update Workspace : {}", workspaceDTO);

        return workspaceRepository
            .findById(workspaceDTO.getId())
            .map(existingWorkspace -> {
                workspaceMapper.partialUpdate(existingWorkspace, workspaceDTO);

                return existingWorkspace;
            })
            .map(workspaceRepository::save)
            .map(savedWorkspace -> {
                workspaceSearchRepository.index(savedWorkspace);
                return savedWorkspace;
            })
            .map(workspaceMapper::toDto);
    }

    /**
     * Get all the workspaces.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkspaceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Workspaces");
        return workspaceRepository.findAll(pageable).map(workspaceMapper::toDto);
    }

    /**
     * Get all the workspaces with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WorkspaceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return workspaceRepository.findAllWithEagerRelationships(pageable).map(workspaceMapper::toDto);
    }

    /**
     * Get one workspace by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkspaceDTO> findOne(Long id) {
        LOG.debug("Request to get Workspace : {}", id);
        return workspaceRepository.findOneWithEagerRelationships(id).map(workspaceMapper::toDto);
    }

    /**
     * Delete the workspace by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Workspace : {}", id);
        workspaceRepository.deleteById(id);
        workspaceSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the workspace corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkspaceDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Workspaces for query {}", query);
        return workspaceSearchRepository.search(query, pageable).map(workspaceMapper::toDto);
    }
}

package me.tudu.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.tudu.repository.WorkspaceRepository;
import me.tudu.service.WorkspaceService;
import me.tudu.service.dto.WorkspaceDTO;
import me.tudu.web.rest.errors.BadRequestAlertException;
import me.tudu.web.rest.errors.ElasticsearchExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link me.tudu.domain.Workspace}.
 */
@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkspaceResource.class);

    private static final String ENTITY_NAME = "workspace";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkspaceService workspaceService;

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceResource(WorkspaceService workspaceService, WorkspaceRepository workspaceRepository) {
        this.workspaceService = workspaceService;
        this.workspaceRepository = workspaceRepository;
    }

    /**
     * {@code POST  /workspaces} : Create a new workspace.
     *
     * @param workspaceDTO the workspaceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workspaceDTO, or with status {@code 400 (Bad Request)} if the workspace has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WorkspaceDTO> createWorkspace(@Valid @RequestBody WorkspaceDTO workspaceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Workspace : {}", workspaceDTO);
        if (workspaceDTO.getId() != null) {
            throw new BadRequestAlertException("A new workspace cannot already have an ID", ENTITY_NAME, "idexists");
        }
        workspaceDTO = workspaceService.save(workspaceDTO);
        return ResponseEntity.created(new URI("/api/workspaces/" + workspaceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, workspaceDTO.getId().toString()))
            .body(workspaceDTO);
    }

    /**
     * {@code PUT  /workspaces/:id} : Updates an existing workspace.
     *
     * @param id the id of the workspaceDTO to save.
     * @param workspaceDTO the workspaceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workspaceDTO,
     * or with status {@code 400 (Bad Request)} if the workspaceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workspaceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkspaceDTO workspaceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Workspace : {}, {}", id, workspaceDTO);
        if (workspaceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workspaceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workspaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workspaceDTO = workspaceService.update(workspaceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workspaceDTO.getId().toString()))
            .body(workspaceDTO);
    }

    /**
     * {@code PATCH  /workspaces/:id} : Partial updates given fields of an existing workspace, field will ignore if it is null
     *
     * @param id the id of the workspaceDTO to save.
     * @param workspaceDTO the workspaceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workspaceDTO,
     * or with status {@code 400 (Bad Request)} if the workspaceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workspaceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workspaceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkspaceDTO> partialUpdateWorkspace(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkspaceDTO workspaceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Workspace partially : {}, {}", id, workspaceDTO);
        if (workspaceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workspaceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workspaceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkspaceDTO> result = workspaceService.partialUpdate(workspaceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, workspaceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /workspaces} : get all the workspaces.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workspaces in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WorkspaceDTO>> getAllWorkspaces(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Workspaces");
        Page<WorkspaceDTO> page;
        if (eagerload) {
            page = workspaceService.findAllWithEagerRelationships(pageable);
        } else {
            page = workspaceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /workspaces/:id} : get the "id" workspace.
     *
     * @param id the id of the workspaceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workspaceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Workspace : {}", id);
        Optional<WorkspaceDTO> workspaceDTO = workspaceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workspaceDTO);
    }

    /**
     * {@code DELETE  /workspaces/:id} : delete the "id" workspace.
     *
     * @param id the id of the workspaceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Workspace : {}", id);
        workspaceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /workspaces/_search?query=:query} : search for the workspace corresponding
     * to the query.
     *
     * @param query the query of the workspace search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<WorkspaceDTO>> searchWorkspaces(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Workspaces for query {}", query);
        try {
            Page<WorkspaceDTO> page = workspaceService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

package me.tudu.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.tudu.repository.UserWorkspacesRepository;
import me.tudu.service.UserWorkspacesService;
import me.tudu.service.dto.UserWorkspacesDTO;
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
 * REST controller for managing {@link me.tudu.domain.UserWorkspaces}.
 */
@RestController
@RequestMapping("/api/user-workspaces")
public class UserWorkspacesResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserWorkspacesResource.class);

    private static final String ENTITY_NAME = "userWorkspaces";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserWorkspacesService userWorkspacesService;

    private final UserWorkspacesRepository userWorkspacesRepository;

    public UserWorkspacesResource(UserWorkspacesService userWorkspacesService, UserWorkspacesRepository userWorkspacesRepository) {
        this.userWorkspacesService = userWorkspacesService;
        this.userWorkspacesRepository = userWorkspacesRepository;
    }

    /**
     * {@code POST  /user-workspaces} : Create a new userWorkspaces.
     *
     * @param userWorkspacesDTO the userWorkspacesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userWorkspacesDTO, or with status {@code 400 (Bad Request)} if the userWorkspaces has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserWorkspacesDTO> createUserWorkspaces(@RequestBody UserWorkspacesDTO userWorkspacesDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserWorkspaces : {}", userWorkspacesDTO);
        if (userWorkspacesDTO.getId() != null) {
            throw new BadRequestAlertException("A new userWorkspaces cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userWorkspacesDTO = userWorkspacesService.save(userWorkspacesDTO);
        return ResponseEntity.created(new URI("/api/user-workspaces/" + userWorkspacesDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userWorkspacesDTO.getId().toString()))
            .body(userWorkspacesDTO);
    }

    /**
     * {@code PUT  /user-workspaces/:id} : Updates an existing userWorkspaces.
     *
     * @param id the id of the userWorkspacesDTO to save.
     * @param userWorkspacesDTO the userWorkspacesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userWorkspacesDTO,
     * or with status {@code 400 (Bad Request)} if the userWorkspacesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userWorkspacesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserWorkspacesDTO> updateUserWorkspaces(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserWorkspacesDTO userWorkspacesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserWorkspaces : {}, {}", id, userWorkspacesDTO);
        if (userWorkspacesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userWorkspacesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userWorkspacesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userWorkspacesDTO = userWorkspacesService.update(userWorkspacesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userWorkspacesDTO.getId().toString()))
            .body(userWorkspacesDTO);
    }

    /**
     * {@code PATCH  /user-workspaces/:id} : Partial updates given fields of an existing userWorkspaces, field will ignore if it is null
     *
     * @param id the id of the userWorkspacesDTO to save.
     * @param userWorkspacesDTO the userWorkspacesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userWorkspacesDTO,
     * or with status {@code 400 (Bad Request)} if the userWorkspacesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userWorkspacesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userWorkspacesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserWorkspacesDTO> partialUpdateUserWorkspaces(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserWorkspacesDTO userWorkspacesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserWorkspaces partially : {}, {}", id, userWorkspacesDTO);
        if (userWorkspacesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userWorkspacesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userWorkspacesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserWorkspacesDTO> result = userWorkspacesService.partialUpdate(userWorkspacesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userWorkspacesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-workspaces} : get all the userWorkspaces.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userWorkspaces in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserWorkspacesDTO>> getAllUserWorkspaces(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserWorkspaces");
        Page<UserWorkspacesDTO> page = userWorkspacesService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-workspaces/:id} : get the "id" userWorkspaces.
     *
     * @param id the id of the userWorkspacesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userWorkspacesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserWorkspacesDTO> getUserWorkspaces(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserWorkspaces : {}", id);
        Optional<UserWorkspacesDTO> userWorkspacesDTO = userWorkspacesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userWorkspacesDTO);
    }

    /**
     * {@code DELETE  /user-workspaces/:id} : delete the "id" userWorkspaces.
     *
     * @param id the id of the userWorkspacesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserWorkspaces(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserWorkspaces : {}", id);
        userWorkspacesService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /user-workspaces/_search?query=:query} : search for the userWorkspaces corresponding
     * to the query.
     *
     * @param query the query of the userWorkspaces search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UserWorkspacesDTO>> searchUserWorkspaces(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of UserWorkspaces for query {}", query);
        try {
            Page<UserWorkspacesDTO> page = userWorkspacesService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

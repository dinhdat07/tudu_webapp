package me.tudu.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.tudu.repository.UserTasksRepository;
import me.tudu.service.UserTasksService;
import me.tudu.service.dto.UserTasksDTO;
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
 * REST controller for managing {@link me.tudu.domain.UserTasks}.
 */
@RestController
@RequestMapping("/api/user-tasks")
public class UserTasksResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserTasksResource.class);

    private static final String ENTITY_NAME = "userTasks";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserTasksService userTasksService;

    private final UserTasksRepository userTasksRepository;

    public UserTasksResource(UserTasksService userTasksService, UserTasksRepository userTasksRepository) {
        this.userTasksService = userTasksService;
        this.userTasksRepository = userTasksRepository;
    }

    /**
     * {@code POST  /user-tasks} : Create a new userTasks.
     *
     * @param userTasksDTO the userTasksDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userTasksDTO, or with status {@code 400 (Bad Request)} if the userTasks has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserTasksDTO> createUserTasks(@RequestBody UserTasksDTO userTasksDTO) throws URISyntaxException {
        LOG.debug("REST request to save UserTasks : {}", userTasksDTO);
        if (userTasksDTO.getId() != null) {
            throw new BadRequestAlertException("A new userTasks cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userTasksDTO = userTasksService.save(userTasksDTO);
        return ResponseEntity.created(new URI("/api/user-tasks/" + userTasksDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userTasksDTO.getId().toString()))
            .body(userTasksDTO);
    }

    /**
     * {@code PUT  /user-tasks/:id} : Updates an existing userTasks.
     *
     * @param id the id of the userTasksDTO to save.
     * @param userTasksDTO the userTasksDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userTasksDTO,
     * or with status {@code 400 (Bad Request)} if the userTasksDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userTasksDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserTasksDTO> updateUserTasks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserTasksDTO userTasksDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserTasks : {}, {}", id, userTasksDTO);
        if (userTasksDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userTasksDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userTasksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userTasksDTO = userTasksService.update(userTasksDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userTasksDTO.getId().toString()))
            .body(userTasksDTO);
    }

    /**
     * {@code PATCH  /user-tasks/:id} : Partial updates given fields of an existing userTasks, field will ignore if it is null
     *
     * @param id the id of the userTasksDTO to save.
     * @param userTasksDTO the userTasksDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userTasksDTO,
     * or with status {@code 400 (Bad Request)} if the userTasksDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userTasksDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userTasksDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserTasksDTO> partialUpdateUserTasks(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody UserTasksDTO userTasksDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserTasks partially : {}, {}", id, userTasksDTO);
        if (userTasksDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userTasksDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userTasksRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserTasksDTO> result = userTasksService.partialUpdate(userTasksDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userTasksDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /user-tasks} : get all the userTasks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userTasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserTasksDTO>> getAllUserTasks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserTasks");
        Page<UserTasksDTO> page = userTasksService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-tasks/:id} : get the "id" userTasks.
     *
     * @param id the id of the userTasksDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userTasksDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserTasksDTO> getUserTasks(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserTasks : {}", id);
        Optional<UserTasksDTO> userTasksDTO = userTasksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userTasksDTO);
    }

    /**
     * {@code DELETE  /user-tasks/:id} : delete the "id" userTasks.
     *
     * @param id the id of the userTasksDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserTasks(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserTasks : {}", id);
        userTasksService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /user-tasks/_search?query=:query} : search for the userTasks corresponding
     * to the query.
     *
     * @param query the query of the userTasks search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UserTasksDTO>> searchUserTasks(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of UserTasks for query {}", query);
        try {
            Page<UserTasksDTO> page = userTasksService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

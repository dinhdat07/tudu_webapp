package me.tudu.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import me.tudu.domain.Workspace;
import me.tudu.repository.WorkspaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Workspace} entity.
 */
public interface WorkspaceSearchRepository extends ElasticsearchRepository<Workspace, Long>, WorkspaceSearchRepositoryInternal {}

interface WorkspaceSearchRepositoryInternal {
    Page<Workspace> search(String query, Pageable pageable);

    Page<Workspace> search(Query query);

    @Async
    void index(Workspace entity);

    @Async
    void deleteFromIndexById(Long id);
}

class WorkspaceSearchRepositoryInternalImpl implements WorkspaceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final WorkspaceRepository repository;

    WorkspaceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, WorkspaceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Workspace> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Workspace> search(Query query) {
        SearchHits<Workspace> searchHits = elasticsearchTemplate.search(query, Workspace.class);
        List<Workspace> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Workspace entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Workspace.class);
    }
}

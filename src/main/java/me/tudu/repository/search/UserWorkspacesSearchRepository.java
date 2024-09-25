package me.tudu.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import me.tudu.domain.UserWorkspaces;
import me.tudu.repository.UserWorkspacesRepository;
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
 * Spring Data Elasticsearch repository for the {@link UserWorkspaces} entity.
 */
public interface UserWorkspacesSearchRepository
    extends ElasticsearchRepository<UserWorkspaces, Long>, UserWorkspacesSearchRepositoryInternal {}

interface UserWorkspacesSearchRepositoryInternal {
    Page<UserWorkspaces> search(String query, Pageable pageable);

    Page<UserWorkspaces> search(Query query);

    @Async
    void index(UserWorkspaces entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UserWorkspacesSearchRepositoryInternalImpl implements UserWorkspacesSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserWorkspacesRepository repository;

    UserWorkspacesSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserWorkspacesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserWorkspaces> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UserWorkspaces> search(Query query) {
        SearchHits<UserWorkspaces> searchHits = elasticsearchTemplate.search(query, UserWorkspaces.class);
        List<UserWorkspaces> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserWorkspaces entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserWorkspaces.class);
    }
}

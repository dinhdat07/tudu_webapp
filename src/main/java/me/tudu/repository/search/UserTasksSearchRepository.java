package me.tudu.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import java.util.List;
import me.tudu.domain.UserTasks;
import me.tudu.repository.UserTasksRepository;
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
 * Spring Data Elasticsearch repository for the {@link UserTasks} entity.
 */
public interface UserTasksSearchRepository extends ElasticsearchRepository<UserTasks, Long>, UserTasksSearchRepositoryInternal {}

interface UserTasksSearchRepositoryInternal {
    Page<UserTasks> search(String query, Pageable pageable);

    Page<UserTasks> search(Query query);

    @Async
    void index(UserTasks entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UserTasksSearchRepositoryInternalImpl implements UserTasksSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserTasksRepository repository;

    UserTasksSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserTasksRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserTasks> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UserTasks> search(Query query) {
        SearchHits<UserTasks> searchHits = elasticsearchTemplate.search(query, UserTasks.class);
        List<UserTasks> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserTasks entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserTasks.class);
    }
}

package dk.cooldev.elasticsearch.service;

import com.sun.istack.internal.NotNull;
import dk.cooldev.elasticsearch.bean.Document;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchService {
    
    public Document get(String originId);
    
    public void addOrUpdate(Document document, int listId);

    public void removeFromList(Document document, int listId);

    public void delete(Document document);
    
    public long count(int list);

    public List<Document> getByQuerySpec(QuerySpec querySpec);

    class QuerySpec {
        public QuerySpec(@NotNull List<Integer> lists, @NotNull Document.Type documentType, @NotNull String field) {
            this.lists = lists;
            this.documentType = documentType;
            this.field = field;
        }

        public List<Integer> lists;
        public Document.Type documentType;
        public String field;
        public SortOrder sortOrder;
        public int page;
        public int pageSize;

    }
}

package dk.cooldev.elasticsearch.service.impl;

import dk.cooldev.elasticsearch.bean.Document;
import dk.cooldev.elasticsearch.service.ElasticSearchService;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.base.Function;
import org.elasticsearch.common.collect.Collections2;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private Client client;
    public static final String LSADMIN_LISTS = "lsadmin.lists";

    public Document get(String originId) {
        GetResponse response = client.get(new GetRequest("lists", Document.Type.INTERCHANGE.name(), originId)).actionGet();
        Document document = null;
        if (response.exists()) {
            document = new Document();
            document.setOriginId(originId);
            document.setType(Document.Type.INTERCHANGE);
            document.setProperties(response.sourceAsMap());
        }
        return document;
    }

    public void addOrUpdate(Document document, int listId) {
        Document existingDocument = get(document.getOriginId());

        if (existingDocument != null)
        {
            setLists(document, getLists(existingDocument));
        }
        addList(document, listId);

        indexDocument(document);
    }

    private List<Integer> setLists(Document document, List<Integer> lists) {
        document.getProperties().put(LSADMIN_LISTS, lists);
        return lists;
    }

    private void indexDocument(Document document) {
        IndexRequest indexRequest = new IndexRequest("lists", document.getType().name(), document.getOriginId());
        indexRequest.source(document.getProperties());

        client.index(indexRequest).actionGet();
        client.admin().indices().refresh(new RefreshRequest("lists").waitForOperations(true)).actionGet();
    }

    public void removeFromList(Document document, int listId) {
        Document existingDocument = get(document.getOriginId());
        if (existingDocument == null)
        {
            return;
        }

        List<Integer> existingLists = getLists(existingDocument);
        if (existingLists.contains(listId))
        {
            List<Integer> lists = removeList(existingDocument, listId);

            if (lists.size() == 0)
            {
                delete(existingDocument);
            }
            else
            {
                indexDocument(existingDocument);
            }
        }
    }


    public void delete(Document document) {
        client.delete(new DeleteRequest("lists", document.getType().name(), document.getOriginId())).actionGet();
    }


    public long count(int list) {

        QueryBuilder qb = termQuery(LSADMIN_LISTS, list);
        System.out.println("qb = " + qb);

        CountRequest countRequest = new CountRequest("lists");
        countRequest.query(qb);

        System.out.println("countRequest = " + countRequest);
        long count = client.count(countRequest).actionGet().count();
        return count;
    }


    public List<Document> getByQuerySpec(final QuerySpec querySpec) {
        QueryBuilder qb = termQuery(LSADMIN_LISTS, querySpec.lists);
        int offset = querySpec.page * querySpec.pageSize;
        int pagesize = querySpec.pageSize;
        SearchResponse response = client.prepareSearch("lists")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)
                .setFrom(offset).setSize(pagesize).setExplain(true)
                .addSort(querySpec.field, querySpec.sortOrder)
                .execute()
                .actionGet();

        Collection<Document> result;
        if (response.getHits().totalHits() == 0) {
            result = Collections.emptyList();
        } else {
            final Document.Type type = querySpec.documentType;
            result = Collections2.transform(Arrays.asList(response.getHits().getHits()), new Function<SearchHit, Document>() {
                public Document apply(SearchHit searchHitFields) {
                    Document document = new Document();
                    document.setOriginId(searchHitFields.id());
                    document.setType(type);
                    document.setProperties(searchHitFields.sourceAsMap());
                    return document;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }


        return (List<Document>) result;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private List<Integer> getLists(Document document)
    {
        return (List<Integer>) document.getProperties().get(LSADMIN_LISTS);
    }
    
    private List<Integer> addList(Document document, int list)
    {
        List<Integer> lists = getLists(document);
        if (!lists.contains(list))
        {
            lists.add(list);
        }
        return lists;
    }

    private List<Integer> removeList(Document document, int list)
    {
        List<Integer> lists = getLists(document);
        lists.remove(Integer.valueOf(list));
        return lists;
    }
}

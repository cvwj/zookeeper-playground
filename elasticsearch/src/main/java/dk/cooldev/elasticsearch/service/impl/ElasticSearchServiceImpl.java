package dk.cooldev.elasticsearch.service.impl;

import dk.cooldev.elasticsearch.bean.*;
import dk.cooldev.elasticsearch.service.ElasticSearchService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.base.Function;
import org.elasticsearch.common.collect.Collections2;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchServiceImpl implements ElasticSearchService {
    Logger log = Logger.getLogger(this.getClass());

    private Client client;
    public static final String LSADMIN_LIST = "lsadmin.list";

    public Document get(int list, String originId) {
        String docId = getId(list, originId);
        GetRequest getRequest = new GetRequest("lists", Document.Type.INTERCHANGE.name(), docId);
        GetResponse response = client.get(getRequest).actionGet();
        Document document = null;
        if (response.exists()) {
            document = new Document();
            document.setOriginId((String) response.sourceAsMap().get("originId"));
            document.setType(Document.Type.INTERCHANGE);
            document.setProperties(response.sourceAsMap());
        }
        return document;
    }

    private String getId(int list, String originId) {
        return new StringBuilder("list:").append(list).append(":origin:").append(originId).toString();
    }

    public void addOrUpdate(int list, Document document) {
        indexDocument(list, document);
    }

    private void indexDocument(int list, Document document) {
        document.getProperties().put(LSADMIN_LIST, list);
        IndexRequest indexRequest = new IndexRequest("lists", document.getType().name(), getId(list, document.getOriginId()));
        indexRequest.source(document.getProperties());

        client.index(indexRequest).actionGet();
        client.admin().indices().refresh(new RefreshRequest("lists").waitForOperations(true)).actionGet();
    }


    public void delete(int list, Document document) {
        String id = getId(list, document.getOriginId());
        client.delete(new DeleteRequest("lists", document.getType().name(), id)).actionGet();
    }


    public long count(int list) {

        QueryBuilder qb = termQuery(LSADMIN_LIST, list);
        System.out.println("qb = " + qb);

        CountRequest countRequest = new CountRequest("lists");
        countRequest.query(qb);

        System.out.println("countRequest = " + countRequest);
        long count = client.count(countRequest).actionGet().count();
        return count;
    }


    private List<Document> getDocumentsFromSearchHits(SearchResponse response) {
        Collection<Document> result;
        if (response.getHits().totalHits() == 0) {
            result = Collections.emptyList();
        } else {
            final Document.Type type = Document.Type.INTERCHANGE;
            result = Collections2.transform(Arrays.asList(response.getHits().getHits()), new Function<SearchHit, Document>() {
                public Document apply(SearchHit searchHitFields) {
                    Document document = new Document();
                    document.setOriginId((String) searchHitFields.sourceAsMap().get("originId"));
                    document.setType(type);
                    document.setProperties(searchHitFields.sourceAsMap());
                    return document;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }


        return new ArrayList(result);
    }



    public DocumentList findByFilter(int list, LSFilterList filterList, FetchSpec fetchSpec) {
        SearchRequestBuilder requestBuilder = client.prepareSearch("lists");
        requestBuilder
                .setSearchType(SearchType.QUERY_AND_FETCH) // Optimal searchtype, as all documents on a list are routed to the same shard
                .setFilter(convertToLuceneFilter(list, filterList))
                .setFrom(fetchSpec.getOffset())
                .setSize(fetchSpec.getPageSize())
                .addSort(fetchSpec.getSortBy(), fetchSpec.getOrder() == FetchSpec.Order.ASC ? SortOrder.ASC : SortOrder.DESC)
                .addSort("publishedDate", SortOrder.DESC);

        log.debug("findByFilter: " + requestBuilder.toString());


        SearchResponse response = requestBuilder.execute().actionGet();

        List<Document> documents = getDocumentsFromSearchHits(response);
        DocumentList result = new DocumentList();
        result.setDocuments(documents);
        result.setTotalHits(response.hits().getTotalHits());
        result.setListIdentifier(list);
//        result.setOffset();
        result.setResultSize(documents.size());
        result.setQueryTime(response.getTookInMillis());

        return result;
    }


    private FilterBuilder convertToLuceneFilter(int list, LSFilterList filterList) {

        BoolFilterBuilder fb = new BoolFilterBuilder();
        fb.must(new TermFilterBuilder(LSADMIN_LIST, list));

        for (LSFilter filter: filterList.getFilters()) {

            if (filter instanceof LSInFilter)
            {
                LSInFilter inFilter = (LSInFilter) filter;
                TermsFilterBuilder termsFilterBuilder = new TermsFilterBuilder(inFilter.getField(), inFilter.getValues());
                // Default is "any" (or "plain" in the documentation)
                if (inFilter.getExecution() == LSInFilter.Execution.ALL)
                {
                    termsFilterBuilder.execution("and");
                }
                fb.must(termsFilterBuilder);
            }
            else if (filter instanceof LSRangeFilter)
            {
                LSRangeFilter rangeFilter = (LSRangeFilter) filter;
                RangeFilterBuilder rangeFilterBuilder = new RangeFilterBuilder(rangeFilter.getField());
                rangeFilterBuilder.from(rangeFilter.getFrom());
                rangeFilterBuilder.to(rangeFilter.getTo());
                rangeFilterBuilder.includeLower(rangeFilter.isIncludeBounds());
                rangeFilterBuilder.includeUpper(rangeFilter.isIncludeBounds());
                fb.must(rangeFilterBuilder);
            }
            else if (filter instanceof LSSimpleFilter)
            {
                LSSimpleFilter simpleFilter = (LSSimpleFilter) filter;
                switch (simpleFilter.getOperator())
                {
                    case LT: fb.must(new RangeFilterBuilder(simpleFilter.getField()).lt(simpleFilter.getValue())); break;
                    case LE: fb.must(new RangeFilterBuilder(simpleFilter.getField()).lte(simpleFilter.getValue())); break;
                    case GT: fb.must(new RangeFilterBuilder(simpleFilter.getField()).gt(simpleFilter.getValue())); break;
                    case GE: fb.must(new RangeFilterBuilder(simpleFilter.getField()).gte(simpleFilter.getValue())); break;
                    case EQ: fb.must(new TermFilterBuilder(simpleFilter.getField(), simpleFilter.getValue())); break;
                    case NE: fb.mustNot(new TermFilterBuilder(simpleFilter.getField(), simpleFilter.getValue())); break;
                }
            }
        }
        return fb;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}

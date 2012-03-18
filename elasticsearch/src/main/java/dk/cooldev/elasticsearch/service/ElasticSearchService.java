package dk.cooldev.elasticsearch.service;

import dk.cooldev.elasticsearch.bean.Document;
import dk.cooldev.elasticsearch.bean.DocumentList;
import dk.cooldev.elasticsearch.bean.FetchSpec;
import dk.cooldev.elasticsearch.bean.LSFilterList;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:02 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ElasticSearchService {
    
    public Document get(int list, String originId);
    
    public void addOrUpdate(int list, Document document);

    public void delete(int list, Document document);
    
    public long count(int list);

    public DocumentList findByFilter(int i, LSFilterList filterList, FetchSpec fetchSpec);

}

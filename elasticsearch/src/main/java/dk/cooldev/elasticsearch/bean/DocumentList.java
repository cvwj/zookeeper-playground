package dk.cooldev.elasticsearch.bean;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 18/01/2012
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentList {
    List<Document> documents;
    long totalHits;
    long offset;
    long resultSize;
    long listIdentifier;
    long queryTime;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getResultSize() {
        return resultSize;
    }

    public void setResultSize(long resultSize) {
        this.resultSize = resultSize;
    }

    public long getListIdentifier() {
        return listIdentifier;
    }

    public void setListIdentifier(long listIdentifier) {
        this.listIdentifier = listIdentifier;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }
}

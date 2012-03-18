package dk.cooldev.elasticsearch.bean;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 18/01/2012
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetchSpec {
    public enum Order {ASC, DESC}
    int offset;
    int pageSize;
    String sortBy;
    Order order;

    public FetchSpec(int offset, int pageSize, String sortBy, Order order) {
        this.offset = offset;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.order = order;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

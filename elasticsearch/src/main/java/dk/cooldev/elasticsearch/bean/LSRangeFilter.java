package dk.cooldev.elasticsearch.bean;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 18/01/2012
 * Time: 5:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class LSRangeFilter implements LSFilter{
    String field;
    Object from;
    Object to;
    boolean includeBounds;

    public LSRangeFilter(String field, Object from, Object to, boolean includeBounds) {
        this.field = field;
        this.from = from;
        this.to = to;
        this.includeBounds = includeBounds;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public Object getTo() {
        return to;
    }

    public void setTo(Object to) {
        this.to = to;
    }

    public boolean isIncludeBounds() {
        return includeBounds;
    }

    public void setIncludeBounds(boolean includeBounds) {
        this.includeBounds = includeBounds;
    }
}

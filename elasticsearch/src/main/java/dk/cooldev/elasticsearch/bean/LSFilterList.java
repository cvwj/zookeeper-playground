package dk.cooldev.elasticsearch.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 13/01/2012
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class LSFilterList {
    public enum OP {LT, LE, GT, GE, EQ, NE}

    List<LSFilter> filters = new ArrayList<LSFilter>();


    public List<LSFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<LSFilter> filters) {
        this.filters = filters;
    }

    public void addSimpleFilter(String field, Object value, OP operator) {
        LSFilter filter = new LSSimpleFilter(field, operator, value);
        filters.add(filter);
    }

    public void addRange(String field, Object fromValue, Object toValue, boolean includeUpperLowerBounds) {
        filters.add(new LSRangeFilter(field, fromValue, toValue, includeUpperLowerBounds));
    }

    public void addIn(String field, List<Object> values, LSInFilter.Execution execution) {
        filters.add(new LSInFilter(field, values, execution));
    }


}

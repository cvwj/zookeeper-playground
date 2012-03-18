package dk.cooldev.elasticsearch.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 18/01/2012
 * Time: 5:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class LSInFilter implements LSFilter {
    public enum Execution {ANY, ALL}
    String field;
    List<Object> values = new ArrayList<Object>();
    Execution execution;



    public LSInFilter(String field, List<Object> values, Execution execution) {
        this.field = field;
        this.values = values;
        this.execution = execution;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }
}

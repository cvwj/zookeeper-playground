package dk.cooldev.elasticsearch.bean;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 18/01/2012
 * Time: 5:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class LSSimpleFilter implements LSFilter{
    String field;
    LSFilterList.OP operator;
    Object value;

    public LSSimpleFilter(String field, LSFilterList.OP operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public LSFilterList.OP getOperator() {
        return operator;
    }

    public void setOperator(LSFilterList.OP operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

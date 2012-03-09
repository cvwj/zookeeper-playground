package dk.cooldev.elasticsearch.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class Document {
    public static enum Type { INTERCHANGE }
    
    private String originId;
    private Type type;

    private Map properties = new HashMap();

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }
}

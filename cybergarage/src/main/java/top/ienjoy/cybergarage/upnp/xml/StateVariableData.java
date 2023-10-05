package top.ienjoy.cybergarage.upnp.xml;

import top.ienjoy.cybergarage.upnp.control.*;

public class StateVariableData extends NodeData {
    public StateVariableData() {
    }

    ////////////////////////////////////////////////
    // value
    ////////////////////////////////////////////////

    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    ////////////////////////////////////////////////
    // QueryListener
    ////////////////////////////////////////////////

    private QueryListener queryListener = null;

    public QueryListener getQueryListener() {
        return queryListener;
    }

    public void setQueryListener(QueryListener queryListener) {
        this.queryListener = queryListener;
    }

    ////////////////////////////////////////////////
    // QueryResponse
    ////////////////////////////////////////////////

    private QueryResponse queryRes = null;

    public QueryResponse getQueryResponse() {
        return queryRes;
    }

    public void setQueryResponse(QueryResponse res) {
        queryRes = res;
    }

}


package top.ienjoy.cybergarage.upnp.control;

import top.ienjoy.cybergarage.upnp.*;
import top.ienjoy.cybergarage.http.*;
import top.ienjoy.cybergarage.soap.*;
import top.ienjoy.cybergarage.xml.*;

public class QueryResponse extends ControlResponse {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public QueryResponse() {
    }

    public QueryResponse(SOAPResponse soapRes) {
        super(soapRes);
    }

    ////////////////////////////////////////////////
    //	Qyery
    ////////////////////////////////////////////////

    private Node getReturnNode() {
        Node bodyNode = getBodyNode();
        if (bodyNode == null)
            return null;
        if (!bodyNode.hasNodes())
            return null;
        Node queryResNode = bodyNode.getNode(0);
        if (queryResNode == null)
            return null;
        if (!queryResNode.hasNodes())
            return null;
        return queryResNode.getNode(0);
    }

    public String getReturnValue() {
        Node node = getReturnNode();
        if (node == null)
            return "";
        return node.getValue();
    }

    ////////////////////////////////////////////////
    //	Response
    ////////////////////////////////////////////////

    public void setResponse(StateVariable stateVar) {
        String var = stateVar.getValue();

        setStatusCode(HTTPStatus.OK);

        Node bodyNode = getBodyNode();
        Node resNode = createResponseNode(var);
        bodyNode.addNode(resNode);

        Node envNodee = getEnvelopeNode();
        setContent(envNodee);

    }

    private Node createResponseNode(String var) {
        Node queryResNode = new Node();
        queryResNode.setName(Control.NS, Control.QUERY_STATE_VARIABLE_RESPONSE);
        queryResNode.setNameSpace(Control.NS, Control.XMLNS);

        Node returnNode = new Node();
        returnNode.setName(Control.RETURN);
        returnNode.setValue(var);
        queryResNode.addNode(returnNode);

        return queryResNode;
    }
}

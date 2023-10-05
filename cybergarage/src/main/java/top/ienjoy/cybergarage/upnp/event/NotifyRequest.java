package top.ienjoy.cybergarage.upnp.event;

import top.ienjoy.cybergarage.http.*;
import top.ienjoy.cybergarage.xml.*;
import top.ienjoy.cybergarage.soap.*;

import top.ienjoy.cybergarage.upnp.device.*;

public class NotifyRequest extends SOAPRequest {
    private final static String XMLNS = "e";
    private final static String PROPERTY = "property";
    private final static String PROPERTYSET = "propertyset";

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public NotifyRequest() {
    }

    public NotifyRequest(HTTPRequest httpReq) {
        set(httpReq);
    }

    ////////////////////////////////////////////////
    //	NT
    ////////////////////////////////////////////////

    public void setNT(String value) {
        setHeader(HTTP.NT, value);
    }

    ////////////////////////////////////////////////
    //	NTS
    ////////////////////////////////////////////////

    public void setNTS(String value) {
        setHeader(HTTP.NTS, value);
    }

    ////////////////////////////////////////////////
    //	SID
    ////////////////////////////////////////////////

    public void setSID(String id) {
        setHeader(HTTP.SID, Subscription.toSIDHeaderString(id));
    }

    public String getSID() {
        return Subscription.getSID(getHeaderValue(HTTP.SID));
    }

    ////////////////////////////////////////////////
    //	SEQ
    ////////////////////////////////////////////////

    public void setSEQ(long value) {
        setHeader(HTTP.SEQ, Long.toString(value));
    }

    public long getSEQ() {
        return getLongHeaderValue(HTTP.SEQ);
    }

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public boolean setRequest(Subscriber sub, String varName, String value) {
        String sid = sub.getSID();
        long notifyCnt = sub.getNotifyCount();
        String host = sub.getDeliveryHost();
        String path = sub.getDeliveryPath();
        int port = sub.getDeliveryPort();

        setMethod(HTTP.NOTIFY);
        setURI(path);
        setHost(host, port);
        setNT(NT.EVENT);
        setNTS(NTS.PROPCHANGE);
        setSID(sid);
        setSEQ(notifyCnt);

        setContentType(XML.DEFAULT_CONTENT_TYPE);
        Node propSetNode = createPropertySetNode(varName, value);
        setContent(propSetNode);

        return true;
    }

    private Node createPropertySetNode(String varName, String value) {
        Node propSetNode = new Node(/*XMLNS + SOAP.DELIM + */PROPERTYSET);

        propSetNode.setNameSpace(XMLNS, Subscription.XMLNS);

        Node propNode = new Node(/*XMLNS + SOAP.DELIM + */PROPERTY);
        propSetNode.addNode(propNode);

        // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (05/22/03)
        //Node varNameNode = new Node(XMLNS + SOAP.DELIM + varName);
        Node varNameNode = new Node(varName);
        varNameNode.setValue(value);
        propNode.addNode(varNameNode);

        return propSetNode;
    }

    private Node getVariableNode() {
        Node rootNode = getEnvelopeNode();
        if (rootNode == null)
            return null;
        if (!rootNode.hasNodes())
            return null;
        Node propNode = rootNode.getNode(0);
        if (!propNode.hasNodes())
            return null;
        return propNode.getNode(0);
    }

    // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (09/08/03)
    private Property getProperty(Node varNode) {
        Property prop = new Property();
        if (varNode == null)
            return prop;
        // remove the event namespace
        String variableName = varNode.getName();
        int index = variableName.lastIndexOf(':');
        if (index != -1)
            variableName = variableName.substring(index + 1);
        prop.setName(variableName);
        prop.setValue(varNode.getValue());
        return prop;
    }

    // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (09/08/03)
    public PropertyList getPropertyList() {
        PropertyList properties = new PropertyList();
        Node varSetNode = getEnvelopeNode();
        for (int i = 0; i < varSetNode.getNNodes(); i++) {
            Node propNode = varSetNode.getNode(i);
            if (propNode == null)
                continue;
            Property prop = getProperty(propNode.getNode(0));
            properties.add(prop);
        }
        return properties;
    }

}	
package top.ienjoy.cybergarage.upnp.control;

import top.ienjoy.cybergarage.upnp.*;
import top.ienjoy.cybergarage.http.*;
import top.ienjoy.cybergarage.soap.*;
import top.ienjoy.cybergarage.xml.*;

public class ActionResponse extends ControlResponse {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public ActionResponse() {
        setHeader(HTTP.EXT, "");
    }

    public ActionResponse(SOAPResponse soapRes) {
        super(soapRes);
        setHeader(HTTP.EXT, "");
    }


    ////////////////////////////////////////////////
    //	Response
    ////////////////////////////////////////////////

    public void setResponse(Action action) {
        setStatusCode(HTTPStatus.OK);

        Node bodyNode = getBodyNode();
        Node resNode = createResponseNode(action);
        bodyNode.addNode(resNode);

        Node envNode = getEnvelopeNode();
        setContent(envNode);
    }

    private Node createResponseNode(Action action) {
        String actionName = action.getName();
        Node actionNameResNode = new Node(SOAP.METHODNS + SOAP.DELIM + actionName + SOAP.RESPONSE);

        Service service = action.getService();
        if (service != null) {
            actionNameResNode.setAttribute(
                    "xmlns:" + SOAP.METHODNS,
                    service.getServiceType());
        }

        ArgumentList argList = action.getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            if (!arg.isOutDirection())
                continue;
            Node argNode = new Node();
            argNode.setName(arg.getName());
            argNode.setValue(arg.getValue());
            actionNameResNode.addNode(argNode);
        }

        return actionNameResNode;
    }

    ////////////////////////////////////////////////
    //	getResponse
    ////////////////////////////////////////////////

    private Node getActionResponseNode() {
        Node bodyNode = getBodyNode();
        if (bodyNode == null || !bodyNode.hasNodes())
            return null;
        return bodyNode.getNode(0);
    }


    public ArgumentList getResponse() {
        ArgumentList argList = new ArgumentList();

        Node resNode = getActionResponseNode();
        if (resNode == null)
            return argList;

        int nArgs = resNode.getNNodes();
        for (int n = 0; n < nArgs; n++) {
            Node node = resNode.getNode(n);
            String name = node.getName();
            String value = node.getValue();
            Argument arg = new Argument(name, value);
            argList.add(arg);
        }

        return argList;
    }
}

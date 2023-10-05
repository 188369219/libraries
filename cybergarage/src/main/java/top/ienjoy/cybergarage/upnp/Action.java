package top.ienjoy.cybergarage.upnp;

import top.ienjoy.cybergarage.upnp.control.ActionListener;
import top.ienjoy.cybergarage.upnp.control.ActionRequest;
import top.ienjoy.cybergarage.upnp.control.ActionResponse;
import top.ienjoy.cybergarage.upnp.control.ControlResponse;
import top.ienjoy.cybergarage.upnp.xml.ActionData;
import top.ienjoy.cybergarage.util.Debug;
import top.ienjoy.cybergarage.util.Mutex;
import top.ienjoy.cybergarage.xml.Node;

@SuppressWarnings("unused")
public class Action {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "action";

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private Node serviceNode;
    private Node actionNode;

    private Node getServiceNode() {
        return serviceNode;
    }

    public Service getService() {
        return new Service(getServiceNode());
    }

    void setService(Service s) {
        serviceNode = s.getServiceNode();
        /*To ensure integrity of the XML structure*/
        for (Argument o : getArgumentList()) {
            o.setService(s);
        }
    }

    public Node getActionNode() {
        return actionNode;
    }

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////
    public Action(Node serviceNode) {
        this.serviceNode = serviceNode;
        this.actionNode = new Node(Action.ELEM_NAME);
    }

    public Action(Node serviceNode, Node actionNode) {
        this.serviceNode = serviceNode;
        this.actionNode = actionNode;
    }

    public Action(Action action) {
        this.serviceNode = action.getServiceNode();
        this.actionNode = action.getActionNode();
    }

    ////////////////////////////////////////////////
    // Mutex
    ////////////////////////////////////////////////

    private Mutex mutex = new Mutex();

    public void lock() {
        mutex.lock();
    }

    public void unlock() {
        mutex.unlock();
    }

    ////////////////////////////////////////////////
    //	isActionNode
    ////////////////////////////////////////////////

    public static boolean isActionNode(Node node) {
        return Action.ELEM_NAME.equals(node.getName());
    }

    ////////////////////////////////////////////////
    //	name
    ////////////////////////////////////////////////

    private final static String NAME = "name";

    public void setName(String value) {
        getActionNode().setNode(NAME, value);
    }

    public String getName() {
        return getActionNode().getNodeValue(NAME);
    }

    ////////////////////////////////////////////////
    //	argumentList
    ////////////////////////////////////////////////

    public ArgumentList getArgumentList() {
        ArgumentList argumentList = new ArgumentList();
        Node argumentListNode = getActionNode().getNode(ArgumentList.ELEM_NAME);
        if (argumentListNode == null)
            return argumentList;
        int nodeCnt = argumentListNode.getNNodes();
        for (int n = 0; n < nodeCnt; n++) {
            Node node = argumentListNode.getNode(n);
            if (!Argument.isArgumentNode(node))
                continue;
            Argument argument = new Argument(getServiceNode(), node);
            argumentList.add(argument);
        }
        return argumentList;
    }

    public void setArgumentList(ArgumentList al) {
        Node argumentListNode = getActionNode().getNode(ArgumentList.ELEM_NAME);
        if (argumentListNode == null) {
            argumentListNode = new Node(ArgumentList.ELEM_NAME);
            getActionNode().addNode(argumentListNode);
        } else {
            argumentListNode.removeAllNodes();
        }
        for (Argument a : al) {
            a.setService(getService());
            argumentListNode.addNode(a.getArgumentNode());
        }
    }

    public ArgumentList getInputArgumentList() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        ArgumentList argList = new ArgumentList();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (!arg.isInDirection())
                continue;
            argList.add(arg);
        }
        return argList;
    }

    public ArgumentList getOutputArgumentList() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        ArgumentList argList = new ArgumentList();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (!arg.isOutDirection())
                continue;
            argList.add(arg);
        }
        return argList;
    }

    public Argument getArgument(String name) {
        ArgumentList argList = getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            String argName = arg.getName();
            if (argName == null)
                continue;
            if (name.equals(argName))
                return arg;
        }
        return null;
    }

    /**
     * @since 1.8.0
     */
    public void setInArgumentValues(ArgumentList argList) {
        getArgumentList().setReqArgs(argList);
    }

    /**
     * @since 1.8.0
     */
    public void setOutArgumentValues(ArgumentList argList) {
        getArgumentList().setResArgs(argList);
    }

    public void setArgumentValue(String name, String value) {
        Argument arg = getArgument(name);
        if (arg == null)
            return;
        arg.setValue(value);
    }

    public void setArgumentValue(String name, int value) {
        setArgumentValue(name, Integer.toString(value));
    }

    private void clearOutputAgumentValues() {
        ArgumentList allArgList = getArgumentList();
        int allArgCnt = allArgList.size();
        for (int n = 0; n < allArgCnt; n++) {
            Argument arg = allArgList.getArgument(n);
            if (!arg.isOutDirection())
                continue;
            arg.setValue("");
        }
    }

    public String getArgumentValue(String name) {
        Argument arg = getArgument(name);
        if (arg == null)
            return "";
        return arg.getValue();
    }

    public int getArgumentIntegerValue(String name) {
        Argument arg = getArgument(name);
        if (arg == null)
            return 0;
        return arg.getIntegerValue();
    }

    ////////////////////////////////////////////////
    //	UserData
    ////////////////////////////////////////////////

    private ActionData getActionData() {
        Node node = getActionNode();
        ActionData userData = (ActionData) node.getUserData();
        if (userData == null) {
            userData = new ActionData();
            node.setUserData(userData);
            userData.setNode(node);
        }
        return userData;
    }

    ////////////////////////////////////////////////
    //	controlAction
    ////////////////////////////////////////////////

    public ActionListener getActionListener() {
        return getActionData().getActionListener();
    }

    public void setActionListener(ActionListener listener) {
        getActionData().setActionListener(listener);
    }

    public boolean performActionListener(ActionRequest actionReq) {
        ActionListener listener = getActionListener();
        if (listener == null)
            return false;
        ActionResponse actionRes = new ActionResponse();
        setStatus(UPnPStatus.INVALID_ACTION);
        clearOutputAgumentValues();
        if (listener.actionControlReceived(this)) {
            actionRes.setResponse(this);
        } else {
            UPnPStatus upnpStatus = getStatus();
            actionRes.setFaultResponse(upnpStatus.getCode(), upnpStatus.getDescription());
        }
        if (Debug.isOn())
            actionRes.print();
        actionReq.post(actionRes);
        return true;
    }

    ////////////////////////////////////////////////
    //	ActionControl
    ////////////////////////////////////////////////

    private ControlResponse getControlResponse() {
        return getActionData().getControlResponse();
    }

    private void setControlResponse(ControlResponse res) {
        getActionData().setControlResponse(res);
    }

    public UPnPStatus getControlStatus() {
        return getControlResponse().getUPnPError();
    }

    ////////////////////////////////////////////////
    //	postControlAction
    ////////////////////////////////////////////////

    public boolean postControlAction() {
        // Thanks for Giordano Sassaroli <sassarol@cefriel.it> (08/30/03)
        ArgumentList actionArgList = getArgumentList();
        ArgumentList actionInputArgList = getInputArgumentList();
        ActionRequest ctrlReq = new ActionRequest();
        ctrlReq.setRequest(this, actionInputArgList);
        if (Debug.isOn())
            ctrlReq.print();
        ActionResponse ctrlRes = ctrlReq.post();
        if (Debug.isOn())
            ctrlRes.print();
        setControlResponse(ctrlRes);
        // Thanks for Dimas <cyberrate@users.sourceforge.net> and Stefano Lenzi <kismet-sl@users.sourceforge.net> (07/09/04)
        int statCode = ctrlRes.getStatusCode();
        setStatus(statCode);
        if (!ctrlRes.isSuccessful())
            return false;
        ArgumentList outArgList = ctrlRes.getResponse();
        try {
            actionArgList.setResArgs(outArgList);
        } catch (IllegalArgumentException ex) {
            setStatus(UPnPStatus.INVALID_ARGS, "Action succesfully delivered but invalid arguments returned.");
            return false;
        }
        return true;
    }

    ////////////////////////////////////////////////
    //	Debug
    ////////////////////////////////////////////////

    public void print() {
        System.out.println("Action : " + getName());
        ArgumentList argList = getArgumentList();
        int nArgs = argList.size();
        for (int n = 0; n < nArgs; n++) {
            Argument arg = argList.getArgument(n);
            String name = arg.getName();
            String value = arg.getValue();
            String dir = arg.getDirection();
            System.out.println(" [" + n + "] = " + dir + ", " + name + ", " + value);
        }
    }

    ////////////////////////////////////////////////
    //	UPnPStatus
    ////////////////////////////////////////////////

    private UPnPStatus upnpStatus = new UPnPStatus();

    public void setStatus(int code, String descr) {
        upnpStatus.setCode(code);
        upnpStatus.setDescription(descr);
    }

    public void setStatus(int code) {
        setStatus(code, UPnPStatus.code2String(code));
    }

    public UPnPStatus getStatus() {
        return upnpStatus;
    }

    ////////////////////////////////////////////////
    //	userData
    ////////////////////////////////////////////////

    private Object userData = null;

    public void setUserData(Object data) {
        userData = data;
    }

    public Object getUserData() {
        return userData;
    }
}

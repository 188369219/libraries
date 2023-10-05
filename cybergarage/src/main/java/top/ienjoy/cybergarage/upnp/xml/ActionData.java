package top.ienjoy.cybergarage.upnp.xml;

import top.ienjoy.cybergarage.upnp.control.*;

public class ActionData extends NodeData {
    public ActionData() {
    }

    ////////////////////////////////////////////////
    // ActionListener
    ////////////////////////////////////////////////

    private ActionListener actionListener = null;

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    ////////////////////////////////////////////////
    // ControlResponse
    ////////////////////////////////////////////////

    private ControlResponse ctrlRes = null;

    public ControlResponse getControlResponse() {
        return ctrlRes;
    }

    public void setControlResponse(ControlResponse res) {
        ctrlRes = res;
    }

}


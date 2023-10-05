package top.ienjoy.cybergarage.upnp.xml;

import top.ienjoy.cybergarage.xml.*;

public class NodeData {
    public NodeData() {
        setNode(null);
    }

    ////////////////////////////////////////////////
    // Node
    ////////////////////////////////////////////////

    private Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}


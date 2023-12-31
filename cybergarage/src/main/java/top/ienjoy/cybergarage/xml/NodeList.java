package top.ienjoy.cybergarage.xml;

import java.util.Vector;

public class NodeList extends Vector<Node> {
    public NodeList() {
    }

    public Node getNode(int n) {
        return get(n);
    }

    public synchronized Node getNode(String name) {
        if (name == null)
            return null;

        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            String nodeName = node.getName();
            if (name.compareTo(nodeName) == 0)
                return node;
        }
        return null;
    }

    public synchronized Node getEndsWith(String name) {
        if (name == null)
            return null;

        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Node node = getNode(n);
            String nodeName = node.getName();
            if (nodeName == null)
                continue;
            if (nodeName.endsWith(name))
                return node;
        }
        return null;
    }
}


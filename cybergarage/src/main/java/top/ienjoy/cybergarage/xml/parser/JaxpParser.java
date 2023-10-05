package top.ienjoy.cybergarage.xml.parser;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import top.ienjoy.cybergarage.xml.Node;
import top.ienjoy.cybergarage.xml.Parser;
import top.ienjoy.cybergarage.xml.ParserException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class JaxpParser extends Parser {

    public JaxpParser() {
        super();
    }

    ////////////////////////////////////////////////
    //	parse (Node)
    ////////////////////////////////////////////////

    public top.ienjoy.cybergarage.xml.Node parse(top.ienjoy.cybergarage.xml.Node parentNode, org.w3c.dom.Node domNode, int rank) {
        int domNodeType = domNode.getNodeType();

        String domNodeName = domNode.getNodeName();
        String domNodeValue = domNode.getNodeValue();
        NamedNodeMap attrs = domNode.getAttributes();
        int arrrsLen = (attrs != null) ? attrs.getLength() : 0;

//		Debug.message("[" + rank + "] ELEM : " + domNodeName + ", " + domNodeValue + ", type = " + domNodeType + ", attrs = " + arrrsLen);

        if (domNodeType == org.w3c.dom.Node.TEXT_NODE) {
            // Change to use Node::addValue() instead of the setValue(). (2008/02/07)
            //parentNode.setValue(domNodeValue);
            parentNode.addValue(domNodeValue);
            return parentNode;
        }

        if (domNodeType != org.w3c.dom.Node.ELEMENT_NODE)
            return parentNode;

        top.ienjoy.cybergarage.xml.Node node = new top.ienjoy.cybergarage.xml.Node();
        node.setName(domNodeName);
        node.setValue(domNodeValue);

        if (parentNode != null)
            parentNode.addNode(node);

        NamedNodeMap attrMap = domNode.getAttributes();
        if (attrMap != null) {
            int attrLen = attrMap.getLength();
            //Debug.message("attrLen = " + attrLen);
            for (int n = 0; n < attrLen; n++) {
                org.w3c.dom.Node attr = attrMap.item(n);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                node.setAttribute(attrName, attrValue);
            }
        }

        org.w3c.dom.Node child = domNode.getFirstChild();
        if (child == null) {
            node.setValue("");
            return node;
        }
        do {
            parse(node, child, rank + 1);
            child = child.getNextSibling();
        } while (child != null);

        return node;
    }

    public top.ienjoy.cybergarage.xml.Node parse(top.ienjoy.cybergarage.xml.Node parentNode, org.w3c.dom.Node domNode) {
        return parse(parentNode, domNode, 0);
    }

    /* (non-Javadoc)
     * @see top.ienjoy.cybergarage.xml.Parser#parse(java.io.InputStream)
     */
    public Node parse(InputStream inStream) throws ParserException {
        top.ienjoy.cybergarage.xml.Node root = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inSrc = new InputSource(inStream);
            Document doc = builder.parse(inSrc);

            org.w3c.dom.Element docElem = doc.getDocumentElement();

            if (docElem != null)
                root = parse(root, docElem);
        } catch (Exception e) {
            throw new ParserException(e);
        }

        return root;
    }

}

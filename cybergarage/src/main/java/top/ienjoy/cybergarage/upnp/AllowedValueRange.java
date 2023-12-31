package top.ienjoy.cybergarage.upnp;

import top.ienjoy.cybergarage.xml.Node;

@SuppressWarnings("unused")
public class AllowedValueRange {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "allowedValueRange";

    ////////////////////////////////////////////////
    //	Member
    ////////////////////////////////////////////////

    private Node allowedValueRangeNode;

    public Node getAllowedValueRangeNode() {
        return allowedValueRangeNode;
    }

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public AllowedValueRange(Node node) {
        allowedValueRangeNode = node;
    }

    public AllowedValueRange() {
        allowedValueRangeNode = new Node(ELEM_NAME);
    }
    ////////////////////////////////////////////////
    //	isAllowedValueRangeNode
    ////////////////////////////////////////////////

    public AllowedValueRange(Number max, Number min, Number step) {
        allowedValueRangeNode = new Node(ELEM_NAME);
        if (max != null)
            setMaximum(max.toString());
        if (min != null)
            setMinimum(min.toString());
        if (step != null)
            setStep(step.toString());
    }

    public static boolean isAllowedValueRangeNode(Node node) {
        return ELEM_NAME.equals(node.getName());
    }

    ////////////////////////////////////////////////
    //	minimum
    ////////////////////////////////////////////////

    private final static String MINIMUM = "minimum";

    public void setMinimum(String value) {
        getAllowedValueRangeNode().setNode(MINIMUM, value);
    }

    public String getMinimum() {
        return getAllowedValueRangeNode().getNodeValue(MINIMUM);
    }

    ////////////////////////////////////////////////
    //	maximum
    ////////////////////////////////////////////////

    private final static String MAXIMUM = "maximum";

    public void setMaximum(String value) {
        getAllowedValueRangeNode().setNode(MAXIMUM, value);
    }

    public String getMaximum() {
        return getAllowedValueRangeNode().getNodeValue(MAXIMUM);
    }

    ////////////////////////////////////////////////
    //	width
    ////////////////////////////////////////////////

    private final static String STEP = "step";

    public void setStep(String value) {
        getAllowedValueRangeNode().setNode(STEP, value);
    }

    public String getStep() {
        return getAllowedValueRangeNode().getNodeValue(STEP);
    }
}

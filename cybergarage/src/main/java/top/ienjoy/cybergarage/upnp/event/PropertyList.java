package top.ienjoy.cybergarage.upnp.event;

import java.util.*;

public class PropertyList extends Vector<Property> {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "PropertyList";

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public PropertyList() {
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public Property getProperty(int n) {
        return get(n);
    }
}

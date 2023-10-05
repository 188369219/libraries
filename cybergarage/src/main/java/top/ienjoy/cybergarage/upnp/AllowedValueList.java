package top.ienjoy.cybergarage.upnp;

import java.util.Vector;

@SuppressWarnings("unused")
public class AllowedValueList extends Vector<AllowedValue> {
    ////////////////////////////////////////////////
    //	Constants
    ////////////////////////////////////////////////

    public final static String ELEM_NAME = "allowedValueList";


    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public AllowedValueList() {
    }

    public AllowedValueList(String[] values) {
        for (String value : values) {
            add(new AllowedValue(value));
        }
    }


    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public AllowedValue getAllowedValue(int n) {
        return get(n);
    }

    public boolean isAllowed(String v) {
        for (AllowedValue av : this) {
            if (av.getValue().equals(v))
                return true;
        }
        return false;
    }
}

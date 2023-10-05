package top.ienjoy.cybergarage.upnp.event;

public class Property {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public Property() {
    }

    ////////////////////////////////////////////////
    //	name
    ////////////////////////////////////////////////

    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String val) {
        if (val == null)
            val = "";
        name = val;
    }

    ////////////////////////////////////////////////
    //	value
    ////////////////////////////////////////////////

    private String value = "";

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        if (val == null)
            val = "";
        value = val;
    }
}

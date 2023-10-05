package top.ienjoy.cybergarage.http;

public class Parameter {
    private String name = "";
    private String value = "";

    public Parameter() {
    }

    public Parameter(String name, String value) {
        setName(name);
        setValue(value);
    }

    ////////////////////////////////////////////////
    //	name
    ////////////////////////////////////////////////

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    ////////////////////////////////////////////////
    //	value
    ////////////////////////////////////////////////

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


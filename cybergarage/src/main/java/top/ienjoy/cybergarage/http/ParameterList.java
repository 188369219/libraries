package top.ienjoy.cybergarage.http;

import java.util.Vector;

public class ParameterList extends Vector<Parameter> {
    public ParameterList() {
    }

    public Parameter at(int n) {
        return get(n);
    }

    public Parameter getParameter(int n) {
        return get(n);
    }

    public Parameter getParameter(String name) {
        if (name == null)
            return null;

        int nLists = size();
        for (int n = 0; n < nLists; n++) {
            Parameter param = at(n);
            if (name.compareTo(param.getName()) == 0)
                return param;
        }
        return null;
    }

    public String getValue(String name) {
        Parameter param = getParameter(name);
        if (param == null)
            return "";
        return param.getValue();
    }
}


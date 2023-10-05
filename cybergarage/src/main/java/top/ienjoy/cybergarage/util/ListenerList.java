package top.ienjoy.cybergarage.util;

import java.util.Vector;

public class ListenerList extends Vector<Object> {
    public boolean add(Object obj) {
        if (0 <= indexOf(obj))
            return false;
        return super.add(obj);
    }
}


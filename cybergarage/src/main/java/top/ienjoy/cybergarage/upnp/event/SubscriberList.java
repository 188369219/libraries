package top.ienjoy.cybergarage.upnp.event;

import java.util.*;

public class SubscriberList extends Vector<Subscriber> {
    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public SubscriberList() {
    }

    ////////////////////////////////////////////////
    //	Methods
    ////////////////////////////////////////////////

    public Subscriber getSubscriber(int n) {
        Subscriber obj = null;
        try {
            obj = get(n);
        } catch (Exception ignored) {
        }
        return obj;
    }
}


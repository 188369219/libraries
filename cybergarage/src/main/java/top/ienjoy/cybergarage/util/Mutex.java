package top.ienjoy.cybergarage.util;

public class Mutex {
    private boolean syncLock;

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public Mutex() {
        syncLock = false;
    }

    ////////////////////////////////////////////////
    //	lock
    ////////////////////////////////////////////////

    public synchronized void lock() {
        while (syncLock) {
            try {
                wait();
            } catch (Exception e) {
                Debug.warning(e);
            }
        }
        syncLock = true;
    }

    public synchronized void unlock() {
        syncLock = false;
        notifyAll();
    }

}
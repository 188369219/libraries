package top.ienjoy.cybergarage.util;

public final class TimerUtil {
    public static void wait(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (Exception ignored) {
        }
    }

    public static void waitRandom(int time) {
        int waitTime = (int) (Math.random() * (double) time);
        try {
            Thread.sleep(waitTime);
        } catch (Exception ignored) {
        }
    }
}


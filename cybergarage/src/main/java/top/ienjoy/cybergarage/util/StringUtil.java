package top.ienjoy.cybergarage.util;

@SuppressWarnings("unused")
public final class StringUtil {
    public static boolean hasData(String value) {
        if (value == null)
            return false;
        return value.length() > 0;
    }

    public static int toInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Debug.warning(e);
        }
        return 0;
    }

    public static long toLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Debug.warning(e);
        }
        return 0;
    }

    public static int findOf(String str, String chars, int startIdx, int endIdx, int offset, boolean isEqual) {
        if (offset == 0)
            return -1;
        int charCnt = chars.length();
        int idx = startIdx;
        while (true) {
            if (0 < offset) {
                if (endIdx < idx)
                    break;
            } else {
                if (idx < endIdx)
                    break;
            }
            char strc = str.charAt(idx);
            int noEqualCnt = 0;
            for (int n = 0; n < charCnt; n++) {
                char charc = chars.charAt(n);
                if (isEqual) {
                    if (strc == charc)
                        return idx;
                } else {
                    if (strc != charc)
                        noEqualCnt++;
                    if (noEqualCnt == charCnt)
                        return idx;
                }
            }
            idx += offset;
        }
        return -1;
    }

    public static int findFirstOf(String str, String chars) {
        return findOf(str, chars, 0, (str.length() - 1), 1, true);
    }

    public static int findFirstNotOf(String str, String chars) {
        return findOf(str, chars, 0, (str.length() - 1), 1, false);
    }

    public static int findLastOf(String str, String chars) {
        return findOf(str, chars, (str.length() - 1), 0, -1, true);
    }

    public static int findLastNotOf(String str, String chars) {
        return findOf(str, chars, (str.length() - 1), 0, -1, false);
    }

    public static String trim(String trimStr, String trimChars) {
        int spIdx = findFirstNotOf(trimStr, trimChars);
        if (spIdx < 0) {
            return trimStr;
        }
        String trimStr2 = trimStr.substring(spIdx);
        spIdx = findLastNotOf(trimStr2, trimChars);
        if (spIdx < 0) {
            return trimStr2;
        }
        return trimStr2.substring(0, spIdx + 1);
    }
}


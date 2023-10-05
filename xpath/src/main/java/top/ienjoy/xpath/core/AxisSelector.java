package top.ienjoy.xpath.core;

import top.ienjoy.jsoup.select.Elements;

/**
 * <a href="https://www.w3.org/TR/1999/REC-xpath-19991116/#NT-AxisSpecifier">...</a>
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public interface AxisSelector {
    /**
     * assign name
     * @return name
     */
    String name();

    /**
     *
     * @return res
     */
    XValue apply(Elements context);
}

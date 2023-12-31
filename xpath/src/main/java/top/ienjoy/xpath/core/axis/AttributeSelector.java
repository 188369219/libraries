package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

/**
 * the attribute axis contains the attributes of the context node; the axis will be empty unless the context node is an element
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class AttributeSelector implements AxisSelector{
    /**
     * assign name
     *
     * @return name
     */
    @Override
    public String name() {
        return "attribute";
    }

    /**
     * @return res
     */
    @Override
    public XValue apply(Elements context) {
        return XValue.create(null).attr();
    }
}

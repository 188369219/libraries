package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * the preceding-sibling-one axis JsoupXpath自定义扩展，用于选取节点的前一个兄弟节点，如果存在的话。
 *
 * @author github.com/hermitmmll
 * @since 2018/3/27.
 */
public class PrecedingSiblingOneSelector implements AxisSelector {
    /**
     * assign name
     *
     * @return name
     */
    @Override
    public String name() {
        return "preceding-sibling-one";
    }

    /**
     * @return res
     */
    @Override
    public XValue apply(Elements context) {
        List<Element> total = new LinkedList<>();
        for (Element el : context){
            if (el.previousElementSibling()!=null){
                total.add(el.previousElementSibling());
            }
        }
        Elements newContext = new Elements();
        newContext.addAll(total);
        return XValue.create(newContext);
    }
}

package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * the following-sibling-one JsoupXpath自定义扩展,比较常用
 *
 * @author <a href="https://github.com/hermitmmll">...</a>
 * @since 2018/3/27.
 */
public class FollowingSiblingOneSelector implements AxisSelector {
    /**
     * assign name
     *
     * @return name
     */
    @Override
    public String name() {
        return "following-sibling-one";
    }

    /**
     *
     * @return res
     */
    @Override
    public XValue apply(Elements context) {
        List<Element> total = new LinkedList<>();
        for (Element el : context){
            if (el.nextElementSibling()!=null){
                total.add(el.nextElementSibling());
            }
        }
        Elements newContext = new Elements();
        newContext.addAll(total);
        return XValue.create(newContext);
    }
}

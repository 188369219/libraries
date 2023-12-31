package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.HashSet;
import java.util.Set;

/**
 * the descendant-or-self axis contains the context node and the descendants of the context node
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class DescendantOrSelfSelector implements AxisSelector {
    @Override
    public String name() {
        return "descendant-or-self";
    }

    @Override
    public XValue apply(Elements context) {
        Set<Element> total = new HashSet<>();
        Elements descendant = new Elements();
        for (Element el:context){
            Elements tmp = el.getAllElements();
            total.addAll(tmp);
        }
        descendant.addAll(total);
        return XValue.create(descendant);
    }
}

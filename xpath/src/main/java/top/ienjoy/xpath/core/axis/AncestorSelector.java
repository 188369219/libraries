package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * the ancestor axis contains the ancestors of the context node; the ancestors of the context node consist of
 * the parent of context node and the parent's parent and so on; thus, the ancestor axis will always include
 * the root node, unless the context node is the root node
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class AncestorSelector implements AxisSelector {
    @Override
    public String name() {
        return "ancestor";
    }

    @Override
    public XValue apply(Elements context) {
        List<Element> total = new LinkedList<>();
        for (Element el:context){
            total.addAll(el.parents());
        }
        return XValue.create(new Elements(total));
    }
}

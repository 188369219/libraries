package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.HashSet;
import java.util.Set;

/**
 * the descendant axis contains the descendants of the context node; a descendant is a child or a child
 * of a child and so on; thus the descendant axis never contains attribute or namespace nodes
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class DescendantSelector implements AxisSelector {
    @Override
    public String name() {
        return "descendant";
    }

    @Override
    public XValue apply(Elements context) {
        Set<Element> total = new HashSet<>();
        Elements descendant = new Elements();
        for (Element el:context){
            Elements tmp = el.getAllElements();
            //exclude self
            tmp.remove(el);
            total.addAll(tmp);
        }
        descendant.addAll(total);
        return XValue.create(descendant);
    }
}

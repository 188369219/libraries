package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * the parent axis contains the parent of the context node, if there is one
 * <a href="https://www.w3.org/TR/1999/REC-xpath-19991116/#NT-AxisSpecifier">...</a>
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public class ParentSelector implements AxisSelector {
    @Override
    public String name() {
        return "parent";
    }

    @Override
    public XValue apply(Elements context) {
        List<Element> total = new LinkedList<>();
        Elements parents = new Elements();
        for (Element el:context){
            total.add(el.parent());
        }
        parents.addAll(total);
        return XValue.create(parents);
    }
}

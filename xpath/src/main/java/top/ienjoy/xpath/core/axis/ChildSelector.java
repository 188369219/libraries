package top.ienjoy.xpath.core.axis;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;
import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.XValue;

/**
 * the child axis contains the children of the context node
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class ChildSelector implements AxisSelector {
    @Override
    public String name() {
        return "child";
    }

    @Override
    public XValue apply(Elements context) {
        Elements childs = new Elements();
        for (Element el:context){
            childs.addAll(el.children());
        }
        return XValue.create(childs);
    }
}

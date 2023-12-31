package top.ienjoy.xpath.core.node;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * 获取全部节点的 包含节点本身在内的全部html
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/4/9.
 */
public class OuterHtml implements NodeTest{
    @Override
    public String name() {
        return "outerHtml";
    }

    @Override
    public XValue call(Scope scope) {
        List<String> res = new LinkedList<>();
        for (Element e:scope.context()){
            res.add(e.outerHtml());
        }
        return XValue.create(res);
    }
}

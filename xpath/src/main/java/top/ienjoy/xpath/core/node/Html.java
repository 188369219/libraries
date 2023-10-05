package top.ienjoy.xpath.core.node;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;

import java.util.LinkedList;
import java.util.List;

/**
 * 获取全部节点的内部的html
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/4/9.
 */
public class Html implements NodeTest {
    @Override
    public String name() {
        return "html";
    }

    @Override
    public XValue call(Scope scope) {
        List<String> res = new LinkedList<>();
        for (Element e:scope.context()){
            res.add(e.html());
        }
        return XValue.create(res);
    }
}

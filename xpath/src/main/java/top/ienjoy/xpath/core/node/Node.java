package top.ienjoy.xpath.core.node;

import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;
import org.apache.commons.lang3.StringUtils;
import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;

/**
 * 获取当前节点下所有子节点以及独立文本
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/4/4.
 */
public class Node implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "node";
    }

    /**
     * 函数具体逻辑
     *
     * @param scope 上下文
     * @return 计算好的节点
     */
    @Override
    public XValue call(Scope scope) {
        Elements context = new Elements();
        for (Element el:scope.context()){
            context.addAll(el.children());
            String  txt = el.ownText();
            if (StringUtils.isNotBlank(txt)){
                Element et = new Element("");
                et.appendText(txt);
                context.add(et);
            }
        }
        return XValue.create(context);
    }
}

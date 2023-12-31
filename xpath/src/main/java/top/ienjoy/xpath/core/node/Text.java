package top.ienjoy.xpath.core.node;

import org.apache.commons.lang3.StringUtils;

import top.ienjoy.jsoup.nodes.Node;
import top.ienjoy.jsoup.nodes.TextNode;
import top.ienjoy.jsoup.select.NodeTraversor;
import top.ienjoy.jsoup.select.NodeVisitor;
import top.ienjoy.xpath.core.Constants;
import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;
import top.ienjoy.xpath.util.CommonUtil;
import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 * `text()`不再简单的返回节点下的所有文本，而是按照标准语义识别出多个文本块，返回文本块列表，如
 * ```
 * <p> one <span> two</span> three </p>
 * ```
 * - `//text()` 返回  `["one", "two", "three" ]`
 * - `//text()[2]` 返回  `["three"]`
 */
@SuppressWarnings("all")
public class Text implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "text";
    }

    /**
     * 函数具体逻辑
     *
     * @param scope 上下文
     * @return 计算好的节点
     */
    @Override
    public XValue call(Scope scope) {
        Elements context = scope.context();
        final Elements res = new Elements();
        final Map<String, Integer> indexMap = new HashMap<>();
        if (context != null && context.size() > 0) {
            if (scope.isRecursion()) {
                for (final Element e : context) {
                    NodeTraversor.traverse(new NodeVisitor() {
                        @Override
                        public void head(Node node, int depth) {
                            if (node instanceof TextNode textNode) {
                                String key = depth + "_" + Objects.requireNonNull(textNode.parent()).hashCode();
                                Integer index = indexMap.get(key);
                                if (index == null) {
                                    index = 1;
                                    indexMap.put(key, index);
                                } else {
                                    index += 1;
                                    indexMap.put(key, index);
                                }
                                Element data = new Element(Constants.DEF_TEXT_TAG_NAME);
                                data.text(textNode.getWholeText());
                                data.attr(Constants.EL_DEPTH_KEY, key);
                                try {
                                    Method parent = Node.class.getDeclaredMethod("setParentNode", Node.class);
                                    parent.setAccessible(true);
                                    parent.invoke(data, textNode.parent());
                                } catch (Exception e) {
                                    //ignore
                                }
                                CommonUtil.setSameTagIndexInSiblings(data, index);
                                res.add(data);
                            }
                        }

                        @Override
                        public void tail(Node node, int depth) {

                        }
                    }, e);
                }
                for (Element e : res) {
                    String depthKey = e.attr(Constants.EL_DEPTH_KEY);
                    if (StringUtils.isNotBlank(depthKey)) {
                        Integer maxNumInChildren = indexMap.get(depthKey);
                        if (maxNumInChildren == null) {
                            continue;
                        }
                        CommonUtil.setSameTagNumsInSiblings(e, maxNumInChildren);
                    }
                }
            } else {
                for (Element e : context) {
                    if ("script".equals(e.nodeName())) {
                        Element data = new Element(Constants.DEF_TEXT_TAG_NAME);
                        data.text(e.data());
                        CommonUtil.setSameTagIndexInSiblings(data, 1);
                        CommonUtil.setSameTagNumsInSiblings(data, 1);
                        res.add(data);
                    } else {
                        List<TextNode> textNodes = e.textNodes();
                        for (int i = 0; i < textNodes.size(); i++) {
                            TextNode textNode = textNodes.get(i);
                            Element data = new Element(Constants.DEF_TEXT_TAG_NAME);
                            data.text(textNode.getWholeText());
                            CommonUtil.setSameTagIndexInSiblings(data, i + 1);
                            CommonUtil.setSameTagNumsInSiblings(data, textNodes.size());
                            res.add(data);
                        }
                    }
                }
            }
        }

        return XValue.create(res);
    }
}

package top.ienjoy.xpath.util;

import org.apache.commons.lang3.StringUtils;
import top.ienjoy.jsoup.nodes.Node;
import top.ienjoy.jsoup.nodes.TextNode;
import top.ienjoy.xpath.core.Constants;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.jsoup.select.Elements;

import java.util.Objects;

/**
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * Date: 14-3-15
 */
@SuppressWarnings("unused")
public class CommonUtil {

    /**
     * 获取同名元素在同胞中的index
     */
    public static int getElIndexInSameTags(Element e,Scope scope){
        Elements chs = e.parent() != null ? e.parent().children() : new Elements();
        int index = 1;
        for (Element cur : chs) {
            if (e.tagName().equals(cur.tagName()) && scope.context().contains(cur)) {
                if (e.equals(cur)) {
                    break;
                } else {
                    index += 1;
                }
            }
        }
        return index;
    }


    /**
     * 获取同胞中同名元素的数量
     * Jsoup文档模型中，空白行和元素均属于同胞也有自己独立的siblingIndex，这对于xpath语法统计，空白行等是没有任何意义的，不应该计入siblingIndex。所以需要自行独立统计，不能直接使用siblingIndex。
     * @return --
     */
    public static int sameTagElNums(Element e,Scope scope){
        Elements context = new Elements();
        Elements els = e.parent() != null ? e.parent().getElementsByTag(e.tagName()) : new Elements();
        for (Element el:els){
            if (scope.context().contains(el)){
                context.add(el);
            }
        }
        return context.size();
    }

    public static int getIndexInContext(Scope scope,Element el){
        for (int i = 0;i<scope.context().size();i++){
            Element tmp = scope.context().get(i);
            if (Objects.equals(tmp,el)){
                return i+1;
            }
        }
        return Integer.MIN_VALUE;
    }

     public static Elements followingSibling(Element el){
        Elements rs = new Elements();
        Node tmp = el.nextSibling();
        while (tmp!=null){
         if (tmp instanceof Element ){
             rs.add((Element) tmp);
         } else if (tmp instanceof TextNode) {
             Element txt = new Element("text");
             txt.text(((TextNode) tmp).text());
             rs.add(txt);
         }
         tmp = tmp.nextSibling();
        }
        if (rs.size() > 0){
            return rs;
        }
        return null;
    }

    public static Elements precedingSibling(Element el){
        Elements rs = new Elements();
        Node tmp = el.previousSibling();
        while (tmp!=null){
            if (tmp instanceof Element ){
                rs.add((Element) tmp);
            } else if (tmp instanceof TextNode) {
                Element txt = new Element("text");
                txt.text(((TextNode) tmp).text());
                rs.add(txt);
            }
            tmp = tmp.previousSibling();
        }

        if (rs.size() > 0){
            return rs;
        }
        return null;
    }

    public static void setSameTagIndexInSiblings(Element ori,int index){
        if (ori == null){
            return;
        }
        ori.attr(Constants.EL_SAME_TAG_INDEX_KEY,String.valueOf(index));
    }

    public static int getJxSameTagIndexInSiblings(Element ori){
        String val = ori.attr(Constants.EL_SAME_TAG_INDEX_KEY);
        if (StringUtils.isBlank(val)){
            return -1;
        }
        return Integer.parseInt(val);
    }

    public static void setSameTagNumsInSiblings(Element ori,int nums){
        if (ori == null){
            return;
        }
        ori.attr(Constants.EL_SAME_TAG_ALL_NUM_KEY,String.valueOf(nums));
    }

    public static int getJxSameTagNumsInSiblings(Element ori){
        String val = ori.attr(Constants.EL_SAME_TAG_ALL_NUM_KEY);
        if (StringUtils.isBlank(val)){
            return -1;
        }
        return Integer.parseInt(val);
    }
}

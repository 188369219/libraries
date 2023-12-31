package top.ienjoy.xpath.core.node;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import top.ienjoy.xpath.core.Constants;
import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;
import top.ienjoy.xpath.util.Scanner;

/**
 * 提取自由文本中的数字，如果知道节点的自有文本(即非子代节点所包含的文本)中只存在一个数字，如阅读数，评论数，价格等那么直接可以直接提取此数字出来。
 * 如果有多个数字将提取第一个匹配的连续数字，支持小数，返回double
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class Num implements NodeTest {
    /**
     * 支持的函数名
     */
    @Override
    public String name() {
        return "num";
    }

    /**
     * 函数具体逻辑
     *
     * @param scope 上下文
     * @return 计算好的节点
     */
    @Override
    public XValue call(Scope scope) {
        NodeTest textFun = Scanner.findNodeTestByName("allText");
        XValue textVal = textFun.call(scope);
        String whole = StringUtils.join(textVal.asList(),"");
        Matcher matcher = Constants.NUM_PATTERN.matcher(whole);
        if (matcher.find()){
            String numStr = matcher.group();
            BigDecimal num = new BigDecimal(numStr);
            if (num.compareTo(new BigDecimal(num.longValue()))==0){
                return XValue.create(num.longValue());
            }
            return XValue.create(num.doubleValue());
        }else {
            return XValue.create(null);
        }
    }
}

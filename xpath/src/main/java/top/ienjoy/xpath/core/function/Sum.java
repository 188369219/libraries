package top.ienjoy.xpath.core.function;

import top.ienjoy.jsoup.nodes.Element;
import top.ienjoy.xpath.core.Constants;
import top.ienjoy.xpath.core.Function;
import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.XValue;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Function: number sum(node-set)
 * The sum function returns the sum, for each node in the argument node-set, of the result of converting the string-values of the node to a number.
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2021/11/30.
 */
public class Sum implements Function {

    @Override
    public String name() {
        return "sum";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        if (params == null||params.isEmpty()){
            return XValue.create(0);
        }
        List<Double> pvs = new LinkedList<>();
        for (XValue p:params){
            if (p.isNumber()){
                pvs.add(p.asDouble());
            }
            if (p.isString()){
                Double pv = getNumFromStr(p.asString());
                if (pv == null){
                    return null;
                }
                pvs.add(pv);
            }
            if (p.isElements()){
                for (Element e:p.asElements()){
                    Double pv = getNumFromStr(e.ownText());
                    if (pv == null){
                        return null;
                    }
                    pvs.add(pv);
                }
            }
        }
        Double finalVal = pvs.stream().reduce( 0.0,Double::sum);
        if (finalVal.compareTo(new BigDecimal(finalVal.longValue()).doubleValue()) == 0){
            return XValue.create(finalVal.longValue());
        }else {
            return XValue.create(finalVal);
        }
    }

    private Double getNumFromStr(String str){
        Matcher matcher = Constants.NUM_PATTERN.matcher(str);
        if (matcher.matches()){
            return Double.parseDouble(str);
        }else {
            //出现不满足条件的
            return null;
        }
    }
}

package top.ienjoy.xpath.core.function;

import top.ienjoy.xpath.core.Scope;
import top.ienjoy.xpath.core.Function;
import top.ienjoy.xpath.core.XValue;

import java.util.List;

/**
 * Function: boolean starts-with(string, string)
 * The starts-with function returns true if the first argument string starts with the second argument string, and otherwise returns false.
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/3/26.
 */
public class StartsWith implements Function {
    @Override
    public String name() {
        return "starts-with";
    }

    @Override
    public XValue call(Scope scope, List<XValue> params) {
        String first = params.get(0).asString();
        String second = params.get(1).asString();
        return XValue.create(first.startsWith(second));
    }
}

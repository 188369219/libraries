package top.ienjoy.xpath.util;

import java.util.HashMap;
import java.util.Map;

import top.ienjoy.xpath.core.AxisSelector;
import top.ienjoy.xpath.core.Function;
import top.ienjoy.xpath.core.NodeTest;
import top.ienjoy.xpath.core.axis.AncestorOrSelfSelector;
import top.ienjoy.xpath.core.axis.AncestorSelector;
import top.ienjoy.xpath.core.axis.AttributeSelector;
import top.ienjoy.xpath.core.axis.ChildSelector;
import top.ienjoy.xpath.core.axis.DescendantOrSelfSelector;
import top.ienjoy.xpath.core.axis.DescendantSelector;
import top.ienjoy.xpath.core.axis.FollowingSelector;
import top.ienjoy.xpath.core.axis.FollowingSiblingOneSelector;
import top.ienjoy.xpath.core.axis.FollowingSiblingSelector;
import top.ienjoy.xpath.core.axis.ParentSelector;
import top.ienjoy.xpath.core.axis.PrecedingSelector;
import top.ienjoy.xpath.core.axis.PrecedingSiblingOneSelector;
import top.ienjoy.xpath.core.axis.PrecedingSiblingSelector;
import top.ienjoy.xpath.core.axis.SelfSelector;
import top.ienjoy.xpath.core.function.Concat;
import top.ienjoy.xpath.core.function.Contains;
import top.ienjoy.xpath.core.function.Count;
import top.ienjoy.xpath.core.function.First;
import top.ienjoy.xpath.core.function.FormatDate;
import top.ienjoy.xpath.core.function.Last;
import top.ienjoy.xpath.core.function.Not;
import top.ienjoy.xpath.core.function.Position;
import top.ienjoy.xpath.core.function.StartsWith;
import top.ienjoy.xpath.core.function.StringLength;
import top.ienjoy.xpath.core.function.SubString;
import top.ienjoy.xpath.core.function.SubStringAfter;
import top.ienjoy.xpath.core.function.SubStringAfterLast;
import top.ienjoy.xpath.core.function.SubStringBefore;
import top.ienjoy.xpath.core.function.SubStringBeforeLast;
import top.ienjoy.xpath.core.function.SubStringEx;
import top.ienjoy.xpath.core.function.Sum;
import top.ienjoy.xpath.core.node.AllText;
import top.ienjoy.xpath.core.node.Html;
import top.ienjoy.xpath.core.node.Node;
import top.ienjoy.xpath.core.node.Num;
import top.ienjoy.xpath.core.node.OuterHtml;
import top.ienjoy.xpath.core.node.Text;
import top.ienjoy.xpath.exception.NoSuchAxisException;
import top.ienjoy.xpath.exception.NoSuchFunctionException;

/**
 * 考虑更广泛的兼容性，替换掉 FastClasspathScanner，采用手工注册
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 2018/2/28.
 */
public class Scanner {
    private static final Map<String, AxisSelector> axisSelectorMap = new HashMap<>();
    private static final Map<String, NodeTest> nodeTestMap = new HashMap<>();
    private static final Map<String, Function> functionMap = new HashMap<>();

    static {
        initAxis(AncestorOrSelfSelector.class,AncestorSelector.class,AttributeSelector.class,ChildSelector.class,DescendantOrSelfSelector.class,DescendantSelector.class,FollowingSelector.class,FollowingSiblingOneSelector.class,FollowingSiblingSelector.class,ParentSelector.class,PrecedingSelector.class,PrecedingSiblingOneSelector.class,PrecedingSiblingSelector.class,SelfSelector.class);
        initFunction(Concat.class,Contains.class,Count.class,First.class,Last.class,Not.class,Position.class,StartsWith.class,StringLength.class,SubString.class,SubStringAfter.class,SubStringBefore.class,SubStringEx.class, FormatDate.class,SubStringAfterLast.class,SubStringBeforeLast.class,Sum.class);
        initNode(AllText.class,Html.class,Node.class,Num.class,OuterHtml.class,Text.class);
    }

    public static AxisSelector findSelectorByName(String selectorName) {
        AxisSelector selector = axisSelectorMap.get(selectorName);
        if (selector == null) {
            throw new NoSuchAxisException("not support axis: " + selectorName);
        }
        return selector;
    }

    public static NodeTest findNodeTestByName(String nodeTestName) {
        NodeTest nodeTest = nodeTestMap.get(nodeTestName);
        if (nodeTest == null) {
            throw new NoSuchFunctionException("not support nodeTest: " + nodeTestName);
        }
        return nodeTest;
    }

    public static Function findFunctionByName(String funcName) {
        Function function = functionMap.get(funcName);
        if (function == null) {
            throw new NoSuchFunctionException("not support function: " + funcName);
        }
        return function;
    }

    public static void registerFunction(Class<? extends Function> func){
        Function function;
        try {
            function = func.newInstance();
            functionMap.put(function.name(), function);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerNodeTest(Class<? extends NodeTest> nodeTestClass){
        NodeTest nodeTest;
        try {
            nodeTest = nodeTestClass.newInstance();
            nodeTestMap.put(nodeTest.name(), nodeTest);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerAxisSelector(Class<? extends AxisSelector> axisSelectorClass){
        AxisSelector axisSelector;
        try {
            axisSelector = axisSelectorClass.newInstance();
            axisSelectorMap.put(axisSelector.name(), axisSelector);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SafeVarargs
    public static void initAxis(Class<? extends AxisSelector>... cls){
        for (Class<? extends AxisSelector> axis:cls){
            registerAxisSelector(axis);
        }
    }

    @SafeVarargs
    public static void initFunction(Class<? extends Function>... cls){
        for (Class<? extends Function> func:cls){
            registerFunction(func);
        }
    }

    @SafeVarargs
    public static void initNode(Class<? extends NodeTest>... cls){
        for (Class<? extends NodeTest> node:cls){
            registerNodeTest(node);
        }
    }

}

package top.ienjoy.xpath.exception;

/**
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 * @since 14-3-19
 */
public class XpathSyntaxErrorException extends RuntimeException {
    public XpathSyntaxErrorException(String msg, Throwable e) {
        super(msg, e);
    }
}

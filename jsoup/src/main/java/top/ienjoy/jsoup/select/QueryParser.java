package top.ienjoy.jsoup.select;

import top.ienjoy.jsoup.internal.StringUtil;
import top.ienjoy.jsoup.helper.Validate;
import top.ienjoy.jsoup.parser.TokenQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.ienjoy.jsoup.internal.Normalizer.normalize;

/**
 * Parses a CSS selector into an Evaluator tree.
 */
public class QueryParser {
    private final static char[] Combinators = {',', '>', '+', '~', ' '};
    private final static String[] AttributeEvals = new String[]{"=", "!=", "^=", "$=", "*=", "~="};

    private final TokenQueue tq;
    private final String query;
    private final List<Evaluator> evals = new ArrayList<>();

    /**
     * Create a new QueryParser.
     *
     * @param query CSS query
     */
    private QueryParser(String query) {
        Validate.notEmpty(query);
        query = query.trim();
        this.query = query;
        this.tq = new TokenQueue(query);
    }

    /**
     * Parse a CSS query into an Evaluator.
     *
     * @param query CSS query
     * @return Evaluator
     * @see Selector selector query syntax
     */
    public static Evaluator parse(String query) {
        try {
            QueryParser p = new QueryParser(query);
            return p.parse();
        } catch (IllegalArgumentException e) {
            throw new Selector.SelectorParseException(e.getMessage() == null ? e.getMessage() : "selector parse error");
        }
    }

    /**
     * Parse the query
     *
     * @return Evaluator
     */
    Evaluator parse() {
        tq.consumeWhitespace();

        if (tq.matchesAny(Combinators)) { // if starts with a combinator, use root as elements
            evals.add(new StructuralEvaluator.Root());
            combinator(tq.consume());
        } else {
            evals.add(consumeEvaluator());
        }

        while (!tq.isEmpty()) {
            // hierarchy and extras
            boolean seenWhite = tq.consumeWhitespace();

            if (tq.matchesAny(Combinators)) {
                combinator(tq.consume());
            } else if (seenWhite) {
                combinator(' ');
            } else { // E.class, E#id, E[attr] etc. AND
                evals.add(consumeEvaluator()); // take next el, #. etc off queue
            }
        }

        if (evals.size() == 1)
            return evals.get(0);

        return new CombiningEvaluator.And(evals);
    }

    private void combinator(char combinator) {
        tq.consumeWhitespace();
        String subQuery = consumeSubQuery(); // support multi > childs

        Evaluator rootEval; // the new topmost evaluator
        Evaluator currentEval; // the evaluator the new eval will be combined to. could be root, or rightmost or.
        Evaluator newEval = parse(subQuery); // the evaluator to add into target evaluator
        boolean replaceRightMost = false;

        if (evals.size() == 1) {
            rootEval = currentEval = evals.get(0);
            // make sure OR (,) has precedence:
            if (rootEval instanceof CombiningEvaluator.Or && combinator != ',') {
                currentEval = ((CombiningEvaluator.Or) currentEval).rightMostEvaluator();
                assert currentEval != null; // rightMost signature can return null (if none set), but always will have one by this point
                replaceRightMost = true;
            }
        } else {
            rootEval = currentEval = new CombiningEvaluator.And(evals);
        }
        evals.clear();

        // for most combinators: change the current eval into an AND of the current eval and the new eval
        switch (combinator) {
            case '>' ->
                    currentEval = new CombiningEvaluator.And(new StructuralEvaluator.ImmediateParent(currentEval), newEval);
            case ' ' ->
                    currentEval = new CombiningEvaluator.And(new StructuralEvaluator.Parent(currentEval), newEval);
            case '+' ->
                    currentEval = new CombiningEvaluator.And(new StructuralEvaluator.ImmediatePreviousSibling(currentEval), newEval);
            case '~' ->
                    currentEval = new CombiningEvaluator.And(new StructuralEvaluator.PreviousSibling(currentEval), newEval);
            case ',' -> {
                CombiningEvaluator.Or or;
                if (currentEval instanceof CombiningEvaluator.Or) {
                    or = (CombiningEvaluator.Or) currentEval;
                } else {
                    or = new CombiningEvaluator.Or();
                    or.add(currentEval);
                }
                or.add(newEval);
                currentEval = or;
            }
            default ->
                    throw new Selector.SelectorParseException("Unknown combinator '%s'", combinator);
        }

        if (replaceRightMost)
            ((CombiningEvaluator.Or) rootEval).replaceRightMostEvaluator(currentEval);
        else rootEval = currentEval;
        evals.add(rootEval);
    }

    private String consumeSubQuery() {
        StringBuilder sq = StringUtil.borrowBuilder();
        while (!tq.isEmpty()) {
            if (tq.matches("("))
                sq.append("(").append(tq.chompBalanced('(', ')')).append(")");
            else if (tq.matches("["))
                sq.append("[").append(tq.chompBalanced('[', ']')).append("]");
            else if (tq.matchesAny(Combinators))
                if (sq.length() > 0)
                    break;
                else
                    tq.consume();
            else
                sq.append(tq.consume());
        }
        return StringUtil.releaseBuilder(sq);
    }

    private Evaluator consumeEvaluator() {
        if (tq.matchChomp("#"))
            return byId();
        else if (tq.matchChomp("."))
            return byClass();
        else if (tq.matchesWord() || tq.matches("*|"))
            return byTag();
        else if (tq.matches("["))
            return byAttribute();
        else if (tq.matchChomp("*"))
            return new Evaluator.AllElements();
        else if (tq.matchChomp(":"))
            return parsePseudoSelector();
        else // unhandled
            throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
    }

    private Evaluator parsePseudoSelector() {
        final String pseudo = tq.consumeCssIdentifier();
        return switch (pseudo) {
            case "lt" -> new Evaluator.IndexLessThan(consumeIndex());
            case "gt" -> new Evaluator.IndexGreaterThan(consumeIndex());
            case "eq" -> new Evaluator.IndexEquals(consumeIndex());
            case "has" -> has();
            case "contains" -> contains(false);
            case "containsOwn" -> contains(true);
            case "containsWholeText" -> containsWholeText(false);
            case "containsWholeOwnText" -> containsWholeText(true);
            case "containsData" -> containsData();
            case "matches" -> matches(false);
            case "matchesOwn" -> matches(true);
            case "matchesWholeText" -> matchesWholeText(false);
            case "matchesWholeOwnText" -> matchesWholeText(true);
            case "not" -> not();
            case "nth-child" -> cssNthChild(false, false);
            case "nth-last-child" -> cssNthChild(true, false);
            case "nth-of-type" -> cssNthChild(false, true);
            case "nth-last-of-type" -> cssNthChild(true, true);
            case "first-child" -> new Evaluator.IsFirstChild();
            case "last-child" -> new Evaluator.IsLastChild();
            case "first-of-type" -> new Evaluator.IsFirstOfType();
            case "last-of-type" -> new Evaluator.IsLastOfType();
            case "only-child" -> new Evaluator.IsOnlyChild();
            case "only-of-type" -> new Evaluator.IsOnlyOfType();
            case "empty" -> new Evaluator.IsEmpty();
            case "root" -> new Evaluator.IsRoot();
            case "matchText" -> new Evaluator.MatchText();
            default ->
                    throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", query, tq.remainder());
        };
    }

    private Evaluator byId() {
        String id = tq.consumeCssIdentifier();
        Validate.notEmpty(id);
        return new Evaluator.Id(id);
    }

    private Evaluator byClass() {
        String className = tq.consumeCssIdentifier();
        Validate.notEmpty(className);
        return new Evaluator.Class(className.trim());
    }

    private Evaluator byTag() {
        // the tag in the selector case-sensitive (and also attribute names). But for now, normalize (lower-case) for
        // consistency - both the selector and the element tag
        String tagName = normalize(tq.consumeElementSelector());
        Validate.notEmpty(tagName);
        final Evaluator eval;

        // namespaces: wildcard match equals(tagName) or ending in ":"+tagName
        if (tagName.startsWith("*|")) {
            String plainTag = tagName.substring(2); // strip *|
            eval = new CombiningEvaluator.Or(
                    new Evaluator.Tag(plainTag),
                    new Evaluator.TagEndsWith(tagName.replace("*|", ":"))
            );
        } else {
            // namespaces: if element name is "abc:def", selector must be "abc|def", so flip:
            if (tagName.contains("|"))
                tagName = tagName.replace("|", ":");

            eval = new Evaluator.Tag(tagName);
        }
        return eval;
    }

    private Evaluator byAttribute() {
        TokenQueue cq = new TokenQueue(tq.chompBalanced('[', ']')); // content queue
        String key = cq.consumeToAny(AttributeEvals); // eq, not, start, end, contain, match, (no val)
        Validate.notEmpty(key);
        cq.consumeWhitespace();
        final Evaluator eval;

        if (cq.isEmpty()) {
            if (key.startsWith("^"))
                eval = new Evaluator.AttributeStarting(key.substring(1));
            else
                eval = new Evaluator.Attribute(key);
        } else {
            if (cq.matchChomp("="))
                eval = new Evaluator.AttributeWithValue(key, cq.remainder());
            else if (cq.matchChomp("!="))
                eval = new Evaluator.AttributeWithValueNot(key, cq.remainder());
            else if (cq.matchChomp("^="))
                eval = new Evaluator.AttributeWithValueStarting(key, cq.remainder());
            else if (cq.matchChomp("$="))
                eval = new Evaluator.AttributeWithValueEnding(key, cq.remainder());
            else if (cq.matchChomp("*="))
                eval = new Evaluator.AttributeWithValueContaining(key, cq.remainder());
            else if (cq.matchChomp("~="))
                eval = new Evaluator.AttributeWithValueMatching(key, Pattern.compile(cq.remainder()));
            else
                throw new Selector.SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", query, cq.remainder());
        }
        return eval;
    }

    //pseudo selectors :first-child, :last-child, :nth-child, ...
    private static final Pattern NTH_AB = Pattern.compile("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern NTH_B = Pattern.compile("([+-])?(\\d+)");

    private Evaluator cssNthChild(boolean backwards, boolean ofType) {
        String arg = normalize(consumeParens());
        Matcher mAB = NTH_AB.matcher(arg);
        Matcher mB = NTH_B.matcher(arg);
        final int a, b;
        if ("odd".equals(arg)) {
            a = 2;
            b = 1;
        } else if ("even".equals(arg)) {
            a = 2;
            b = 0;
        } else if (mAB.matches()) {
            a = mAB.group(3) != null ? Integer.parseInt(mAB.group(1).replaceFirst("^\\+", "")) : 1;
            b = mAB.group(4) != null ? Integer.parseInt(mAB.group(4).replaceFirst("^\\+", "")) : 0;
        } else if (mB.matches()) {
            a = 0;
            b = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
        } else {
            throw new Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format", arg);
        }

        final Evaluator eval;
        if (ofType)
            if (backwards)
                eval = new Evaluator.IsNthLastOfType(a, b);
            else
                eval = new Evaluator.IsNthOfType(a, b);
        else {
            if (backwards)
                eval = (new Evaluator.IsNthLastChild(a, b));
            else
                eval = new Evaluator.IsNthChild(a, b);
        }
        return eval;
    }

    private String consumeParens() {
        return tq.chompBalanced('(', ')');
    }

    private int consumeIndex() {
        String index = consumeParens().trim();
        Validate.isTrue(StringUtil.isNumeric(index), "Index must be numeric");
        return Integer.parseInt(index);
    }

    // pseudo selector :has(el)
    private Evaluator has() {
        String subQuery = consumeParens();
        Validate.notEmpty(subQuery, ":has(selector) sub-select must not be empty");
        return new StructuralEvaluator.Has(parse(subQuery));
    }

    // pseudo selector :contains(text), containsOwn(text)
    private Evaluator contains(boolean own) {
        String query = own ? ":containsOwn" : ":contains";
        String searchText = TokenQueue.unescape(consumeParens());
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        return own
                ? new Evaluator.ContainsOwnText(searchText)
                : new Evaluator.ContainsText(searchText);
    }

    private Evaluator containsWholeText(boolean own) {
        String query = own ? ":containsWholeOwnText" : ":containsWholeText";
        String searchText = TokenQueue.unescape(consumeParens());
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        return own
                ? new Evaluator.ContainsWholeOwnText(searchText)
                : new Evaluator.ContainsWholeText(searchText);
    }

    // pseudo selector :containsData(data)
    private Evaluator containsData() {
        String searchText = TokenQueue.unescape(consumeParens());
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty");
        return new Evaluator.ContainsData(searchText);
    }

    // :matches(regex), matchesOwn(regex)
    private Evaluator matches(boolean own) {
        String query = own ? ":matchesOwn" : ":matches";
        String regex = consumeParens(); // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, query + "(regex) query must not be empty");

        return own
                ? new Evaluator.MatchesOwn(Pattern.compile(regex))
                : new Evaluator.Matches(Pattern.compile(regex));
    }

    // :matches(regex), matchesOwn(regex)
    private Evaluator matchesWholeText(boolean own) {
        String query = own ? ":matchesWholeOwnText" : ":matchesWholeText";
        String regex = consumeParens(); // don't unescape, as regex bits will be escaped
        Validate.notEmpty(regex, query + "(regex) query must not be empty");

        return own
                ? new Evaluator.MatchesWholeOwnText(Pattern.compile(regex))
                : new Evaluator.MatchesWholeText(Pattern.compile(regex));
    }

    // :not(selector)
    private Evaluator not() {
        String subQuery = consumeParens();
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");

        return new StructuralEvaluator.Not(parse(subQuery));
    }

    @Override
    public String toString() {
        return query;
    }
}

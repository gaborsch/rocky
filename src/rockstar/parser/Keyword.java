package rockstar.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gabor
 */
public enum Keyword {

    // keywords starting with "+" are Rocky extensions, ignored in strict mode
    // keywords starting with "-" are ignored in Rocky extended mode for that keyword    
    MYSTERIOUS("mysterious"),
    EMPTY_STRING("empty", "silent", "silence"),
    NULL("null", "nothing", "nowhere", "nobody", "gone"),
    EMPTY_ARRAY("void", "hollow"),
    BOOLEAN_TRUE("true", "right", "yes", "ok"),
    BOOLEAN_FALSE("false", "wrong", "no", "lies"),
    RESERVED("definitely", "maybe"),
    COMMON_VARIABLE_PREFIX("a", "an", "the", "my", "your", "our"),
    ON("+on", "+by", "+in", "+to", "+for", "+from", "+near"),
    AT("at"),
    NOT("not"),
    AND("and"),
    OR("or"),
    NOR("nor"),
    IS("is", "are", "was", "were"),
    ISNT("isnt", "arent", "aint", "wasnt", "werent"),
    THAN("than"),
    AS("as"),
    HIGHER("higher", "greater", "bigger", "stronger"),
    LOWER("lower", "less", "smaller", "weaker"),
    HIGH("high", "great", "big", "strong"),
    LOW("low", "little", "small", "weak"),
    WITH("with"),
    PLUS("plus", "+"),
    MINUS("minus", "without", "-"),
    TIMES("times", "of", "*"),
    OVER("over", "between", "/"),
    ROCK(),
    ROLL("roll", "pop"),
    INTO("into", "in"),
    FROM("+from"),
    TILL("+till"),
    TAKING("taking"),
    SORTED("+sorted"),
    COUNT("+count", "+length", "+height"),
    OF("+of"),
    LAST("+last"),
    ALL("+all"),
    KEYS("+keys"),
    VALUES("+values"),
    IT("it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver"),
    SELF("+self", "+myself", "+yourself", "+himself", "+herself", "+itself", "+ourselves", "+yourselves", "+themselves"),
    PARENT("+parent", "+father", "+mother", "+papa", "+mama"),
    _ANY_KEYWORD();

    private final List<String> strictKeywords = new LinkedList<>();
    private final List<String> extKeywords = new LinkedList<>();

    private Keyword(String... keywords) {
        if (name().startsWith("_")) {
            return;
        }
        for (String keyword : keywords) {
            int len = keyword.length();
            if (len > 1 && keyword.startsWith("+")) {
                extKeywords.add(keyword.substring(1));
            } else if (len > 1 && keyword.startsWith("-")) {
                strictKeywords.add(keyword.substring(1));
            } else {
                strictKeywords.add(keyword);
                extKeywords.add(keyword);
            }
        }
    }

    private static boolean strictMode = true;
    private static Set<String> allKeywordsStrict = null;
    private static Set<String> allKeywordsExt = null;

	public static void setStrictMode(boolean strictMode) {
		if (Keyword.strictMode != strictMode) {
			allKeywordsStrict = null;
			allKeywordsExt = null;
		}
		Keyword.strictMode = strictMode;
	}

    private static Set<String> getAllKeywords() {
        if (strictMode) {
            if (allKeywordsStrict == null) {
                allKeywordsStrict = new HashSet<>();
                for (Keyword kw : values()) {
                    allKeywordsStrict.addAll(kw.strictKeywords);
                }
            }
            return allKeywordsStrict;
        } else {
            if (allKeywordsExt == null) {
                allKeywordsExt = new HashSet<>();
                for (Keyword kw : values()) {
                    allKeywordsExt.addAll(kw.extKeywords);
                }
            }
            return allKeywordsExt;
        }
    }

    public List<String> getKeywordVariations() {
        return strictMode
                ? strictKeywords
                : extKeywords;
    }

    public boolean matches(String needle) {
        Collection<String> haystack = _ANY_KEYWORD.equals(this)
                ? getAllKeywords()
                : getKeywordVariations();
        String needleLC = needle.toLowerCase();
        return haystack.contains(needleLC);
    }
}

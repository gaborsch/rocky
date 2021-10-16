package rockstar.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 *
 * @author Gabor
 */
public enum Keyword {

    MYSTERIOUS(true, true, "mysterious"),
    EMPTY_STRING(true, true, "empty", "silent", "silence"),
    NULL(true, true, "null", "nothing", "nowhere", "nobody", "gone"),
    EMPTY_ARRAY(true, true, "void", "hollow"),
    BOOLEAN_TRUE(true, true, "true", "right", "yes", "ok"),
    BOOLEAN_FALSE(true, true, "false", "wrong", "no", "lies"),
    RESERVED(true, true, "definitely", "maybe"),
    COMMON_VARIABLE_PREFIX(true, true, "a", "an", "the", "my", "your"),
    ON(false, true, "on", "by", "in", "to", "for", "from", "near"),
    AT(true, true, "at"),
    NOT(true, true, "not"),
    AND(true, true, "and"),
    OR(true, true, "or"),
    NOR(true, true, "nor"),
    IS(true, true, "is", "are", "was", "were"),
    ISNT(true, true, "isnt", "arent", "aint", "wasnt", "werent"),
    THAN(false, false, "than"),
    AS(false, false, "as"),
    HIGHER(false, false, "higher", "greater", "bigger", "stronger"),
    LOWER(false, false, "lower", "less", "smaller", "weaker"),
    HIGH(false, false, "high", "great", "big", "strong"),
    LOW(false, false, "low", "little", "small", "weak"),
    WITH(true, true, "with"),
    PLUS(true, true, "plus", "+"),
    MINUS(true, true, "minus", "without", "-"),
    TIMES(true, true, "times", "of", "*"),
    OVER(true, true, "over", "/"),
    ROCK(false, false),
    ROLL(true, true, "roll", "pop"),
    INTO(true, true, "into"),
    FROM(false, true, "from"),
    TILL(false, true, "till"),
    TAKING(true, true, "taking"),
    SORTED(false, true, "sorted"),
    COUNT(false, true, "count", "length", "height"),
    OF(false, false, "of"),
    LAST(false, true, "last"),
    ALL(false, true, "all"),
    KEYS(false, true, "keys"),
    VALUES(false, true, "values"),
    _STARTER_KEYWORD(false, false),
    _ANY_KEYWORD(false, false);

    private final String[] keywordsVariations;
    private final boolean starter;
    private final boolean extStarter;

    private Keyword(boolean starter, boolean extStarter, String... keywords) {
        this.keywordsVariations = keywords;
        this.starter = starter;
        this.extStarter = extStarter;
    }

    public String[] getKeywordsVariations() {
        return keywordsVariations;
    }

    private boolean isStarter() {
        return starter;
    }

    private boolean isExtStarter() {
        return extStarter;
    }

    private static boolean strictMode = true;
    private static String[] allKeywords = null;
    private static String[] starterKeywords = null;
    private static String[] extStarterKeywords = null;

    public static void setStrictMode(boolean strictMode) {
        Keyword.strictMode = strictMode;
    }

    private static String[] getAllKeywords() {
        if (allKeywords == null) {
            allKeywords = getFilteredKeywords(kw -> true);
        }
        return allKeywords;
    }

    private static String[] getStarterKeywords() {
        if (starterKeywords == null) {
            starterKeywords = getFilteredKeywords(Keyword::isStarter);
        }
        return starterKeywords;
    }

    private static String[] getExtStarterKeywords() {
        if (extStarterKeywords == null) {
            extStarterKeywords = getFilteredKeywords(Keyword::isExtStarter);
        }
        return extStarterKeywords;
    }

    private static String[] getFilteredKeywords(Predicate<Keyword> filter) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (Keyword value : values()) {
            if (filter.test(value) && !value.name().startsWith("_")) {
                filteredList.addAll(Arrays.asList(value.keywordsVariations));
            }
        }
        return filteredList.toArray(new String[filteredList.size()]);
    }

    public boolean matches(String needle) {
        String[] haystack = _ANY_KEYWORD.equals(this)
                ? getAllKeywords()
                : _STARTER_KEYWORD.equals(this)
                ? (strictMode ? getStarterKeywords() : getExtStarterKeywords())
                : keywordsVariations;
        String needleLC = needle.toLowerCase();
        for (String s : haystack) {
            if (needleLC.equals(s)) {
                return true;
            }
        }
        return false;
    }
}

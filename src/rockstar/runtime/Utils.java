/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rockstar.parser.Token;

/**
 *
 * @author Gabor
 */
public class Utils {

    public static String UTF8 = "UTF-8";

    /**
     * Repeats a String 'count' times
     *
     * @param s
     * @param count
     * @return
     */
    public static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * compares two lists
     *
     * @param <T>
     * @param l1
     * @param l2
     * @return
     */
    public static <T> boolean isListEquals(List<T> l1, List<T> l2) {
        if (l1 == null && l2 == null) {
            return true;
        }
        if ((null == l1 || null == l2) || (l1.size() != l2.size())) {
            return false;
        }
        Iterator<T> it1 = l1.iterator();
        Iterator<T> it2 = l2.iterator();
        while (it1.hasNext()) {
            T v1 = it1.next();
            T v2 = it2.next();
            if (!v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares two maps (keys and values)
     *
     * @param <T>
     * @param <T2>
     * @param m1
     * @param m2
     * @return
     */
    public static <T, T2> boolean isMapEquals(Map<T, T2> m1, Map<T, T2> m2) {
        if (m1 == null && m2 == null) {
            return true;
        }
        if ((null == m1 || null == m2) || (m1.size() != m2.size())) {
            return false;
        }
        Iterator<T> it1 = m1.keySet().iterator();
        while (it1.hasNext()) {
            T key1 = it1.next();
            T2 v1 = m2.get(key1);
            T2 v2 = m2.get(key1);
            if (v2 == null || !v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * searches for an element in a list, returns its position if not found,
     * returns the list size
     *
     * @param <T>
     * @param list
     * @param v
     * @param startIdx
     * @return
     */
    public static <T> int findInList(List<T> list, T v, int startIdx) {
        for (int i = startIdx; i < list.size(); i++) {
            T t = list.get(i);
            if (t.equals(v)) {
                return i;
            }
        }
        return list.size();
    }

    /**
     * searches for an element in a list, returns its position if not found,
     * returns the list size
     *
     * @param <T>
     * @param list
     * @param v
     * @param startIdx
     * @return
     */
    public static int findInList(List<Token> list, String v, int startIdx) {
        for (int i = startIdx; i < list.size(); i++) {
        	Token t = list.get(i);
            if (t.getValue().equals(v)) {
                return i;
            }
        }
        return list.size();
    }    

}

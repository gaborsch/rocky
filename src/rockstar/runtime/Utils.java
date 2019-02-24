/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.runtime;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gabor
 */
public class Utils {

    public static String UTF8 = "UTF-8";

    public static String repeat(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

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

}

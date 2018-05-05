package com.example.sandwraith8.gr_final.common;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by sandwraith8 on 08/04/2018.
 */

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    public static String removeAccent(String s) {

        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}

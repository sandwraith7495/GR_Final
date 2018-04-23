package com.example.sandwraith8.gr_final.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sandwraith8 on 08/04/2018.
 */

public class ContactExtractor {
    private static final ContactExtractor ourInstance = new ContactExtractor();

    public static ContactExtractor getInstance() {
        return ourInstance;
    }

    private ContactExtractor() {
    }


    public String extractName(String[] data) {
        for (String str : data) {
            final String NAME_REGEX = "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
            Pattern p = Pattern.compile(NAME_REGEX, Pattern.MULTILINE);
            Matcher m = p.matcher(str);
            if (m.find()) {
                System.out.println(m.group());
                return m.group();
            }
        }
        return null;
    }

    public String extractEmail(String[] data) {
        for (String str : data) {
            final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
            Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
            Matcher m = p.matcher(str);   // get a matcher object
            if (m.find()) {
                System.out.println(m.group());
                return m.group();
            }
        }
        return null;
    }

    public List<String> extractPhone(String[] data) {
        List<String> phones = new ArrayList<>();
        for (String str : data) {
            final String PHONE_REGEX = "[\\d\\s\\-\\+]{9,18}";
            Pattern p = Pattern.compile(PHONE_REGEX, Pattern.MULTILINE);
            Matcher m = p.matcher(str);   // get a matcher object
            if (m.find()) {
                phones.add(m.group());
            }
        }
        return phones;
    }



}

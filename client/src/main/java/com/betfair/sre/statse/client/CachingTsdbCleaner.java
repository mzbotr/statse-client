/*
Copyright 2013, The Sporting Exchange Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.betfair.sre.statse.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: mcintyret2
 * Date: 14/08/2013
 *
 * <p>The original implementation (in Mantis Performance) did this very simply using Google Guava's CharMatcher.</p>
 *
 * <p>This implementation does it manually to avoid importing that library, especially given known issues in Cougar.</p>
 */
public class CachingTsdbCleaner implements TsdbCleaner {

    private final Map<String, String> sanitised = new ConcurrentHashMap<String, String>();

    private static boolean isValidChar(char c) {
        if (Character.isLetterOrDigit(c)) {
            return true;
        } else {
            switch (c) {
                case '-':
                case '_':
                case '.':
                case '/':
                    return true;
                default:
                    return false;
            }
        }
    }

    private String doClean(String input) {
        char[] chars = new char[input.length()];
        int i = 0;
        for (int c = 0; c < input.length(); c++) {
            char ch = input.charAt(c);
            if (isValidChar(ch)) {
                chars[i++] = ch;
            }
        }
        return new String(chars, 0, i);
    }

    public String clean(String value) {
        if (value != null) {
            String result = sanitised.get(value);
            if (result != null) {
                return result;
            }
            result = doClean(value);
            sanitised.put(value, result);
            return result;
        }
        return null;
    }

}

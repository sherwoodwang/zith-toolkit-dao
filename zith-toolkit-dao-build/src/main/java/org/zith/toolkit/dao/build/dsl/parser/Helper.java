package org.zith.toolkit.dao.build.dsl.parser;

import javax.lang.model.SourceVersion;

class Helper {
    static boolean isJavaName(String symbol) {
        return SourceVersion.isName(symbol);
    }

    static String decodeString(String longSymbol) {
        if (!(longSymbol.startsWith("\"") && longSymbol.endsWith("\""))) {
            throw new IllegalArgumentException();
        }

        StringBuilder result = new StringBuilder();

        boolean e = false;
        for (int i = 1; i < longSymbol.length() - 1; i++) {
            char c = longSymbol.charAt(i);

            if (e) {
                switch (c) {
                    case '\\':
                    case '"':
                        result.append(c);
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 'n':
                        result.append('\n');
                        break;
                }
                e = false;
            } else {
                if (c == '\\') {
                    e = true;
                } else {
                    result.append(c);
                }
            }
        }

        if (e) {
            throw new IllegalArgumentException();
        }

        return result.toString();
    }
}

package com.aile.tex;

import java.text.DecimalFormat;

/**
 * Created by JW on 16. 5. 15..
 */
public class Util {
    // 천단위 콤마 처리
    public static String makeStringComma(String string) {
        if (string.length() == 0) {
            return "";
        }

        // 원래는 파라미터로 받는 부분인데 default를 true로 놔둠
        boolean ignoreZero = true;

        try {
            // 소수점 있는 경우
            if (string.indexOf(".") >= 0) {
                double value = Double.parseDouble(string);
                if (ignoreZero && value == 0) {
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,##0.00");
                return format.format(value);
            }
            // 소수점이 없는 경우
            else {
                long value = Long.parseLong(string);
                if (ignoreZero && value == 0) {
                    return "";
                }
                DecimalFormat format = new DecimalFormat("###,###");
                return format.format(value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }
}

package com.xindany.util;

import android.text.TextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.net.Uri.decode;

/**
 * string工具类
 */

public class StringUtils {


    public static String toURLDecoded(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return "";
        }
        if (paramString.contains("\\u")) {
            return decode(paramString);
        }
        try {
            paramString = paramString.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            return URLDecoder.decode(paramString, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


        static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿",
                "十亿", "百亿", "千亿", "万亿" };
        static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };

    public static String foematInteger(int num) {
            char[] val = String.valueOf(num).toCharArray();
            int len = val.length;
            System.out.println("----" + len);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                String m = val[i] + "";
                int n = Integer.valueOf(m);
                boolean isZero = n == 0;
                String unit = units[(len - 1) - i];
                if (isZero) {
                    if ('0' == val[i - 1]) {
                        //当前val[i]的下一个值val[i-1]为0则不输出零
                        continue;
                    } else {
                        //只有当当前val[i]的下一个值val[i-1]不为0才输出零
                        sb.append(numArray[n]);
                    }
                } else {
                    sb.append(numArray[n]);
                    sb.append(unit);
                }
            }
            return sb.toString();
        }

    private static final SimpleDateFormat PLAIN_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    private StringUtils() {
    }

    /**
     * 转换为字符串,如果小数部分为0则去掉
     *
     * @param f
     * @return
     */
    public static String trimFloat(float f) {
        int i = (int) f;
        if (i == f) {
            return i + "";
        } else {
            return f + "";
        }
    }

    public static boolean toBoolean(String str, boolean fallback) {
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static boolean toBoolean(String str) {
        return toBoolean(str, false);
    }

    public static double toDouble(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0);
    }

    /**
     * 将表示整数的字符串转化为int型，如果出现格式错误，返回 {@code fallback}
     *
     * @param str
     * @param radix
     * @param fallback
     * @return
     */
    public static int toInt(String str, int radix, int fallback) {
        try {
            return Integer.parseInt(str, radix);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static boolean judgeContainsChar(String cardNum) {
        String regex = ".*[a-zA-Z].*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }



    /**
     * 将表示十进制整数字符串转化为int型，如果出现格式错误，返回 {@code fallback}
     *
     * @param str
     * @param fallback
     * @return
     */
    public static int toInt(String str, int fallback) {
        return toInt(str, 10, fallback);
    }

    /**
     * 将表示十进制整数字符串转化为int型，如果出现格式错误，返回 0
     *
     * @param str
     * @return
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }


    private static final SimpleDateFormat COMPACT_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    /**
     * 返回以紧凑格式（i.e. yyyyMMddHHmmss）表示的当前系统时间, e.g. 20161221130420
     *
     * @return
     */
    public static String getCompactTimeString() {
        return COMPACT_DATE_FORMAT.format(new Date());
    }

    /**
     * 将一个以紧凑格式（i.e. yyyyMMddHHmmss）表示时间的字符串转化为Unix时间戳
     *
     * @param str
     * @return
     */
    public static long parseCompactTimeString(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return COMPACT_DATE_FORMAT.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static final SimpleDateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat CLOCK_DATE_FORMAT_SHORT = new SimpleDateFormat("mm:ss", Locale.getDefault());

    static {
        CLOCK_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private static ThreadLocal<Date> sTmpDate = new ThreadLocal<Date>() {
        @Override
        protected Date initialValue() {
            return new Date();
        }
    };

    /**
     * 返回以计时格式（i.e. HH:mm:ss）表示的给定时间
     *
     * @return
     */
    public static String getClockTimeString(int seconds) {
        Date date = sTmpDate.get();
        date.setTime(seconds * 1000);
        if (seconds < 60 * 60) {
            return CLOCK_DATE_FORMAT_SHORT.format(date);
        } else {
            return CLOCK_DATE_FORMAT.format(date);
        }
    }


    private static final SimpleDateFormat COMPLETE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final SimpleDateFormat COMPLETE_DATE_SSS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);

    /**
     * 返回当前系统时间的完整表示（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getCompleteTimeString() {
        return getCompleteTimeString(System.currentTimeMillis());
    }


    public static String getCompleteTimeString(long millis) {
        Date date = sTmpDate.get();
        date.setTime(millis);
        return COMPLETE_DATE_FORMAT.format(date);
    }

    public static String getCompleteDateTimeString() {
        return getCompleteDateTimeString(System.currentTimeMillis());
    }

    public static String getCompleteDateTimeString(long millis) {
        Date date = sTmpDate.get();
        date.setTime(millis);
        return COMPLETE_DATE_SSS_FORMAT.format(date);
    }

    /**
     * 将时间的完整表示（yyyy-MM-dd HH:mm:ss）转换为Unix时间戳. 若参数为null或格式错误，返回0
     *
     * @param str
     * @return
     */
    public static long parseCompleteTimeString(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return COMPLETE_DATE_FORMAT.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * check if the given string is null or ""
     *
     * @param str
     * @return true if str is null or ""
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * stronger than {@link #isEmpty(String)}, in that it will return true if str.trim is ""
     *
     * @param str
     * @return true if str is null, or str.trim is ""
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * return a String denote the current time in a form like 20161221130420
     *
     * @return
     */
    public static String getPlainTimeString() {
        return PLAIN_DATE_FORMAT.format(new Date());
    }

    /**
     * 将当前 日期转换为 格式 yyyyMMdd-HH:mm:ss
     *
     * @return
     */
    public static String parseDateAccurateToSecond() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.CHINA);

        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        String yyyyMMddString = yyyyMMdd.format(date);

        return yyyyMMddString;
    }


    /**
     * 获取当前的 月份 0-11，所以要加1
     *
     * @return
     */
    public static int parseMonthString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 将当前 天  的日期转换为 数字格式 yyyyMMdd
     *
     * @return
     */
    public static int parseDateString() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        String yyyyMMddString = yyyyMMdd.format(date);

        return toInt(yyyyMMddString);
    }

    /**
     * 获取当前的 小时
     *
     * @return
     */
    public static int parseHourString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前的 总分钟 ／当天
     *
     * @return
     */
    public static int parseMinuteString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.MINUTE);

        return hour * 60 + minute;
    }

    /**
     * 根据 '时间'字符串  获取当前的 总分钟 ／当天
     *
     * @return
     */
    public static int parseMinuteString(String HHmm) {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("HH:mm");
        if (!TextUtils.isEmpty(HHmm)) {
            try {
                Date date = yyyyMMdd.parse(HHmm);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.MINUTE);

                return hour * 60 + minute;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return -1;
    }


    /**
     * 获取今天是周几
     *
     * @return
     */
    public static int parsWeekDayString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 8;
        }
        return week - 1;
    }

    /**
     * ISO8601 UTC 时间
     *
     * @param date
     * @return
     */
    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    /**
     * 获取字符串随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * 获取异常的栈跟踪信息
     *
     * @param e 异常
     * @return
     */
    public static String getStackTraceFromException(Exception e) {
        String exceptionStack = null;
        if (e != null) {
            try (StringWriter writer = new StringWriter()
                 ; PrintWriter stringWriter = new PrintWriter(writer)) {

                e.printStackTrace(stringWriter);
                exceptionStack = writer.toString();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return exceptionStack;
    }

    public static String toString(Exception e) {
        return e == null ? "" : e.toString();
    }

    public static final char[] CHINESE_DIGITS_LOWER_CASE = {'〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static int sizeOf(String str) {
        return str == null ? 0 : (36 + str.length() * 2);
    }

    public static int string2int(String str) {
        return string2int(str, 0);
    }

    public static int string2int(String str, int def) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
        }
        return def;
    }
}

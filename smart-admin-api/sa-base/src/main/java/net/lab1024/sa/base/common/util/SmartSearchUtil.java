package net.lab1024.sa.base.common.util;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.sourceforge.pinyin4j.PinyinHelper;

public class SmartSearchUtil {

    public static Set<String> search(String key, Set<String> dataList) {
        Set<String> result = new HashSet<>();

        // 如果关键词包含中文，直接返回匹配列表
        if (containsChinese(key)) {
            for (String item : dataList) {
                if (item.contains(key)) {
                    result.add(item);
                }
            }
            return result;
        }

        // 如果关键词不含任何中文，看是否完全由英文字母组成（假定这代表有可能是拼音）
        if (Pattern.compile("[A-Za-z]").matcher(key).find()) {
            // 匹配流程，将候选集中包含中文的元素处理成首字母的拼音进行匹配
            for (String item : dataList) {
                if (containsChinese(item)) { // 如果元素包含中文
                    String convertedItem = firstLetter(item); // 转换为首字母拼音
                    // 进行匹配
                    if (convertedItem.contains(key)) {
                        result.add(item);
                    }
                } else {
                    // 如果候选元素不含中文，直接匹配
                    if (item.contains(key)) {
                        result.add(item);
                    }
                }
            }
        } else {
            for (String item : dataList) {
                if (item.contains(key)) {
                    result.add(item);
                }
            }
            return result;
        }
        return result;
    }

    // 判断一个字符串是否包含中文
    public static boolean containsChinese(String str) {
        return str != null && Pattern.compile("[\u4E00-\u9FA5]").matcher(str).find();
    }

    // 将中文字符串转换为首字母拼音（示例方法，可以用具体的库进行实现）
    public static String firstLetter(String chineseStr) {
        if (chineseStr == null || chineseStr.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char c : chineseStr.toCharArray()) {
            // 获取每个汉字的拼音
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
            if (pinyinArray != null && pinyinArray.length > 0) {
                // 取拼音的首字母并转换为大写
                result.append(Character.toLowerCase(pinyinArray[0].charAt(0)));
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Set<String> dataList = new HashSet<>();
//            '杭州', 'hz11', '33hz', 'hxz', 'hz', '浙江杭州', '杭州11', '11-11'
        dataList.add("杭州");
        dataList.add("hz11");
        dataList.add("33hz");
        dataList.add("hxz");
        dataList.add("hz");
        dataList.add("浙江杭州");
        dataList.add("杭州11");
        dataList.add("11-11");

//            # keywords = ['杭州', 'hz', 'hz11', '11', '-', '杭', 'hangzhou']
        Set<String> result = search("11", dataList);
        for (String item : result) {
            System.out.println(item);
        }
    }

}


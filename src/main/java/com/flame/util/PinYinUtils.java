package com.flame.util;

import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;

public class PinYinUtils {
	public static String getFirstLetter(String chinese) {
		if (StringUtil.isBlank(chinese))
			return "";

		StringBuilder builder = new StringBuilder();
		String pinyin = PinyinHelper.toPinyin(chinese.toUpperCase(), PinyinStyleEnum.NORMAL);
		for (String str : pinyin.split(" ")) {
			char first = str.charAt(0);
			if (Character.isLowerCase(first)) {
				builder.append(first);
			} else {
				builder.append(str);
			}
		}

		return builder.toString();
	}

	public static String getLetter(String chinese) {
		return PinyinHelper.toPinyin(chinese, PinyinStyleEnum.NORMAL);
	}

	public static String getPinYin(String chinese) {
		return PinyinHelper.toPinyin(chinese);
	}

	public static void main(String[] args) {
		System.out.println(getPinYin("我是一个aaaa并答复的"));
	}
}

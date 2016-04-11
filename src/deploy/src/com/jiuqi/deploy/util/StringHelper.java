package com.jiuqi.deploy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiuqi.deploy.server.Contants;

public class StringHelper {

	public static boolean isEmpty(String text) {
		return null == text || "".equals(text.trim());
	}

	public static boolean equals(String text1, String text2) {
		return null != text1 && text1.equals(text2);
	}

	public static List<String> getUrlList(String nodesUrlStr) {
		List<String> nodesUrl = new ArrayList<String>();
		if (null != nodesUrlStr && !"".equals(nodesUrlStr)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(nodesUrlStr);
			nodesUrlStr = m.replaceAll("");
			String[] subUrls = nodesUrlStr.split("http");
			for (String subUrl : subUrls) {
				if (!"".equals(subUrl.trim())) {
					nodesUrl.add("http" + subUrl);
				}
			}
		}
		return nodesUrl;
	}

	public static String getEnableStr(boolean enable) {
		return enable ? Contants.ATTRIBUTE_ENABLE : "";
	}
}

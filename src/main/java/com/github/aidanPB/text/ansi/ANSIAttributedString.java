package com.github.aidanPB.text.ansi;

import java.text.AttributedString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ANSIAttributedString extends AttributedString {

	private static Pattern ansipat = Pattern.compile("\\e\\[([^\\p{Alnum}]+)?(?:(\\d{1,3})((?:;\\d{1,3})*))?([^a-zA-Z]+?)?\\p{Alpha}");

	private ANSIAttributedString(String str){
		super(str);
	}

	public static ANSIAttributedString fromString(String source){
		Matcher ansimat = ansipat.matcher(source);
		StringBuffer sbuf = new StringBuffer();
		int curpos = 0;
		int endOfLast = 0;
		while(ansimat.find()){
			curpos += ansimat.start() - endOfLast;
			endOfLast = ansimat.end();
			//TODO Catalogue the matched ansi code.
			ansimat.appendReplacement(sbuf, null);
		}
		ansimat.appendTail(sbuf);
		ANSIAttributedString ansistr = new ANSIAttributedString(sbuf.toString());
		//TODO apply the attributes derived from the ANSI codes.
		return ansistr;
	}
}

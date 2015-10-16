package com.github.aidanPB.text.ansi;

public enum TextModifier {

	RESET(0), BOLD(1), FAINT(2), ITALIC(3), UNDERLINE(4), BLINK_SLOW(5), BLINK_FAST(6),
	REVERSE(7), STRIKEOUT(9), FRAKTUR(20), UNBOLD(22), UNFRATALIC(23), DEUNDERLINE(24),
	UNBLINKING(25), UNINVERSE(27), UNCROSSED(29), SET_FG_BLACK(30), SET_FG_RED(31),
	SET_FG_GREEN(32), SET_FG_YELLOW(33), SET_FG_BLUE(34), SET_FG_MAGENTA(35),
	SET_FG_CYAN(36), SET_FG_WHITE(37), SET_FG_EXTENDED(38), SET_FG_DEFAULT(39),
	SET_BG_BLACK(40), SET_BG_RED(41), SET_BG_GREEN(42),	SET_BG_YELLOW(43),
	SET_BG_BLUE(44), SET_BG_MAGENTA(45), SET_BG_CYAN(46), SET_BG_WHITE(47),
	SET_BG_EXTENDED(48), SET_BG_DEFAULT(49), FRAMED(51), CIRCLED(52), OVERLINE(53),
	NOSURROUND(54), UNOVERLINE(55);

	private int codeNumber;

	private TextModifier(int codeNumber){
		this.codeNumber = codeNumber;
	}

	public int getCodeNumber(){
		return codeNumber;
	}

	public String toANSIEscape(int... escargs){
		StringBuilder sb = new StringBuilder();
		sb.append("\033[").append(codeNumber);
		for(int arg : escargs){
			sb.append(';').append(arg);
		}
		return sb.append('m').toString();
	}
}

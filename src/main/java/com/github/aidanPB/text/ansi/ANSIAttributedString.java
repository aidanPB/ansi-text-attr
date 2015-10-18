package com.github.aidanPB.text.ansi;

import java.text.AttributedString;
import java.text.CharacterIterator;
import java.awt.Color;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.ImmutableTriple;

public class ANSIAttributedString extends AttributedString {

	public static class ANSIAttribute extends AttributedCharacterIterator.Attribute{

		/**
		 * The unique ID number for this version of this class.
		 */
		private static final long serialVersionUID = 514136063403470694L;

		public enum AttributeKind{
			STRENGTH,BLINK,FONT,LETTER,UNDERLN,OVERLN,FOREGROUND,BACKGROUND,STRIKE,REVERSE,SURROUND
		}

		private final AttributeKind kind;

		protected ANSIAttribute(String name, AttributeKind kind){
			super(name);
			this.kind = kind;
		}

		public AttributeKind getKind(){
			return kind;
		}
	}

	/**
	 * Attribute key for the "strength" of a text. Values should be constants of
	 * the ANSIAttributedString.ANSIWeight enum type. The default value is NORMAL.
	 */
	public static final ANSIAttribute ANSI_STRENGTH = new ANSIAttribute("ANSI_STRENGTH", ANSIAttribute.AttributeKind.STRENGTH);
	public enum ANSIWeight{
		BOLD,NORMAL,FAINT
	}

	/**
	 * Attribute key for the blink rate of a text. Values should be constants of
	 * the ANSIAttributedString.ANSIBlinkRate enum type. The default value is STEADY.
	 */
	public static final ANSIAttribute ANSI_BLINK = new ANSIAttribute("ANSI_BLINK_RATE", ANSIAttribute.AttributeKind.BLINK);
	public enum ANSIBlinkRate{
		STEADY,SLOW,FAST
	}

	/**
	 * Attribute key for the font number of a text. Some terminals can handle multiple
	 * fonts, and this attribute selects which one by number. Values should be
	 * instances of Byte, valued in the range 0-9, inclusive. Default value is 0.
	 */
	public static final ANSIAttribute ANSI_FONT = new ANSIAttribute("ANSI_FONT_NUMBER", ANSIAttribute.AttributeKind.FONT);

	/**
	 * Attribute key for the letterform of a text. Some terminals can handle italic
	 * and/or (more rarely) Fraktur letterforms. Values should be constants of the
	 * ANSIAttributedString.ANSILetterform enum type. The default value is NORMAL.
	 */
	public static final ANSIAttribute ANSI_LETTER = new ANSIAttribute("ANSI_LETTERFORM", ANSIAttribute.AttributeKind.LETTER);
	public enum ANSILetterform{
		NORMAL,ITALIC,FRAKTUR
	}

	/**
	 * Attribute key for the underline status of a text. Values should be instances
	 * of Boolean. The default value is False.
	 */
	public static final ANSIAttribute ANSI_ULINE = new ANSIAttribute("ANSI_UNDERLINE", ANSIAttribute.AttributeKind.UNDERLN);

	/**
	 * Attribute key for the overline status of a text. Values should be instances
	 * of Boolean. The default value is False.
	 */
	public static final ANSIAttribute ANSI_OVERLN = new ANSIAttribute("ANSI_OVERLINE", ANSIAttribute.AttributeKind.OVERLN);

	/**
	 * Attribute key for the foreground (text) colour of a text. Values should be
	 * instances of java.awt.Color. the default value is null, which represents the
	 * terminal-dependent default value.
	 */
	public static final ANSIAttribute ANSI_FG_COL = new ANSIAttribute("FOREGROUND_COLOR", ANSIAttribute.AttributeKind.FOREGROUND);

	/**
	 * Attribute key for the background colour of a text. Values should be instances
	 * of java.awt.Color. The default value is null, which represents the
	 * terminal-dependent default value.
	 */
	public static final ANSIAttribute ANSI_BG_COL = new ANSIAttribute("BACKGROUND_COLOR", ANSIAttribute.AttributeKind.BACKGROUND);

	/**
	 * Attribute key for the strike-out status of a text. Values should be instances
	 * of Boolean. The default value is False.
	 */
	public static final ANSIAttribute ANSI_STRIKE = new ANSIAttribute("STRIKE_OUT", ANSIAttribute.AttributeKind.STRIKE);

	/**
	 * Attribute key for the reverse video status of a text. Values should be
	 * instances of Boolean; when true, foregroud and background colours are swapped.
	 * Default value is false.
	 */
	public static final ANSIAttribute ANSI_REVERSE = new ANSIAttribute("REVERSE_VIDEO", ANSIAttribute.AttributeKind.REVERSE);

	/**
	 * Attribute key for the surroundedness of a text. Some terminals can surround
	 * text with rectangular or round shapes. Values should be constants of the
	 * ANSIAttributedString.ANSISurround enum type. The default value is NONE.
	 */
	public static final ANSIAttribute ANSI_SURROUND = new ANSIAttribute("ANSI_SURROUND", ANSIAttribute.AttributeKind.SURROUND);
	public enum ANSISurround{
		NONE,FRAMED,ENCIRCLED
	}

	private static final Pattern ansipat = Pattern.compile("\\e\\[(?:(\\d{1,3})((?:;\\d{1,3})*))?([^a-zA-Z]+?)?m");

	public ANSIAttributedString(AttributedCharacterIterator text){
		super(text);
	}

	public ANSIAttributedString(AttributedCharacterIterator text, int beginIdx, int endIdx){
		super(text, beginIdx, endIdx);
	}

	private ANSIAttributedString(String str){
		super(str);
	}

	public static ANSIAttributedString fromString(String source){
		Matcher ansimat = ansipat.matcher(source);
		StringBuffer sbuf = new StringBuffer();
		ArrayList<ImmutableTriple<Integer, ANSIAttribute, ?>> ansicodes = new ArrayList<ImmutableTriple<Integer,ANSIAttribute,?>>();
		int curpos = 0;
		int endOfLast = 0;
		while(ansimat.find()){
			curpos += ansimat.start() - endOfLast;
			endOfLast = ansimat.end();
			String code = ansimat.group(1);
			if(code == null) code = "0";
			int sgr = Integer.parseInt(code);
			switch(sgr){
			case 0:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRENGTH, ANSIWeight.NORMAL));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BLINK, ANSIBlinkRate.STEADY));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)0)));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_LETTER, ANSILetterform.NORMAL));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_ULINE, false));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_OVERLN, false));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRIKE, false));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_REVERSE, false));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color) null));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color) null));
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_SURROUND, ANSISurround.NONE));
				break;
			case 1:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRENGTH, ANSIWeight.BOLD));
				break;
			case 2:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRENGTH, ANSIWeight.FAINT));
				break;
			case 3:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_LETTER, ANSILetterform.ITALIC));
				break;
			case 4:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_ULINE, true));
				break;
			case 5:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BLINK, ANSIBlinkRate.SLOW));
				break;
			case 6:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BLINK, ANSIBlinkRate.FAST));
				break;
			case 7:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_REVERSE, true));
				break;
			case 8:
				//Conceal seems like it'd be even rarer than Fraktur. Skip over it for now.
				break;
			case 9:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRIKE, false));
				break;
			case 10:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)0)));
				break;
			case 11:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)1)));
				break;
			case 12:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)2)));
				break;
			case 13:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)3)));
				break;
			case 14:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)4)));
				break;
			case 15:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)5)));
				break;
			case 16:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)6)));
				break;
			case 17:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)7)));
				break;
			case 18:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)8)));
				break;
			case 19:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FONT, Byte.valueOf((byte)9)));
				break;
			case 20:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_LETTER, ANSILetterform.ITALIC));
				break;
			case 21:
				//21 is ambiguous, so I ignore it.
				break;
			case 22:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRENGTH, ANSIWeight.NORMAL));
				break;
			case 23:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_LETTER, ANSILetterform.NORMAL));
				break;
			case 24:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_ULINE, false));
				break;
			case 25:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BLINK, ANSIBlinkRate.STEADY));
				break;
			case 26:
				//Noted as "Reserved"; ignore it.
				continue;
			case 27:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_REVERSE, false));
				break;
			case 28:
				//This turns off a feature that I'm ignoring, so it should be ignored.
				break;
			case 29:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_STRIKE, false));
				break;
			case 30:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLACK));
				break;
			case 31:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.RED.darker()));
				break;
			case 32:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.GREEN.darker()));
				break;
			case 33:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.YELLOW.darker()));
				break;
			case 34:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLUE.darker()));
				break;
			case 35:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.MAGENTA.darker()));
				break;
			case 36:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.CYAN.darker()));
				break;
			case 37:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.LIGHT_GRAY));
				break;
			case 38:
				if(ansimat.group(2) == null || ansimat.group(2).equals("")){
					ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color)null));
					break;
				}
				String forecolor = ansimat.group(2).substring(1);
				String[] forevals = forecolor.split(";");
				if(forevals[0].equals("5")){
					if(forevals.length == 1){
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color)null));
						break;
					}
					int bytecolour = Integer.parseInt(forevals[1]);
					switch(bytecolour){
					case 0:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLACK));
						break;
					case 1:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.RED.darker()));
						break;
					case 2:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.GREEN.darker()));
						break;
					case 3:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.YELLOW.darker()));
						break;
					case 4:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLUE.darker()));
						break;
					case 5:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.MAGENTA.darker()));
						break;
					case 6:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.CYAN.darker()));
						break;
					case 7:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.LIGHT_GRAY));
						break;
					case 8:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLACK));
						break;
					case 9:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.RED));
						break;
					case 0xA:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.GREEN));
						break;
					case 0xB:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.YELLOW));
						break;
					case 0xC:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLUE));
						break;
					case 0xD:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.MAGENTA));
						break;
					case 0xE:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.CYAN));
						break;
					case 0xF:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.WHITE));
						break;
					default:
						if(bytecolour > 0xE7){
							float greypoint = (bytecolour - 0xE8) / 24f;
							ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.getHSBColor(0, 0, greypoint)));
							break;
						}
						bytecolour -= 0x10;
						int rbits = bytecolour / 36;
						rbits = (rbits * 128) / 3;
						bytecolour %= 36;
						int gbits = bytecolour / 6;
						gbits = (gbits * 128) / 3;
						int bbits = bytecolour % 6;
						bbits = (bbits * 128) / 3;
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, new Color(rbits, gbits, bbits)));
						break;
					}
					break;
				}
				if(forevals[0].equals("2")){
					if(forevals.length < 4){
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color)null));
						break;
					}
					int rbits = Integer.parseInt(forevals[1]);
					if(rbits > 999){
						rbits = 255;
					}else if(rbits > 255){
						float rfloat = rbits / 1000f;
						rbits = (int)(rfloat * 256);
					}
					int gbits = Integer.parseInt(forevals[2]);
					if(gbits > 999){
						gbits = 255;
					}else if(gbits > 255){
						float gflt = gbits / 1000f;
						gbits = (int)(gflt * 256);
					}
					int bbits = Integer.parseInt(forevals[3]);
					if(bbits > 999){
						bbits = 255;
					}else if(bbits > 255){
						float bfloat = bbits / 1000f;
						bbits = (int)(bfloat * 256);
					}
					ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, new Color(rbits, gbits, bbits)));
					break;
				}
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color)null));
				break;
			case 39:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, (Color)null));
				break;
			case 40:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLACK));
				break;
			case 41:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.RED.darker().darker()));
				break;
			case 42:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.GREEN.darker().darker()));
				break;
			case 43:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.YELLOW.darker().darker()));
				break;
			case 44:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLUE.darker().darker()));
				break;
			case 45:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.MAGENTA.darker().darker()));
				break;
			case 46:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.CYAN.darker().darker()));
				break;
			case 47:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.GRAY));
				break;
			case 48:
				if(ansimat.group(2) == null || ansimat.group(2).equals("")){
					ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color)null));
					break;
				}
				String backcolor = ansimat.group(2).substring(1);
				String[] backvals = backcolor.split(";");
				if(backvals[0].equals("5")){
					if(backvals.length == 1){
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color)null));
						break;
					}
					int bytecolour = Integer.parseInt(backvals[1]);
					switch(bytecolour){
					case 0:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLACK));
						break;
					case 1:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.RED.darker()));
						break;
					case 2:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.GREEN.darker()));
						break;
					case 3:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.YELLOW.darker()));
						break;
					case 4:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLUE.darker()));
						break;
					case 5:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.MAGENTA.darker()));
						break;
					case 6:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.CYAN.darker()));
						break;
					case 7:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.LIGHT_GRAY));
						break;
					case 8:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLACK));
						break;
					case 9:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.RED));
						break;
					case 0xA:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.GREEN));
						break;
					case 0xB:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.YELLOW));
						break;
					case 0xC:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.BLUE));
						break;
					case 0xD:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.MAGENTA));
						break;
					case 0xE:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.CYAN));
						break;
					case 0xF:
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.WHITE));
						break;
					default:
						if(bytecolour > 0xE7){
							float greypoint = (bytecolour - 0xE8) / 24f;
							ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, Color.getHSBColor(0, 0, greypoint)));
							break;
						}
						bytecolour -= 0x10;
						int rbits = bytecolour / 36;
						rbits = (rbits * 128) / 3;
						bytecolour %= 36;
						int gbits = bytecolour / 6;
						gbits = (gbits * 128) / 3;
						int bbits = bytecolour % 6;
						bbits = (bbits * 128) / 3;
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, new Color(rbits, gbits, bbits)));
						break;
					}
					break;
				}
				if(backvals[0].equals("2")){
					if(backvals.length < 4){
						ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color)null));
						break;
					}
					int rbits = Integer.parseInt(backvals[1]);
					if(rbits > 999){
						rbits = 255;
					}else if(rbits > 255){
						float rfloat = rbits / 1000f;
						rbits = (int)(rfloat * 256);
					}
					int gbits = Integer.parseInt(backvals[2]);
					if(gbits > 999){
						gbits = 255;
					}else if(gbits > 255){
						float gflt = gbits / 1000f;
						gbits = (int)(gflt * 256);
					}
					int bbits = Integer.parseInt(backvals[3]);
					if(bbits > 999){
						bbits = 255;
					}else if(bbits > 255){
						float bfloat = bbits / 1000f;
						bbits = (int)(bfloat * 256);
					}
					ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, new Color(rbits, gbits, bbits)));
					break;
				}
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color)null));
				break;
			case 49:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_BG_COL, (Color)null));
				break;
			case 50:
				//Reserved and ignored.
				break;
			case 51:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_SURROUND, ANSISurround.FRAMED));
				break;
			case 52:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_SURROUND, ANSISurround.ENCIRCLED));
				break;
			case 53:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_OVERLN, true));
				break;
			case 54:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_SURROUND, ANSISurround.NONE));
				break;
			case 55:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_OVERLN, false));
				break;
			case 90:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLACK));
				break;
			case 91:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.RED));
				break;
			case 92:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.GREEN));
				break;
			case 93:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.YELLOW));
				break;
			case 94:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLUE));
				break;
			case 95:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.MAGENTA));
				break;
			case 96:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.CYAN));
				break;
			case 97:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.WHITE));
				break;
			case 100:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLACK));
				break;
			case 101:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.RED));
				break;
			case 102:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.GREEN));
				break;
			case 103:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.YELLOW));
				break;
			case 104:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.BLUE));
				break;
			case 105:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.MAGENTA));
				break;
			case 106:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.CYAN));
				break;
			case 107:
				ansicodes.add(ImmutableTriple.of(curpos, ANSI_FG_COL, Color.WHITE));
				break;
			default:
				//ignore anything else for now.
				break;
			}
			ansimat.appendReplacement(sbuf, "");
		}
		ansimat.appendTail(sbuf);
		ANSIAttributedString ansistr = new ANSIAttributedString(sbuf.toString());
		for(int i = 0;i < ansicodes.size();++i){
			ImmutableTriple<Integer, ANSIAttribute, ?> triple = ansicodes.get(i);
			if(triple.getLeft() == sbuf.length()) break;
			int attribend = triple.getLeft() - 1;
			for(int j = i + 1;j < ansicodes.size();++j){
				ImmutableTriple<Integer, ANSIAttribute, ?> nexttrip = ansicodes.get(j);
				if(nexttrip.getMiddle().equals(triple.getMiddle())){
					attribend = nexttrip.getLeft();
					break;
				}
			}
			if(attribend == triple.getLeft()) continue;
			if(attribend < triple.getLeft()) attribend = sbuf.length() - 1;
			ansistr.addAttribute(triple.getMiddle(), triple.getRight(), triple.getLeft(), attribend);
		}
		return ansistr;
	}

	public String toString(){
		AttributedCharacterIterator citer = getIterator();
		StringBuffer sbuf = new StringBuffer(citer.getEndIndex());
		char currch = citer.first();
		while(currch != CharacterIterator.DONE){
			for(Entry<Attribute, Object> entry : citer.getAttributes().entrySet()){
				if(!(entry.getKey() instanceof ANSIAttribute))continue;
				if(citer.getRunStart(entry.getKey()) == citer.getIndex()){
					ANSIAttribute key = (ANSIAttribute) entry.getKey();
					Object attrval = entry.getValue();
					int code;
					Color colval;
					switch(key.getKind()){
					case BACKGROUND:
						if(attrval != null && !(attrval instanceof Color)) attrval = null;
						if(attrval == null){
							sbuf.append("\033[49m");
							break;
						}
						colval = (Color) attrval;
						if(colval.equals(Color.BLACK)){
							sbuf.append("\033[40m");
							break;
						}
						if(colval.equals(Color.GRAY)){
							sbuf.append("\033[47m");
							break;
						}
						if(colval.equals(Color.RED.darker().darker())){
							sbuf.append("\033[41m");
							break;
						}
						if(colval.equals(Color.GREEN.darker().darker())){
							sbuf.append("\033[42m");
							break;
						}
						if(colval.equals(Color.YELLOW.darker().darker())){
							sbuf.append("\033[43m");
							break;
						}
						if(colval.equals(Color.BLUE.darker().darker())){
							sbuf.append("\033[44m");
							break;
						}
						if(colval.equals(Color.MAGENTA.darker().darker())){
							sbuf.append("\033[45m");
							break;
						}
						if(colval.equals(Color.CYAN.darker().darker())){
							sbuf.append("\033[46m");
							break;
						}
						if(colval.equals(Color.RED)){
							sbuf.append("\033[101m");
							break;
						}
						if(colval.equals(Color.GREEN)){
							sbuf.append("\033[102m");
							break;
						}
						if(colval.equals(Color.YELLOW)){
							sbuf.append("\033[103m");
							break;
						}
						if(colval.equals(Color.BLUE)){
							sbuf.append("\033[104m");
							break;
						}
						if(colval.equals(Color.MAGENTA)){
							sbuf.append("\033[105m");
							break;
						}
						if(colval.equals(Color.CYAN)){
							sbuf.append("\033[106m");
							break;
						}
						if(colval.equals(Color.WHITE)){
							sbuf.append("\033[107m");
							break;
						}
						sbuf.append("\033[").append(48).append(';').append(2).append(';').append(colval.getRed())
								.append(';').append(colval.getGreen()).append(";").append(colval.getBlue()).append("m");
						break;
					case BLINK:
						if(!(attrval instanceof ANSIBlinkRate)) attrval = ANSIBlinkRate.STEADY;
						switch((ANSIBlinkRate) attrval){
						case FAST:
							code = 6;
							break;
						case SLOW:
							code = 5;
							break;
						default:
							code = 25;
							break;
						}
						sbuf.append("\033[").append(code).append("m");
						break;
					case FONT:
						if(!(attrval instanceof Byte)) attrval = (byte) 0;
						code = 10 + (Byte) attrval;
						sbuf.append("\033[").append(code).append("m");
						break;
					case FOREGROUND:
						if(attrval != null && !(attrval instanceof Color)) attrval = null;
						if(attrval == null){
							sbuf.append("\033[49m");
							break;
						}
						colval = (Color) attrval;
						if(colval.equals(Color.BLACK)){
							sbuf.append("\033[30m");
							break;
						}
						if(colval.equals(Color.LIGHT_GRAY)){
							sbuf.append("\033[37m");
							break;
						}
						if(colval.equals(Color.RED.darker())){
							sbuf.append("\033[31m");
							break;
						}
						if(colval.equals(Color.GREEN.darker())){
							sbuf.append("\033[32m");
							break;
						}
						if(colval.equals(Color.YELLOW.darker())){
							sbuf.append("\033[33m");
							break;
						}
						if(colval.equals(Color.BLUE.darker())){
							sbuf.append("\033[34m");
							break;
						}
						if(colval.equals(Color.MAGENTA.darker())){
							sbuf.append("\033[35m");
							break;
						}
						if(colval.equals(Color.CYAN.darker())){
							sbuf.append("\033[36m");
							break;
						}
						if(colval.equals(Color.RED)){
							sbuf.append("\033[91m");
							break;
						}
						if(colval.equals(Color.GREEN)){
							sbuf.append("\033[92m");
							break;
						}
						if(colval.equals(Color.YELLOW)){
							sbuf.append("\033[93m");
							break;
						}
						if(colval.equals(Color.BLUE)){
							sbuf.append("\033[94m");
							break;
						}
						if(colval.equals(Color.MAGENTA)){
							sbuf.append("\033[95m");
							break;
						}
						if(colval.equals(Color.CYAN)){
							sbuf.append("\033[96m");
							break;
						}
						if(colval.equals(Color.WHITE)){
							sbuf.append("\033[97m");
							break;
						}
						sbuf.append("\033[").append(38).append(';').append(2).append(';').append(colval.getRed())
								.append(';').append(colval.getGreen()).append(";").append(colval.getBlue()).append("m");
						break;
					case LETTER:
						if(!(attrval instanceof ANSILetterform)) attrval = ANSILetterform.NORMAL;
						switch((ANSILetterform) attrval){
						case ITALIC:
							code = 3;
							break;
						case FRAKTUR:
							code = 20;
							break;
						default:
							code = 23;
							break;
						}
						sbuf.append("\033[").append(code).append("m");
						break;
					case OVERLN:
						if(!(attrval instanceof Boolean)) attrval = false;
						sbuf.append((Boolean) attrval?"\033[53m":"\033[55m");
						break;
					case REVERSE:
						if(!(attrval instanceof Boolean)) attrval = false;
						sbuf.append((Boolean) attrval?"\033[7m":"\033[27m");
						break;
					case STRENGTH:
						if(!(attrval instanceof ANSIWeight)) attrval = ANSIWeight.NORMAL;
						switch((ANSIWeight) attrval){
						case BOLD:
							code = 1;
							break;
						case FAINT:
							code = 2;
							break;
						default:
							code = 22;
							break;
						}
						sbuf.append("\033[").append(code).append("m");
						break;
					case STRIKE:
						if(!(attrval instanceof Boolean)) attrval = false;
						sbuf.append((Boolean) attrval?"\033[9m":"\033[29");
						break;
					case SURROUND:
						if(!(attrval instanceof ANSISurround)) attrval = ANSISurround.NONE;
						switch((ANSISurround) attrval){
						case ENCIRCLED:
							code = 52;
							break;
						case FRAMED:
							code = 51;
							break;
						default:
							code = 54;
							break;
						}
						sbuf.append("\033[").append(code).append("m");
						break;
					case UNDERLN:
						if(!(attrval instanceof Boolean)) attrval = false;
						sbuf.append((Boolean) attrval?"\033[4m":"\033[24m");
						break;
					}
				}
			}
			sbuf.append(currch);
			currch = citer.next();
		}
		return sbuf.toString();
	}
}

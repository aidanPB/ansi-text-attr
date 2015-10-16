package com.github.aidanPB.text.ansi;

public class SGRTest {

	private static final String CSI = "\033[";

	public static void main(String[] args) {
		for(TextModifier tam : TextModifier.values()){
			if(tam == TextModifier.RESET || tam == TextModifier.DEUNDERLINE
					|| tam == TextModifier.SET_BG_EXTENDED || tam == TextModifier.SET_FG_EXTENDED
					|| tam == TextModifier.UNBLINKING || tam == TextModifier.UNBOLD
					|| tam == TextModifier.UNCROSSED || tam == TextModifier.UNFRATALIC
					|| tam == TextModifier.UNINVERSE || tam == TextModifier.UNOVERLINE
					|| tam == TextModifier.NOSURROUND) continue;
			System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s0m SGR code.%n", CSI, tam.getCodeNumber(), tam.toString());
		}
		System.out.println();
		System.out.println(CSI + "32m"+CSI+"41mRerunning some of the tests with green-on-red text.");
		System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s32m%1$s41m SGR code.%n", CSI,
				TextModifier.SET_FG_BLACK.getCodeNumber(),  TextModifier.SET_FG_BLACK.toString());
		System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s32m%1$s41m SGR code.%n", CSI,
				TextModifier.SET_FG_DEFAULT.getCodeNumber(),  TextModifier.SET_FG_DEFAULT.toString());
		System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s32m%1$s41m SGR code.%n", CSI,
				TextModifier.SET_BG_BLACK.getCodeNumber(),  TextModifier.SET_BG_BLACK.toString());
		System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s32m%1$s41m SGR code.%1$s0m%n", CSI,
				TextModifier.SET_BG_DEFAULT.getCodeNumber(),  TextModifier.SET_BG_DEFAULT.toString());
		System.out.println();
		System.out.println("Test complete.");
	}

}

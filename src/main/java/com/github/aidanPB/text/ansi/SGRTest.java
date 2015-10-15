package com.github.aidanPB.text.ansi;

public class SGRTest {

	private static final String CSI = "\033[";

	public static void main(String[] args) {
		for(TextAttributeModifier tam : TextAttributeModifier.values()){
			if(tam == TextAttributeModifier.RESET || tam == TextAttributeModifier.DEUNDERLINE
					|| tam == TextAttributeModifier.SET_BG_EXTENDED || tam == TextAttributeModifier.SET_FG_EXTENDED
					|| tam == TextAttributeModifier.UNBLINKING || tam == TextAttributeModifier.UNBOLD
					|| tam == TextAttributeModifier.UNCROSSED || tam == TextAttributeModifier.UNFRATALIC
					|| tam == TextAttributeModifier.UNINVERSE || tam == TextAttributeModifier.UNOVERLINE
					|| tam == TextAttributeModifier.NOSURROUND) continue;
			System.out.printf("This is a test of the %1$s%2$dm%3$s%1$s0m SGR code.%n", CSI, tam.getCodeNumber(), tam.toString());
		}
		System.out.printf("Some systems treat SGR code 21 as %1$s1mturn %1$s21moff %1$s1mbold%1$s0m;%n"
				+ " others treat it as %1$s21mdouble underline%1$s0m;%n"
				+ " still others ignore it.%n", CSI);
	}

}

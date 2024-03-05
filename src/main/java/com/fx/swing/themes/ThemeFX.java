package com.fx.swing.themes;

import com.formdev.flatlaf.FlatLightLaf;

public class ThemeFX
	extends FlatLightLaf
{
	public static final String NAME = "ThemeFX";

	public static boolean setup() {
		return setup( new ThemeFX() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, ThemeFX.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}

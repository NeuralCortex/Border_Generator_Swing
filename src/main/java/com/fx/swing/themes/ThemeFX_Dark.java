package com.fx.swing.themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class ThemeFX_Dark extends FlatDarkLaf {

    public static final String NAME = "ThemeFX";

    public static boolean setup() {
        return setup(new ThemeFX_Dark());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, ThemeFX_Dark.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

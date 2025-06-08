package com.fx.swing.themes;

import com.formdev.flatlaf.FlatLightLaf;

public class ThemeFX_Light extends FlatLightLaf {

    public static final String NAME = "ThemeFX";

    public static boolean setup() {
        return setup(new ThemeFX_Light());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, ThemeFX_Light.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

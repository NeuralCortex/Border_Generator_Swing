package com.fx.swing.renderer;

import com.fx.swing.Globals;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ColorRenderer extends JLabel implements TableCellRenderer{

    public ColorRenderer(){
        setOpaque(true);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color color=(Color)value;
        super.setBackground(color);
        
        if(isSelected){
            setBackground(Globals.COLOR_BLUE);
        }
        
        return this;
    }
}

package com.fx.swing.model;

import com.fx.swing.pojo.BorderPOJO;
import com.fx.swing.pojo.TableHeaderPOJO;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BorderTableModel extends AbstractTableModel {

     private static final Logger _log = LogManager.getLogger(BorderTableModel.class);
    private final List<TableHeaderPOJO> headerList;
    private List<BorderPOJO> list;

    public BorderTableModel(List<TableHeaderPOJO> headerList, List<BorderPOJO> list) {
        this.headerList = headerList;
        this.list = list;
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return headerList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BorderPOJO borderPOJO = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return borderPOJO.getFileName();
            case 1:
                return borderPOJO.isActive();
            case 2:
                return borderPOJO.getCountCoord();
            case 3:
                return borderPOJO.getLength();
            case 4:
                return borderPOJO.getColor();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        BorderPOJO borderPOJO = list.get(rowIndex);
        switch (columnIndex) {
            case 1:
                borderPOJO.setActive((Boolean) aValue);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return headerList.get(columnIndex).getKlasse();
    }

    @Override
    public String getColumnName(int column) {
        return headerList.get(column).getName();
    }

    public void add(BorderPOJO borderPOJO) {
        list.add(borderPOJO);
        fireTableDataChanged();
    }

    public List<BorderPOJO> getList() {
        return list;
    }
}

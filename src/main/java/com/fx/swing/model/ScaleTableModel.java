package com.fx.swing.model;

import com.fx.swing.pojo.ScalePOJO;
import com.fx.swing.pojo.TableHeaderPOJO;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScaleTableModel extends AbstractTableModel {

    private static final Logger _log = LogManager.getLogger(ScaleTableModel.class);
    private final List<TableHeaderPOJO> headerList;
    private List<ScalePOJO> list;

    public ScaleTableModel(List<TableHeaderPOJO> headerList, List<ScalePOJO> list) {
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
        ScalePOJO scalePOJO = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return scalePOJO.isActive();
            case 1:
                return scalePOJO.getDistance();
            case 2:
                return scalePOJO.getColor();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ScalePOJO scalePOJO = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                scalePOJO.setActive((Boolean) aValue);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return headerList.get(columnIndex).getKlasse();
    }

    @Override
    public String getColumnName(int column) {
        return headerList.get(column).getName();
    }

    public void add(ScalePOJO scalePOJO) {
        list.add(scalePOJO);
        fireTableDataChanged();
    }

    public List<ScalePOJO> getList() {
        return list;
    }

    public void setList(List<ScalePOJO> list) {
        this.list = list;
        fireTableDataChanged();
    }
}

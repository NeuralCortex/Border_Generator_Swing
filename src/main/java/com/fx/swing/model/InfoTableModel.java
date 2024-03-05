package com.fx.swing.model;

import com.fx.swing.pojo.InfoPOJO;
import com.fx.swing.pojo.TableHeaderPOJO;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfoTableModel extends AbstractTableModel {

    private static final Logger _log = LogManager.getLogger(InfoTableModel.class);
    private final List<TableHeaderPOJO> headerList;
    private List<InfoPOJO> list;

    public InfoTableModel(List<TableHeaderPOJO> headerList, List<InfoPOJO> list) {
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
        InfoPOJO infoPOJO = list.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return infoPOJO.getParam();
            case 1:
                return infoPOJO.getValue();
            default:
                throw new AssertionError();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return headerList.get(columnIndex).getKlasse();
    }

    @Override
    public String getColumnName(int column) {
        return headerList.get(column).getName();
    }

    public void add(InfoPOJO infoPOJO) {
        list.add(infoPOJO);
        fireTableDataChanged();
    }

    public List<InfoPOJO> getList() {
        return list;
    }

    public void setList(List<InfoPOJO> list) {
        this.list = list;
        fireTableDataChanged();
    }
}

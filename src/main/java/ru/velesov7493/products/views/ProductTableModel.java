package ru.velesov7493.products.views;

import ru.velesov7493.products.dto.Product;
import ru.velesov7493.products.stores.JdbcProductsStore;
import ru.velesov7493.products.stores.Store;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductTableModel extends AbstractTableModel {

    private final Store<Integer, Product> products;
    private int columnCount;
    private List<String[]> rows;

    public ProductTableModel() {
        products = JdbcProductsStore.getInstance();
        columnCount = 3;
        rows = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] row = rows.get(rowIndex);
        return row[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        String result;
        switch (column) {
            case 0: result = "id"; break;
            case 1: result = "name"; break;
            case 2: result = "category"; break;
            default: result = ""; break;
        }
        return result;
    }

    @Override
    public int findColumn(String columnName) {
        int result = -1;
        for (int i = 0; i < columnCount; i++) {
            if (getColumnName(i).equals(columnName)) {
                result = i;
                break;
            }
        }
        return result;
    }

    public void refresh() {
        rows =
                products.findAll().stream()
                .map((p) -> new String[]{
                    String.valueOf(p.getId()),
                    p.getName(),
                    p.getCategoryName()
                }).collect(Collectors.toList());
        fireTableDataChanged();
    }
}

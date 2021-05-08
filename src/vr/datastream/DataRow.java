package vr.datastream;

import java.util.Arrays;

public class DataRow {
    
    private final DataList list;
    private final Object[] data;

    DataRow(DataList list, Object[] data) {
        this.list = list;
        this.data = Arrays.copyOf(data, list.getHeader().size());
    }

    public Object get(String column) {
        int index = this.list.getHeader().indexOf(column);
        if (index == -1)
            return null;
        return get(index);
    }
    
    public Object get(int index) {
        if (index < 0 || index >= data.length)
            return null;
        return data[index];
    }
    
}

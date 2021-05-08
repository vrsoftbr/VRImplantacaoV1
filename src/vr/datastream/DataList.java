package vr.datastream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class DataList implements Iterable<DataRow>{
    
    private List<String> header;
    private DataRow[] rows;

    @Override
    public Iterator<DataRow> iterator() {
        return new Iterator<DataRow>() {
            private int count = 0;
            @Override
            public boolean hasNext() {
                return count < rows.length;
            }

            @Override
            public DataRow next() {
                return rows[count++];                
            }
        };
    }
    
    public static DataList of(String[] header, Object[][] values) {
        DataList dataList = new DataList();
        dataList.header =  new ArrayList<>(
                new LinkedHashSet<>(Arrays.asList(header))
        );
        dataList.rows = new DataRow[values.length];
        for (int i = 0; i < values.length; i++) {
            dataList.rows[i] = new DataRow(dataList, values[i]);
        }
        return dataList;
    }

    public List<String> getHeader() {
        return this.header;
    }
    
    public DataRow getRow(int index){
        if (index < 0 || index >= rows.length)
            return null;
        return rows[index];
    }
  
}

package vr.datastream;

public class DataStream {

    private final DataList list;
    private DataStreamProcess startProcess;
    private DataStreamProcess lastProcess;
    
    private DataStream(DataList list) {
        this.list = list;
    }
    
    public static DataStream of(DataList list) {
        return new DataStream(list);
    }

    public DataList condense() {        
        return list;
    }
    
    public static class DataStreamProcess {
        
        
        
    }
    
}

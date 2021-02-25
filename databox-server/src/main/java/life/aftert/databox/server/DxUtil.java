package life.aftert.databox.server;

import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

public class DxUtil {

    public static final byte[][] OBJ_REGIONS = new byte[][]{
            Bytes.toBytes("1"),
            Bytes.toBytes("4"),
            Bytes.toBytes("7")
    };

    //表的前缀
    static final String OBJ_TABLE_PREFIX = "dx_obj_";
    static final String DIR_TABLE_PREFIX = "dx_dir_";

    //目录表的列族
    static final String DIR_META_CF = "cf";
    public static final byte[] DIR_META_CF_BYTES = DIR_META_CF.getBytes();
    public static final String DIR_SUBDIR_CF = "sub";
    public static final byte[] DIR_SUBDIR_CF_BYTES = DIR_SUBDIR_CF.getBytes();

    //文件表的列族
    static final String OBJ_CONT_CF = "c";
    public static final byte[] OBJ_CONT_CF_BYTES = OBJ_CONT_CF.getBytes();
    static final String OBJ_META_CF = "cf";
    public static final byte[] OBJ_META_CF_BYTES = OBJ_META_CF.getBytes();

    //目录表的列名
    public static final byte[] DIR_SEQID_QUALIFIER = "u".getBytes();
    public static final byte[] OBJ_CONT_QUALIFIER = "c".getBytes();
    public static final byte[] OBJ_LEN_QUALIFIER = "l".getBytes();
    public static final byte[] OBJ_PROPS_QUALIFIER = "p".getBytes();
    public static final byte[] OBJ_MEDIATYPE_QUALIFIER = "m".getBytes();

    //hdfs上dx的根目录
    public static final String FILE_STORE_ROOT = "/dx";

    //大于这个值存到hdfs，小于这个值存到hbase
    public static final int FILE_STORE_THRESHOLD = 20 * 1024 * 1024; // 20m


    static final int OBJ_LIST_MAX_COUNT = 200;

    //存储hbase目录表id的表
    public static final String BUCKET_DIR_SEQ_TABLE = "dx_dir_seq";
    public static final String BUCKET_DIR_SEQ_CF = "s";
    public static final byte[] BUCKET_DIR_SEQ_CF_BYTES = BUCKET_DIR_SEQ_CF.getBytes();
    public static final byte[] BUCKET_DIR_SEQ_QUALIFIER = "s".getBytes();

    public static final FilterList OBJ_META_SCAN_FILTER = new FilterList(FilterList.Operator.MUST_PASS_ONE);

    static {
        try {
            byte[][] qualifiers = new byte[][]{DxUtil.DIR_SEQID_QUALIFIER,
                    DxUtil.OBJ_LEN_QUALIFIER,
                    DxUtil.OBJ_MEDIATYPE_QUALIFIER};
            for (byte[] b : qualifiers) {
                Filter filter = new QualifierFilter(CompareFilter.CompareOp.EQUAL,new BinaryComparator(b));
                OBJ_META_SCAN_FILTER.addFilter(filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  获取目录表的全名
     */
    public static String getDirTableName(String bucket) {
        return DIR_TABLE_PREFIX + bucket;
    }

    /**
     * 获取文件表的全名
     *
     * @param bucket
     * @return
     */
    public static String getObjTableName(String bucket) {
        return OBJ_TABLE_PREFIX + bucket;
    }

    /**
     * 获取目录表的所有列族
     *
     * @return
     */
    public static String[] getDirColumnFamily() {
        return new String[]{DIR_SUBDIR_CF, DIR_META_CF};
    }

    /**
     * 获取文件表的所有列族
     *
     * @return
     */
    public static String[] getObjColumnFamily() {
        return new String[]{OBJ_META_CF, OBJ_CONT_CF};
    }


}

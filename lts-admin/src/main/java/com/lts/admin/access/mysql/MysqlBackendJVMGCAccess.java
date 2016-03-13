package com.lts.admin.access.mysql;

import com.lts.admin.access.RshHandler;
import com.lts.admin.access.face.BackendJVMGCAccess;
import com.lts.admin.request.JvmDataReq;
import com.lts.admin.request.MDataPaginationReq;
import com.lts.core.cluster.Config;
import com.lts.monitor.access.domain.JVMGCDataPo;
import com.lts.monitor.access.mysql.MysqlJVMGCAccess;
import com.lts.store.jdbc.builder.DeleteSql;
import com.lts.store.jdbc.builder.SelectSql;
import com.lts.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author Robert HG (254963746@qq.com) on 3/10/16.
 */
public class MysqlBackendJVMGCAccess extends MysqlJVMGCAccess implements BackendJVMGCAccess {

    public MysqlBackendJVMGCAccess(Config config) {
        super(config);
    }

    @Override
    public void delete(JvmDataReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    @Override
    public List<JVMGCDataPo> queryAvg(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .select()
                .columns("timestamp",
                        "AVG(young_gc_collection_count) AS young_gc_collection_count",
                        "AVG(young_gc_collection_time) AS young_gc_collection_time",
                        "AVG(full_gc_collection_count) AS full_gc_collection_count",
                        "AVG(full_gc_collection_time) AS full_gc_collection_time",
                        "AVG(span_young_gc_collection_count) AS span_young_gc_collection_count",
                        "AVG(span_young_gc_collection_time) AS span_young_gc_collection_time",
                        "AVG(span_full_gc_collection_count) span_full_gc_collection_count",
                        "AVG(span_full_gc_collection_time) span_full_gc_collection_time")
                .from()
                .table(getTableName())
                .whereSql(buildWhereSql(request))
                .groupBy(" timestamp ASC ")
                .limit(request.getStart(), request.getLimit())
                .list(RshHandler.JVM_GC_SUM_M_DATA_RSH);
    }

    public WhereSql buildWhereSql(JvmDataReq req) {
        return new WhereSql()
                .andOnNotEmpty("identity = ?", req.getIdentity())
                .andBetween("timestamp", req.getStartTime(), req.getEndTime());

    }

    public WhereSql buildWhereSql(MDataPaginationReq request) {
        return new WhereSql()
                .andOnNotNull("id = ?", request.getId())
                .andOnNotEmpty("identity = ?", request.getIdentity())
                .andOnNotEmpty("node_group = ?", request.getNodeGroup())
                .andBetween("timestamp", request.getStartTime(), request.getEndTime());
    }
}

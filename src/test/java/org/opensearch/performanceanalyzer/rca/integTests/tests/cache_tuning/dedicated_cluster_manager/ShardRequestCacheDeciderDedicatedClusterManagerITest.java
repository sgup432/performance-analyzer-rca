/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.dedicated_cluster_manager;

import static org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.Constants.CACHE_TUNING_RESOURCES_DIR;
import static org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.Constants.INDEX_NAME;
import static org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.Constants.SHARD_ID;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.opensearch.performanceanalyzer.metrics.AllMetrics;
import org.opensearch.performanceanalyzer.rca.framework.api.metrics.Cache_Max_Size;
import org.opensearch.performanceanalyzer.rca.framework.api.metrics.Cache_Request_Eviction;
import org.opensearch.performanceanalyzer.rca.framework.api.metrics.Cache_Request_Hit;
import org.opensearch.performanceanalyzer.rca.framework.api.metrics.Cache_Request_Size;
import org.opensearch.performanceanalyzer.rca.framework.api.metrics.Heap_Max;
import org.opensearch.performanceanalyzer.rca.integTests.framework.RcaItMarker;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AClusterType;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AErrorPatternIgnored;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AExpect;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.AMetric;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ARcaConf;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ARcaGraph;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ATable;
import org.opensearch.performanceanalyzer.rca.integTests.framework.annotations.ATuple;
import org.opensearch.performanceanalyzer.rca.integTests.framework.configs.ClusterType;
import org.opensearch.performanceanalyzer.rca.integTests.framework.configs.HostTag;
import org.opensearch.performanceanalyzer.rca.integTests.framework.runners.RcaItNotEncryptedRunner;
import org.opensearch.performanceanalyzer.rca.integTests.tests.cache_tuning.validator.ShardRequestCacheDeciderValidator;
import org.opensearch.performanceanalyzer.rca.persistence.actions.PersistedAction;
import org.opensearch.performanceanalyzer.rca.store.OpenSearchAnalysisGraph;

@Category(RcaItMarker.class)
@RunWith(RcaItNotEncryptedRunner.class)
@AClusterType(ClusterType.MULTI_NODE_CO_LOCATED_CLUSTER_MANAGER)
@ARcaGraph(OpenSearchAnalysisGraph.class)
// specify a custom rca.conf to set the collector time periods to 5s to reduce runtime
@ARcaConf(dataNode = CACHE_TUNING_RESOURCES_DIR + "rca.conf")
@AMetric(
        name = Cache_Request_Size.class,
        dimensionNames = {
            AllMetrics.CommonDimension.Constants.INDEX_NAME_VALUE,
            AllMetrics.CommonDimension.Constants.SHARDID_VALUE
        },
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {INDEX_NAME, SHARD_ID},
                                sum = 100.0,
                                avg = 100.0,
                                min = 100.0,
                                max = 100.0)
                    })
        })
@AMetric(
        name = Cache_Request_Eviction.class,
        dimensionNames = {
            AllMetrics.CommonDimension.Constants.INDEX_NAME_VALUE,
            AllMetrics.CommonDimension.Constants.SHARDID_VALUE
        },
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {INDEX_NAME, SHARD_ID},
                                sum = 1.0,
                                avg = 1.0,
                                min = 1.0,
                                max = 1.0)
                    })
        })
@AMetric(
        name = Cache_Request_Hit.class,
        dimensionNames = {
            AllMetrics.CommonDimension.Constants.INDEX_NAME_VALUE,
            AllMetrics.CommonDimension.Constants.SHARDID_VALUE
        },
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {INDEX_NAME, SHARD_ID},
                                sum = 1.0,
                                avg = 1.0,
                                min = 1.0,
                                max = 1.0)
                    })
        })
@AMetric(
        name = Cache_Max_Size.class,
        dimensionNames = {AllMetrics.CacheConfigDimension.Constants.TYPE_VALUE},
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {
                                    AllMetrics.CacheType.Constants.SHARD_REQUEST_CACHE_NAME
                                },
                                sum = 100.0,
                                avg = 100.0,
                                min = 100.0,
                                max = 100.0)
                    }),
            @ATable(
                    hostTag = HostTag.ELECTED_CLUSTER_MANAGER,
                    tuple = {
                        @ATuple(
                                dimensionValues = {
                                    AllMetrics.CacheType.Constants.SHARD_REQUEST_CACHE_NAME
                                },
                                sum = 100.0,
                                avg = 100.0,
                                min = 100.0,
                                max = 100.0)
                    })
        })
@AMetric(
        name = Heap_Max.class,
        dimensionNames = {AllMetrics.HeapDimension.Constants.TYPE_VALUE},
        tables = {
            @ATable(
                    hostTag = HostTag.DATA_0,
                    tuple = {
                        @ATuple(
                                dimensionValues = {AllMetrics.GCType.Constants.HEAP_VALUE},
                                sum = 1000000.0,
                                avg = 1000000.0,
                                min = 1000000.0,
                                max = 1000000.0)
                    }),
            @ATable(
                    hostTag = {HostTag.ELECTED_CLUSTER_MANAGER},
                    tuple = {
                        @ATuple(
                                dimensionValues = {AllMetrics.GCType.Constants.HEAP_VALUE},
                                sum = 1000000.0,
                                avg = 1000000.0,
                                min = 1000000.0,
                                max = 1000000.0)
                    })
        })
public class ShardRequestCacheDeciderDedicatedClusterManagerITest {
    // Test CacheDecider for ModifyCacheAction (shard request cache).
    // The cache decider should emit modify cache size action as shard request cache rca is
    // unhealthy.
    @Test
    @AExpect(
            what = AExpect.Type.DB_QUERY,
            on = HostTag.ELECTED_CLUSTER_MANAGER,
            validator = ShardRequestCacheDeciderValidator.class,
            forRca = PersistedAction.class,
            timeoutSeconds = 1000)
    @AErrorPatternIgnored(
            pattern = "AggregateMetric:gather()",
            reason = "CPU metrics are expected to be missing in this integ test")
    @AErrorPatternIgnored(
            pattern = "Metric:gather()",
            reason = "Metrics are expected to be missing in this integ test")
    @AErrorPatternIgnored(
            pattern = "NodeConfigCacheReaderUtil",
            reason = "Node Config Cache are expected to be missing in this integ test.")
    @AErrorPatternIgnored(
            pattern = "SubscribeResponseHandler:onError()",
            reason =
                    "A unit test expressly calls SubscribeResponseHandler#onError, which writes an error log")
    @AErrorPatternIgnored(
            pattern = "SQLParsingUtil:readDataFromSqlResult()",
            reason = "Old gen metrics is expected to be missing in this integ test.")
    @AErrorPatternIgnored(
            pattern = "HighHeapUsageOldGenRca:operate()",
            reason = "Old gen rca is expected to be missing in this integ test.")
    @AErrorPatternIgnored(
            pattern = "ModifyCacheMaxSizeAction:build()",
            reason = "Node config cache is expected to be missing during shutdown")
    @AErrorPatternIgnored(
            pattern = "NodeConfigCollector:collectAndPublishMetric()",
            reason = "Field data cache metrics is expected to be missing")
    @AErrorPatternIgnored(
            pattern = "CacheUtil:getCacheMaxSize()",
            reason = "Field data cache metrics is expected to be missing.")
    @AErrorPatternIgnored(
            pattern = "OldGenRca:getMaxOldGenSizeOrDefault()",
            reason = "Old gen metrics is expected to be missing in this integ test.")
    public void testShardRequestCacheAction() {}
}

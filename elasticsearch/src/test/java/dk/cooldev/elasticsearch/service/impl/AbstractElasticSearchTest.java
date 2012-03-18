package dk.cooldev.elasticsearch.service.impl;

import dk.cooldev.elasticsearch.bean.Document;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.junit.*;

import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 6:10 AM
 * To change this template use File | Settings | File Templates.
 */

public abstract class AbstractElasticSearchTest {
    protected static ElasticSearchServiceImpl service;
    private static Node node;
    protected static Client client;
    protected Logger log = Logger.getLogger(this.getClass());

    @BeforeClass
    public static void init() {
        Settings settings = ImmutableSettings.settingsBuilder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0).put("gateway.type", "none").build();
//        node = nodeBuilder().local(true).data(true).settings(settings).node();

        node = nodeBuilder().node();
        client = node.client();
//
//        Settings settings = ImmutableSettings.settingsBuilder()

//                .put("client.transport.sniff", false)
//                .put("cluster.name", "elastic-research")
//                .build();
//        client = new TransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress("192.168.2.103", 9300));

        service = new ElasticSearchServiceImpl();
        service.setClient(client);
    }

    public static void waitForGreen() {
        ClusterHealthResponse health = client.admin().cluster().health(new ClusterHealthRequest("lists").waitForGreenStatus()).actionGet();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
        if (node != null)
            node.close();
    }

    private XContentBuilder mapping() throws Exception {
        XContentBuilder xbMapping =
                jsonBuilder()
                        .startObject()
                            .startObject(Document.Type.INTERCHANGE.name())
                                .startObject("_timestamp")
                                    .field("enabled", "true")
                                    .field("store", "yes")
                                .endObject()
                                .startObject("_ttl")
                                    .field("enabled", "true")
                                    .field("default", "10m")
                                .endObject()
                                .startObject("_routing")
                                    .field("enabled", "true")
                                    .field("path", "lsadmin.lists")
                                .endObject()
                            .endObject()
                        .endObject();
        return xbMapping;
    }


    @Before
    public void before() throws Exception {
        if (!client.admin().indices().exists(new IndicesExistsRequest("lists")).actionGet().exists()) {
            XContentBuilder mapping = mapping();
//            log.debug("mapping = " + mapping.prettyPrint().string());
            client.admin().indices().create(new CreateIndexRequest("lists").mapping(Document.Type.INTERCHANGE.name(), mapping)).actionGet();
        }

        waitForGreen();
    }

    @After
    public void after() {
        client.admin().indices().prepareDelete("lists").execute().actionGet();
    }


    protected Document createDocument(String originId, int list) {
        Document document = new Document();
        document.setOriginId(originId);
        document.setType(Document.Type.INTERCHANGE);
        document.getProperties().put(service.LSADMIN_LIST, list);
        document.getProperties().put("headline", "Some interesting headline");
        document.getProperties().put("body", "somebody has to do something");
        document.getProperties().put("publishedDate", new Date());
        document.getProperties().put("reach", 100);
        document.getProperties().put("originId", originId);
        return document;
    }

    private String getId(int list, String originId) {
        return new StringBuilder("list:").append(list).append(":origin:").append(originId).toString();
    }

    protected void indexDocument(int list, Document document) {
        String id = getId(list, document.getOriginId());
        IndexRequest indexRequest = new IndexRequest("lists", document.getType().name(), id);
        indexRequest.type(document.getType().name());
        indexRequest.source(document.getProperties());

        client.index(indexRequest).actionGet();
        client.admin().indices().refresh(new RefreshRequest("lists").waitForOperations(true)).actionGet();

    }


    protected void assertDocumentsEqual(Document document1, Document document2) {
        assertEquals(document1.getOriginId(), document2.getOriginId());
//        assertEquals(document1.getProperties(), document2.getProperties());
        assertEquals(document1.getProperties().get("headline"), document2.getProperties().get("headline"));
        assertEquals(document1.getProperties().get(service.LSADMIN_LIST), document2.getProperties().get(service.LSADMIN_LIST));
    }
}

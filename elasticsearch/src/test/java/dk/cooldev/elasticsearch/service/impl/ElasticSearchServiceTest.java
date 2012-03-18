package dk.cooldev.elasticsearch.service.impl;

import dk.cooldev.elasticsearch.bean.Document;
import dk.cooldev.elasticsearch.bean.LSFilterList;
import dk.cooldev.elasticsearch.bean.LSInFilter;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchServiceTest extends AbstractElasticSearchTest {
    @Test
    public void testFindById_NotFound() throws Exception {
        Document document = service.get(1, "id:1");
        assertEquals(null, document);
    }

    @Test
    public void findDocumentById_Found() throws Exception {
        Document document1 = createDocument("id:1", 1);
        indexDocument(1, document1);
        Document document = service.get(1, document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }

    @Test
    public void addNewDocument() throws Exception {
        Document document1 = createDocument("id:1", 1);
        Document document = service.get(1, document1.getOriginId());
        assertEquals(null, document);

        service.addOrUpdate(1, document1);
        document = service.get(1, document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }

    @Test
    public void addNewDocumentToTwoLists() throws Exception {
        Document document1 = createDocument("id:1", 1);
        Document document = service.get(1, document1.getOriginId());
        assertEquals(null, document);

        service.addOrUpdate(1, document1);
        document = service.get(1, document1.getOriginId());
        assertDocumentsEqual(document1, document);

        // Change the headline:
        document1.getProperties().put("headline", "My New Headline");
        service.addOrUpdate(2, document1);
        document = service.get(2, document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }

    @Test
    public void countTwoDocuments() {
        //List 1
        int list1 = 1;
        indexDocument(list1, createDocument("id:1", list1));
        indexDocument(list1, createDocument("id:2", list1));

        long count = service.count(list1);
        assertEquals(2, count);

        //List 2
        int list2 = 2;
        indexDocument(list2, createDocument("id:3", list2));
        indexDocument(list2, createDocument("id:4", list2));
        indexDocument(list2, createDocument("id:5", list2));

        count = service.count(list2);
        assertEquals(3, count);
    }

    @Test
    public void removeDocumentFromList() {
        Document document1 = createDocument("id:1", 1);
        indexDocument(1, document1);
        assertEquals(1, document1.getProperties().get(service.LSADMIN_LIST));

        service.addOrUpdate(2, document1);
        Document documentFromIndex = service.get(2, document1.getOriginId());
        assertEquals(2, documentFromIndex.getProperties().get(service.LSADMIN_LIST));

        service.delete(2, document1);
        documentFromIndex = service.get(2, document1.getOriginId());
        assertEquals(null, documentFromIndex);
        documentFromIndex = service.get(1, document1.getOriginId());
        assertEquals(1, documentFromIndex.getProperties().get(service.LSADMIN_LIST));

        service.delete(1, document1);
        documentFromIndex = service.get(1, document1.getOriginId());
        assertEquals(null, documentFromIndex);
    }

    @Test
    public void deleteEntireList() {
        //List 1
        int list1 = 1;
        indexDocument(list1, createDocument("id:1", list1));
        indexDocument(list1, createDocument("id:2", list1));

        long count = service.count(list1);
        assertEquals(2, count);

        //List 2
        int list2 = 2;
        indexDocument(list2, createDocument("id:3", list2));
        indexDocument(list2, createDocument("id:4", list2));
        indexDocument(list2, createDocument("id:5", list2));

        count = service.count(list2);
        assertEquals(3, count);
    }

    @Test
    public void dateTypeRemainDates() throws Exception {
        Document document1 = createDocument("id:1", 1);
        indexDocument(1, document1);
        Document document = service.get(1, document1.getOriginId());
        assertDocumentsEqual(document1, document);
//        assertTrue(document.getProperties().get("publishedDate") instanceof Date);

    }


    @Test
    public void inFilterAny()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addIn("originId", Arrays.asList(new Object[] {2, 5, 9, 9}), LSInFilter.Execution.ANY);
        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(3, documents.size());
        assertEquals("2", documents.get(0).getOriginId());
        assertEquals("5", documents.get(1).getOriginId());
        assertEquals("9", documents.get(2).getOriginId());
    }
    @Test
    public void inFilterAll()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addIn("headline", Arrays.asList(new Object[] {"some", "1"}), LSInFilter.Execution.ALL);
        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(1, documents.size());
        assertEquals("1", documents.get(0).getOriginId());
    }
    @Test
    public void simpleGE_Filter()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        Date cutDate = getDateAt12oclock(-3);
        filterList.addSimpleFilter("publishedDate", cutDate, LSFilterList.OP.GE);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(4, documents.size());
        assertEquals("0", documents.get(0).getOriginId());
        assertEquals("1", documents.get(1).getOriginId());
        assertEquals("2", documents.get(2).getOriginId());
        assertEquals("3", documents.get(3).getOriginId());
    }
    @Test
    public void simpleGT_Filter()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        Date cutDate = getDateAt12oclock(-3);
        filterList.addSimpleFilter("publishedDate", cutDate, LSFilterList.OP.GT);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(3, documents.size());
        assertEquals("0", documents.get(0).getOriginId());
        assertEquals("1", documents.get(1).getOriginId());
        assertEquals("2", documents.get(2).getOriginId());
    }
    @Test
    public void simpleLE_Filter()
    {
        Date cutDate = getDateAt12oclock(-3);
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addSimpleFilter("publishedDate", cutDate, LSFilterList.OP.LE);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(7, documents.size());
        assertEquals("3", documents.get(0).getOriginId());
        assertEquals("4", documents.get(1).getOriginId());
        assertEquals("5", documents.get(2).getOriginId());
        assertEquals("6", documents.get(3).getOriginId());
        assertEquals("7", documents.get(4).getOriginId());
        assertEquals("8", documents.get(5).getOriginId());
        assertEquals("9", documents.get(6).getOriginId());
    }
    @Test
    public void simpleLT_Filter()
    {
        // Find all documents older than 3 days ago
        Date cutDate = getDateAt12oclock(-3);

        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addSimpleFilter("publishedDate", cutDate, LSFilterList.OP.LT);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(6, documents.size());
        assertEquals("4", documents.get(0).getOriginId());
        assertEquals("5", documents.get(1).getOriginId());
        assertEquals("6", documents.get(2).getOriginId());
        assertEquals("7", documents.get(3).getOriginId());
        assertEquals("8", documents.get(4).getOriginId());
        assertEquals("9", documents.get(5).getOriginId());
    }
    @Test
    public void simpleEQ_Filter()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addSimpleFilter("reach", 6, LSFilterList.OP.EQ);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(1, documents.size());
        assertEquals("6", documents.get(0).getOriginId());
    }
    @Test
    public void simpleNE_Filter()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addSimpleFilter("reach", 6, LSFilterList.OP.NE);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(9, documents.size());
        assertEquals("0", documents.get(0).getOriginId());
        assertEquals("1", documents.get(1).getOriginId());
        assertEquals("2", documents.get(2).getOriginId());
        assertEquals("3", documents.get(3).getOriginId());
        assertEquals("4", documents.get(4).getOriginId());
        assertEquals("5", documents.get(5).getOriginId());
        assertEquals("7", documents.get(6).getOriginId());
        assertEquals("8", documents.get(7).getOriginId());
        assertEquals("9", documents.get(8).getOriginId());
    }
    @Test
    public void rangeFilter_excludeBounds()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addRange("reach", 2, 6, false);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(3, documents.size());
        assertEquals("3", documents.get(0).getOriginId());
        assertEquals("4", documents.get(1).getOriginId());
        assertEquals("5", documents.get(2).getOriginId());

    }
    @Test
    public void rangeFilter_includeBounds()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addRange("reach", 2, 6, true);

        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(5, documents.size());
        assertEquals("2", documents.get(0).getOriginId());
        assertEquals("3", documents.get(1).getOriginId());
        assertEquals("4", documents.get(2).getOriginId());
        assertEquals("5", documents.get(3).getOriginId());
        assertEquals("6", documents.get(4).getOriginId());

    }
    @Test
    public void combine2FieldsInFilter()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addIn("originId", Arrays.asList(new Object[]{2, 5, 9, 9}), null);
        filterList.addIn("reach", Arrays.asList(new Object[] {4, 5, 6}), null);
        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(1, documents.size());
        assertEquals("5", documents.get(0).getOriginId());
    }
    @Test
    public void combine2Fields2FilterTypes()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addIn("originId", Arrays.asList(new Object[]{2, 5, 9, 9}), null);
        filterList.addRange("reach", 2, 8, true);
        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(2, documents.size());
        assertEquals("2", documents.get(0).getOriginId());
        assertEquals("5", documents.get(1).getOriginId());
    }
    @Test
    public void combine3Fields3FilterTypes()
    {
        create10Documents();
        LSFilterList filterList = new LSFilterList();
        filterList.addIn("originId", Arrays.asList(new Object[] {2, 5, 9, 9}), null);
        filterList.addRange("reach", 2, 8, true);
        filterList.addSimpleFilter("publishedDate", getDateAt12oclock(-5), LSFilterList.OP.NE);
        List<Document> documents = service.findByFilter(1, filterList);
        assertEquals(1, documents.size());
        assertEquals("2", documents.get(0).getOriginId());
    }





    private Date getDateAt12oclock(int addDays)
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, addDays);
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }


    private void create10Documents()
    {
        // Prepare ten documents published one day apart
        List<Document> documents = new ArrayList<Document>();
        for (int i=0; i<10; i++)
        {
            Document doc = createDocument(Integer.toString(i), 1);
            doc.getProperties().put("reach", i);
            doc.getProperties().put("headline", "some beautiful headline with the number " + i );
            doc.getProperties().put("publishedDate", getDateAt12oclock(-i));
            documents.add(doc);
            indexDocument(1, doc);
        }

    }

}

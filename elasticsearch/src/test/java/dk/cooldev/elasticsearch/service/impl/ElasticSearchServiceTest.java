package dk.cooldev.elasticsearch.service.impl;

import dk.cooldev.elasticsearch.bean.Document;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: csj
 * Date: 08/01/2012
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchServiceTest extends AbstractElasticSearchTest
{
    @Test
    public void testFindById_NotFound() throws Exception {
        Document document = service.get("id:1");
        assertEquals (null, document);
    }
    @Test
    public void findDocumentById_Found() throws Exception {
        Document document1 = createDocument("id:1", 1);
        indexDocument(document1);
        Document document = service.get(document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }

    @Test
    public void addNewDocument() throws Exception {
        Document document1 = createDocument("id:1", 1);
        Document document = service.get(document1.getOriginId());
        assertEquals(null, document);
        
        service.addOrUpdate(document1, 1);
        document = service.get(document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }

    @Test
    public void addNewDocumentToTwoLists() throws Exception {
        Document document1 = createDocument("id:1", 1);
        Document document = service.get(document1.getOriginId());
        assertEquals(null, document);
        
        service.addOrUpdate(document1, 1);
        document = service.get(document1.getOriginId());
        assertDocumentsEqual(document1, document);

        // Change the headline:
        document1.getProperties().put("headline", "My New Headline");
        service.addOrUpdate(document1, 2);
        document = service.get(document1.getOriginId());
        assertDocumentsEqual(document1, document);
    }


    @Test
    public void countTwoDocuments()
    {
        //List 1
        int list1 = 1;
        indexDocument(createDocument("id:1", list1));
        indexDocument(createDocument("id:2", list1));

        long count = service.count(list1);
        assertEquals(2, count);

        //List 2
        int list2 = 2;
        indexDocument(createDocument("id:3", list2));
        indexDocument(createDocument("id:4", list2));
        indexDocument(createDocument("id:5", list2));

        count = service.count(list2);
        assertEquals(3, count);
    }
    
    @Test
    public void removeDocumentFromList()
    {
        Document document1 = createDocument("id:1", 1);
        indexDocument(document1);
        assertEquals(Arrays.asList(1), document1.getProperties().get(service.LSADMIN_LISTS));

        service.addOrUpdate(document1, 2);
        Document documentFromIndex = service.get(document1.getOriginId());
        assertEquals(Arrays.asList(1, 2), documentFromIndex.getProperties().get(service.LSADMIN_LISTS));

        service.removeFromList(document1, 2);
        documentFromIndex = service.get(document1.getOriginId());
        assertEquals(Arrays.asList(1), documentFromIndex.getProperties().get(service.LSADMIN_LISTS));

        service.removeFromList(document1, 1);
        documentFromIndex = service.get(document1.getOriginId());
        assertEquals(null, documentFromIndex);

    }



    @Test
    public void deleteEntireList()
    {
        //List 1
        int list1 = 1;
        indexDocument(createDocument("id:1", list1));
        indexDocument(createDocument("id:2", list1));

        long count = service.count(list1);
        assertEquals(2, count);

        //List 2
        int list2 = 2;
        indexDocument(createDocument("id:3", list2));
        indexDocument(createDocument("id:4", list2));
        indexDocument(createDocument("id:5", list2));

        count = service.count(list2);
        assertEquals(3, count);
    }


}

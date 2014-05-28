package com.engagepoint.admincenter.web;

import com.engagepoint.admincenter.core.infinispan.InfinispanPrefs;
import com.engagepoint.admincenter.web.controller.InitController;

import com.engagepoint.admincenter.web.searcher.LuceneSearcher;
import org.apache.lucene.document.Document;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.List;
import java.util.prefs.Preferences;


public class LuceneSearchTest {
    private static final Logger LOG = LoggerFactory.getLogger(LuceneSearchTest.class);

    public static final String INIT_PROPERTIES_FILENAME = "engagepoint-service.properties";
    public static final String INIT_BINARY_PROPERTIES_FILENAME = "engagepoint-service-binary.properties";

    private LuceneSearcher luceneSearcher;
    Preferences preferences = Preferences.userRoot();
    InfinispanPrefs infinispanPrefs;


    @BeforeClass
    public void setUp() {
        infinispanPrefs = new InfinispanPrefs();

        luceneSearcher = new LuceneSearcher();
        luceneSearcher.initSearchIndex();

    }

    @AfterClass
    public void tearDown() {
        infinispanPrefs.clear();
        luceneSearcher = null;
    }

    @Test
    public void findOneProperty() throws Exception {

        preferences.put("com.test", "new4");
        luceneSearcher.updatePrefsInRAM(preferences);


        List<Document> documentLlst = luceneSearcher.searchQuery("*test*");

        assertNotNull(documentLlst);
        assertEquals(1, documentLlst.size());
        assertEquals(documentLlst.get(0).get("value"), "new4");

    }

    @Test
    public void findNode() throws Exception {
        Preferences comNode = preferences.node("com");
        Preferences engagepointNode = comNode.node("engagepoint");

        luceneSearcher.updatePrefsInRAM(preferences);

        List<Document> documentLlst = luceneSearcher.searchQuery("engagepoint");

        assertEquals(1, documentLlst.size());
        assertEquals("node", documentLlst.get(0).get("type"));

    }

    @Test
    public void findFewProperiesAndNodes() throws Exception {
        Preferences netNode = preferences.node("net");
        Preferences suiteNode = netNode.node("suite");
        Preferences firstNode = suiteNode.node("firstNode");
        Preferences secondNode = suiteNode.node("secondNode");

        firstNode.put("firstParam", "1111");
        firstNode.put("Params", "secondValue");

        secondNode.put("secondPar", "22second33");
        secondNode.put("ParamPam", "ValuefirstValue");

        luceneSearcher.updatePrefsInRAM(preferences);

        List<Document> documentLlst = luceneSearcher.searchQuery("*first*");

        assertEquals(3, documentLlst.size());

        List<Document> documentLlst2 = luceneSearcher.searchQuery("*second*");

        assertEquals(4, documentLlst2.size());


    }
}
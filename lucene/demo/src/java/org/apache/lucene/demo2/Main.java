package org.apache.lucene.demo2;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Engine engine = new Engine("./data/demo2");
        engine.batchIndex(getSortedSetDocValues());
        engine.flush();
    }

    public static List<Document> getSortedSetDocValues() {
        List<Document> docs = new ArrayList<>();

        Document doc1 = new Document();
        doc1.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("hello")));
        doc1.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("allen")));
        docs.add(doc1);

        Document doc2 = new Document();
        doc2.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("hi")));
        doc2.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("brother")));
        docs.add(doc2);

        Document doc3 = new Document();
        doc3.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("action")));
        doc3.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("lucene")));
        docs.add(doc3);

        return docs;
    }
}

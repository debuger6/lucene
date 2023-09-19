/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.own.demo.docvalue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.util.BytesRef;

import static org.apache.lucene.search.DocIdSetIterator.NO_MORE_DOCS;

public class SortedDocValuesDemo {
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/sorted_doc_value_demo");
    engine.batchIndex(sampleDocs());
    engine.commit();

   /* engine.batchIndex(genDocs(4000));
    engine.commit();

    engine.batchIndex(genDocs(4000));
    engine.commit();*/

    List<LeafReaderContext> leaves = engine.leaves();
    for (LeafReaderContext leaf : leaves) {
      System.out.println("segment: " + leaf.toString() + ": ");
      SortedDocValues sortedDocValues = leaf.reader().getSortedDocValues("sdv_field");
      while (sortedDocValues.nextDoc() != NO_MORE_DOCS) {
        int ordValue = sortedDocValues.ordValue();
        BytesRef fieldValue = sortedDocValues.lookupOrd(ordValue);
        System.out.println("ord: " + ordValue + ", " + "value: " + fieldValue.utf8ToString());
      }
    }
  }

  public static List<Document> sampleDocs() {
    List<Document> docs = new ArrayList<>();
    Document doc = new Document();
    doc.add(new SortedDocValuesField("sdv_field", new BytesRef("lucene")));
    docs.add(doc);

    doc = new Document();
    doc.add(new SortedDocValuesField("sdv_field", new BytesRef("elasticsearch")));
    docs.add(doc);

    // empty document
    doc = new Document();
    docs.add(doc);

    doc = new Document();
    doc.add(new SortedDocValuesField("sdv_field", new BytesRef("action")));
    docs.add(doc);

    return docs;
  }

  public static List<Document> genDocs(int n) {
    List<Document> docs = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      Document doc = new Document();
      doc.add(new SortedDocValuesField("sdv_field", new BytesRef(randomString(5))));
      docs.add(doc);
    }

    return docs;
  }

  static String randomString(int length) {
    String allCharacters = "abcde";
    StringBuffer randomString = new StringBuffer();

    for (int i = 0; i < length; i++) {
      int randomIndex = (int)(Math.random() * allCharacters.length());
      randomString.append(allCharacters.charAt(randomIndex));
    }
    return randomString.toString();
  }
}

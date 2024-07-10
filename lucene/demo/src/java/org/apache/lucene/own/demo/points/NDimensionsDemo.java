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

package org.apache.lucene.own.demo.points;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public class NDimensionsDemo {
  public static void main(String[] args) throws IOException {
    List<Document> docs = new ArrayList<>();
    Document doc = new Document();
    doc.add(new IntPoint("content", 1, 2));
    docs.add(doc);

    doc = new Document();
    doc.add(new IntPoint("content", 8, 9));
    doc.add(new IntPoint("content", 3, 4));
    doc.add(new IntPoint("content", 6, 7));
    docs.add(doc);

    doc = new Document();
    doc.add(new IntPoint("content", 4, 6));
    doc.add(new IntPoint("content", 2, 8));
    doc.add(new IntPoint("content", 4, 3));
    docs.add(doc);

    doc = new Document();
    doc.add(new IntPoint("content", 7, 11));
    docs.add(doc);

    Engine engine = Utils.engine("./data/n_dimensions_domo");
    engine.batchIndex(docs);
    //engine.commit();
    engine.flush();

    Query rangeQuery = IntPoint.newRangeQuery("content", new int[]{7, 11}, new int[]{7, 11});
    List<ScoreDoc> scoreDocs = engine.search(rangeQuery, 100);
    scoreDocs.forEach(scoreDoc -> {
      System.out.println("docid: " + scoreDoc.doc);
    });
  }
}

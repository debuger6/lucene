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
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

public class PointRangeQueryDemo {
  public static void main(String[] args) throws IOException {
    Random random = new Random(System.currentTimeMillis());
    List<Document> docs = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      Document document = new Document();
      document.add(new IntField("num_field", random.nextInt()%100));
      //document.add(new StringField("num_field", "hello", Field.Store.NO));
      docs.add(document);
    }
    Engine engine = Utils.engine("./data/point_range_query_demo");
    engine.batchIndex(docs);
    engine.commit();

    Query rangeQuery = IntPoint.newRangeQuery("num_field", 3, 50);
    List<ScoreDoc> scoreDocs = engine.search(rangeQuery, 100);
    scoreDocs.forEach(scoreDoc -> {
      System.out.println("docid: " + scoreDoc.doc);
    });
  }
}

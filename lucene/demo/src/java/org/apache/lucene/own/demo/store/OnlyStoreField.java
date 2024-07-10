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

package org.apache.lucene.own.demo.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;

import static org.apache.lucene.own.demo.utils.Utils.randomString;

public class OnlyStoreField {
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/store_field_demo");
    engine.batchIndex(genDocs(10000));
    engine.commit();

    List<ScoreDoc> scoreDocs = engine.search(new MatchAllDocsQuery(), 10);
    for (ScoreDoc scoreDoc : scoreDocs) {
      System.out.println("docId: " + scoreDoc.doc + " field0: " + engine.fieldValue("field0", scoreDoc.doc));
    }

  }

  public static List<Document> genDocs(int n) {
    List<Document> docs = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      docs.add(new StoredStringRow(randomString(5), randomString(5), randomString(5)).document());
    }
    return docs;
  }
}

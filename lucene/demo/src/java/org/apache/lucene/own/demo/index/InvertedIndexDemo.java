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

package org.apache.lucene.own.demo.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;

public class InvertedIndexDemo {
  public static void main(String[] args) throws IOException {
    List<Document> documents = genDocs();
    Engine engine = Utils.engine("./data/inverted_index_demo");
    engine.batchIndex(documents);
    engine.commit();
  }

  public static List<Document> genDocs() {
    List<Document> documents = new ArrayList<>();
    try {
      List<String> lines = Files.readAllLines(Paths.get("./logs/logfile"));
      lines.forEach(line -> {
        Field field = new TextField("content", line, Field.Store.NO);
        Document doc = new Document();
        doc.add(field);
        documents.add(doc);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return documents;
  }
}

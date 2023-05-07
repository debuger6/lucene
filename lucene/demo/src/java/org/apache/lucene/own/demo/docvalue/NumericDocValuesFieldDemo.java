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
import java.util.Random;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;

public class NumericDocValuesFieldDemo {
  static class Row {
    final Field field1;
    final Field field2;
    final Field field3;

    Row(long value1, long value2, long value3) {
      this.field1 = new NumericDocValuesField("field1", value1);
      this.field2 = new NumericDocValuesField("field2", value2);
      this.field3 = new NumericDocValuesField("field3", value3);
    }

    Document document() {
      Document doc = new Document();
      doc.add(field1);
      doc.add(field2);
      doc.add(field3);
      return doc;
    }
  }
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/numberic_doc_value_demo");
    engine.batchIndex(genDocs());
    engine.flush();
  }

  public static List<Document> genDocs() {
    List<Document> docs = new ArrayList<>();
    Random r = new Random();
    for (int i = 0; i < (1 << 16); i++) {
      docs.add(new Row(r.nextInt(100000), r.nextInt(100000), r.nextInt(100000)).document());
    }
    Document doc = new Document();
    doc.add(new NumericDocValuesField("field1", 1));
    docs.add(doc);
    return docs;
  }
}

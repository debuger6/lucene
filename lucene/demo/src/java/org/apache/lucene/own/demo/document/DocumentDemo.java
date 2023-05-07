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

package org.apache.lucene.own.demo.document;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.util.BytesRef;

public class DocumentDemo {
  private static Engine engine;

  static {
    try {
      engine = new Engine("./data/document_demo");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) throws IOException {
    Document document = new Document();
    // 一个 Document 可以包含多个同名 Field
    Field field1 = new StringField("str_field", "hello", Field.Store.YES);
    Field field2 = new StringField("str_field", "world", Field.Store.NO);
    Field field3 = new DoubleField("d_field", 0.2d);
    document.add(field1);
    document.add(field2);
    document.add(field3);
    document.add(new NumericDocValuesField("ndf", 1));
    document.add(new NumericDocValuesField("ndf", 2));
    document.add(new SortedDocValuesField("sdf", new BytesRef("hello")));
    document.add(new SortedDocValuesField("sdf", new BytesRef("world")));

    for (IndexableField field : document) {
      System.out.println(field.name() + ": " + field.stringValue());
    }

    engine.index(document);
    engine.flush();
  }
}

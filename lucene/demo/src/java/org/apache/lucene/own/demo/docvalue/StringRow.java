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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.util.BytesRef;

public class StringRow {
  List<Field> fields = new ArrayList<>(3);
  public StringRow(String value0, String value1, String value2) {
    fields.add(new SortedDocValuesField("field0", new BytesRef(value0)));
    fields.add(new SortedDocValuesField("field1", new BytesRef(value1)));
    fields.add(new SortedDocValuesField("field2", new BytesRef(value2)));
  }

  Document document() {
    Document doc = new Document();
    for (Field field : this.fields) {
      if (field.stringValue() != "") {
        doc.add(field);
      }
    }
    return doc;
  }
}

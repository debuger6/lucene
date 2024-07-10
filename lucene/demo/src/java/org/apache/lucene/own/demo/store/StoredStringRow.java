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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;

public class StoredStringRow {
  List<Field> fields = new ArrayList<>(3);
  public StoredStringRow(String value0, String value1, String value2) {
    fields.add(new StoredField("field0", value0));
    fields.add(new StoredField("field1", value1));
    fields.add(new StoredField("field2", value2));
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

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
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;

public class NumericRow {
  List<Field> fields = new ArrayList<>(3);

  NumericRow(long value1, long value2, long value3) {
    fields.add(new NumericDocValuesField("field0", value1));
    fields.add(new NumericDocValuesField("field1", value2));
    fields.add(new NumericDocValuesField("field2", value3));
  }

  NumericRow(long[] value0, long[] value1, long[] value2) {
    if (value0 != null) {
      if (value0.length == 1) {
        fields.add(new NumericDocValuesField("field0", value0[0]));
      } else {
        for (long value : value0) {
          fields.add(new SortedNumericDocValuesField("field0", value));
        }
      }
    }

    if (value1 != null) {
      if (value1.length == 1) {
        fields.add(new NumericDocValuesField("field1", value1[0]));
      } else {
        for (long value : value1) {
          fields.add(new SortedNumericDocValuesField("field1", value));
        }
      }
    }

    if (value2 != null) {
      if (value2.length == 1) {
        fields.add(new NumericDocValuesField("field2", value2[0]));
      } else {
        for (long value : value2) {
          fields.add(new SortedNumericDocValuesField("field2", value));
        }
      }
    }

  }

  Document document() {
    Document doc = new Document();
    for (Field field : this.fields) {
      if (field.numericValue().longValue() != -1) {
        doc.add(field);
      }
    }
    return doc;
  }
}

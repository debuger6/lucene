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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
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
      if (field1.numericValue().longValue() != -1L) {
        doc.add(field1);
      }
      if (field2.numericValue().longValue() != -1L) {
        doc.add(field2);
      }
      if (field3.numericValue().longValue() != -1L) {
        doc.add(field3);
      }
      return doc;
    }
  }
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/numeric_doc_value_demo");
    engine.batchIndex(genDocs(1));
    engine.commit();

    List<LeafReaderContext> leaves = engine.leaves();
    for (LeafReaderContext leaf : leaves) {
      NumericDocValues numericDocValues = leaf.reader().getNumericDocValues("field2");
      //numericDocValues.advanceExact(10000);
      if (numericDocValues.advanceExact(3 * (1 << 16) + 65530)) {
        System.out.println(numericDocValues.longValue());
      }
    }
  }

  // 生成 IndexedDISI block 稠密度为 blockType 的文档集合
  // blockType 枚举值为 0,1,2; 0 代表稠密度为 ALL; 1 代表稠密度为 DENSE; 2 代表稠密度为 SPARSE
  public static List<Document> genDocs(int blockType) {
    List<Document> docs = new ArrayList<>();
    Random r = new Random();

    for (int block = 0; block < 10; block++) {
      for (int i = 0; i < 1 << 16; i++) {
        // 制造一种特殊场景：block 0 为空
        if (block == 0) {
          docs.add(new Row(-1, -1, -1).document());
          continue;
        }

        // 制造 dense block：每个 block 的第 20000 个 doc 空缺
        if (blockType == 1 && i == 20000) {
          docs.add(new Row(-1, -1, -1).document());
          continue;
        }

        // 制造 sparse block：每个 block 的文档个数为 4095
        if (blockType == 2 && i >= 4095) {
          docs.add(new Row(-1, -1, -1).document());
          continue;
        }

        docs.add(new Row(r.nextInt(100000), r.nextInt(100000), r.nextInt(100000)).document());
      }
    }
    return docs;
  }
}

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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;

public class SortedNumericDocValuesDemo {
  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/sorted_numeric_doc_value_demo");
    engine.batchIndex(genDocs(1));
    engine.commit();
    engine.batchIndex(genDocs(2));
    engine.commit();

    List<LeafReaderContext> leaves = engine.leaves();
    for (LeafReaderContext leaf : leaves) {
      SortedNumericDocValues numericDocValues = leaf.reader().getSortedNumericDocValues("field1");
      //numericDocValues.advanceExact(10000);
      if (numericDocValues.advanceExact(3 * (1 << 16) + 65086)) {
        int docCount = numericDocValues.docValueCount();
        for (int i =0 ; i < docCount; i++) {
          System.out.println(numericDocValues.nextValue());
        }
      }
    }
  }

  public static List<Document> genDocs(int blockType) {
    List<Document> docs = new ArrayList<>();
    Random r = new Random();

    for (int block = 0; block < 10; block++) {
      for (int i = 0; i < 1 << 16; i++) {
        // 制造一种特殊场景：block 0 为空
        if (block == 0) {
          docs.add(new NumericRow(new long[]{-1}, new long[]{-1}, new long[]{-1}).document());
          continue;
        }

        // 制造 dense block：每个 block 的第 20000 个 doc 空缺
        if (blockType == 1 && i == 20000) {
          docs.add(new NumericRow(new long[]{-1}, new long[]{-1}, new long[]{-1}).document());
          continue;
        }

        // 制造 sparse block：每个 block 的文档个数为 4095
        if (blockType == 2 && i >= 4095) {
          docs.add(new NumericRow(new long[]{-1}, new long[]{-1}, new long[]{-1}).document());
          continue;
        }

        docs.add(new NumericRow(new long[]{r.nextInt(100000), r.nextInt(100000)}, new long[]{r.nextInt(100000), r.nextInt(100000)}, new long[]{r.nextInt(100000), r.nextInt(100000)}).document());
      }
    }
    return docs;
  }
}

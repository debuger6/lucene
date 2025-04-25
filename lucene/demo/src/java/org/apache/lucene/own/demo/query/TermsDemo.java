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

package org.apache.lucene.own.demo.query;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;

public class TermsDemo {
  public static void main(String[] args) throws IOException {
    String dataPath = "./data/term_demo";
    Engine engine = Utils.engine(dataPath);

    List<Document> docs = new ArrayList<>();
    docs.add(Utils.genSingleDocWithSingleField("hello lucene"));
    docs.add(Utils.genSingleDocWithSingleField("hello elasticsearch"));
    docs.add(Utils.genSingleDocWithSingleField("hello the world"));

    engine.batchIndex(docs);
    engine.commit();

    // 获取索引级的 Terms（可能跨多个段）
    IndexReader reader = DirectoryReader.open(new MMapDirectory(Paths.get(dataPath)));
    Terms terms = MultiTerms.getTerms(reader, "content"); // MultiTerms 是 Terms 的一种实现类，里面包含了 index 下所有 segment 的 Terms

    // 获取段级的 Terms（针对单个段）
    LeafReader leafReader = reader.leaves().get(0).reader();
    Terms leafTerms = leafReader.terms("content");

    // 遍历字段中所有词项
    if (leafTerms != null) {
      TermsEnum termsEnum = leafTerms.iterator(); // 获取迭代器
      BytesRef term;
      while ((term = termsEnum.next()) != null) {
        String termText = term.utf8ToString();
        long docFreq = termsEnum.docFreq();      // 当前词项的文档频率
        long totalTermFreq = termsEnum.totalTermFreq(); // 当前词项的总词频
        System.out.printf("Term: %s, DocFreq: %d, TotalTermFreq: %d\n",
            termText, docFreq, totalTermFreq);
      }
    }

    // 统计字段级元数据
    long sumTotalTermFreq = terms.getSumTotalTermFreq(); // 字段总词频
    long sumDocFreq = terms.getSumDocFreq();             // 所有词项的文档频率之和
    int docCount = terms.getDocCount();                  // 包含该字段的文档数

    System.out.println("Total terms in field: " + (terms.size() == -1 ? "Unknown" : terms.size()));
    System.out.println("Sum total term freq: " + sumTotalTermFreq);
    System.out.println("Sum doc freq: " + sumDocFreq);
    System.out.println("doc count: " + docCount);
  }
}

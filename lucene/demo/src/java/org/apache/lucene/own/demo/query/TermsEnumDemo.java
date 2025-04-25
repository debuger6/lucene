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
import java.util.Objects;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.CompiledAutomaton;
import org.apache.lucene.util.automaton.Operations;

public class TermsEnumDemo {
  public static void main(String[] args) throws IOException {
    String dataPath = "./data/term_enum_demo";
    Engine engine = Utils.engine(dataPath);

    List<Document> docs = new ArrayList<>();
    docs.add(Utils.genSingleDocWithSingleField("hello lucene"));
    docs.add(Utils.genSingleDocWithSingleField("hello elasticsearch"));
    docs.add(Utils.genSingleDocWithSingleField("hello the world"));

    engine.batchIndex(docs);
    engine.commit();

    // 获取索引级的 Terms（可能跨多个段）
    IndexReader reader = DirectoryReader.open(new MMapDirectory(Paths.get(dataPath)));
    Terms terms = MultiTerms.getTerms(reader, "content");

    // 获取 TermsEnum 迭代器
    assert terms != null;
    TermsEnum termsEnum = terms.iterator();
    BytesRef term;

    // 遍历所有词项
    while ((term = termsEnum.next()) != null) {
      String termText = term.utf8ToString();
      int df = termsEnum.docFreq(); // 文档频率
      System.out.printf("Term: %-20s DocFreq: %d\n", termText, df);
    }

    // 构建通配符自动机（匹配 "luc*"）
    Automaton automaton = Automata.makeString("luc");
    automaton = Operations.concatenate(automaton, Automata.makeAnyString());
    CompiledAutomaton compiled = new CompiledAutomaton(automaton);

    // 获取 TermsEnum 并过滤
    terms = MultiTerms.getTerms(reader, "content");
    assert terms != null;
    termsEnum = terms.intersect(compiled, null);
    while ((term = termsEnum.next()) != null) {
      System.out.println("Matched term: " + term.utf8ToString());
    }

    BytesRef start = new BytesRef("hello");
    BytesRef end = new BytesRef("world");

    // 跳转到起始词项
    termsEnum = Objects.requireNonNull(MultiTerms.getTerms(reader, "content")).iterator();
    if (termsEnum.seekCeil(start) == TermsEnum.SeekStatus.END) return;

    // 遍历直到超出范围
    while (termsEnum.term() != null && termsEnum.term().compareTo(end) < 0) {
      System.out.println("Term in range: " + termsEnum.term().utf8ToString());
      termsEnum.next();
    }

    BytesRef targetTerm = new BytesRef("hello");
    if (termsEnum.seekExact(targetTerm)) {
      // 获取倒排表迭代器（需要位置信息）
      PostingsEnum postings = termsEnum.postings(null, PostingsEnum.FREQS | PostingsEnum.POSITIONS);
      int docId;
      while ((docId = postings.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
        int freq = postings.freq(); // 当前文档中的词频
        System.out.printf("DocID: %d, Freq: %d\n", docId, freq);
        // 遍历位置信息
        for (int i = 0; i < freq; i++) {
          int pos = postings.nextPosition();
          System.out.println("Position: " + pos);
        }
      }
    }

  }
}

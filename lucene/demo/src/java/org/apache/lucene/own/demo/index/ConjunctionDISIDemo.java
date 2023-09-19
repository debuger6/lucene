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
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.ConjunctionUtils;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class ConjunctionDISIDemo {
  private static final String FIELD_NAME = "field0";

  public static void main(String[] args) throws IOException {
    Directory directory = new ByteBuffersDirectory();
    WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

    Document document;
    // doc0
    document = new Document();
    document.add(new TextField(FIELD_NAME, "b d", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc1
    document = new Document();
    document.add(new TextField(FIELD_NAME, "d", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc2
    document = new Document();
    document.add(new TextField(FIELD_NAME, "b c", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc3
    document = new Document();
    document.add(new TextField(FIELD_NAME, "a b d", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc4
    document = new Document();
    document.add(new TextField(FIELD_NAME, "a b c", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc5
    document = new Document();
    document.add(new TextField(FIELD_NAME, "a b c", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc6
    document = new Document();
    document.add(new TextField(FIELD_NAME, "a c", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc7
    document = new Document();
    document.add(new TextField(FIELD_NAME, "c", Field.Store.YES));
    indexWriter.addDocument(document);
    // doc8
    document = new Document();
    document.add(new TextField(FIELD_NAME, "a b c d", Field.Store.YES));
    indexWriter.addDocument(document);

    indexWriter.flush();
    indexWriter.commit();

    // 假设我们要查找这个短语匹配
    PhraseQuery phraseQuery = new PhraseQuery.Builder()
        .setSlop(20)
        .add(new Term(FIELD_NAME, "a"), 0)
        .add(new Term(FIELD_NAME, "b"), 1)
        .add(new Term(FIELD_NAME, "c"), 2)
        .add(new Term(FIELD_NAME, "d"), 3)
        .build();

    // 以下就是Lucene中寻找满足PhraseQuery的第一步，寻找同时包含a b c d的文档列表
    IndexReader reader = DirectoryReader.open(indexWriter);
    LeafReaderContext leafReaderContext = reader.getContext().leaves().get(0);
    // 包含a的倒排链
    PostingsEnum postingsEnum1 = leafReaderContext.reader().postings(new Term(FIELD_NAME, "a"));
    // 包含b的倒排链
    PostingsEnum postingsEnum2 = leafReaderContext.reader().postings(new Term(FIELD_NAME, "b"));
    // 包含c的倒排链
    PostingsEnum postingsEnum3 = leafReaderContext.reader().postings(new Term(FIELD_NAME, "c"));
    // 包含d的倒排链
    PostingsEnum postingsEnum4 = leafReaderContext.reader().postings(new Term(FIELD_NAME, "d"));

    // 同时包含a b c d的倒排链，对上面四个倒排表求交集
    DocIdSetIterator docIdSetIterator = ConjunctionUtils.intersectIterators(
        List.of(postingsEnum1, postingsEnum2, postingsEnum3, postingsEnum4));

    while (docIdSetIterator.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
      System.out.println(docIdSetIterator.docID());
    }

    indexWriter.close();
  }
}
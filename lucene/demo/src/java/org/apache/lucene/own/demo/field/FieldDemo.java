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

package org.apache.lucene.own.demo.field;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.own.demo.Engine;
import org.apache.lucene.own.demo.utils.Utils;
import org.apache.lucene.util.BytesRef;

public class FieldDemo {

  public static void main(String[] args) throws IOException {
    Engine engine = Utils.engine("./data/field_demo");
    // 自定义类型
    FieldType fieldType = new FieldType();
    fieldType.setIndexOptions(IndexOptions.DOCS); // 倒排类型
    fieldType.setTokenized(false); // 不分词
    fieldType.setStored(true); // 行存
    fieldType.setDocValuesType(DocValuesType.SORTED); // 列存类型
    fieldType.freeze();
    Field field = new Field("myself_define_field", new BytesRef("hello"), fieldType);
    Document document = new Document();
    document.add(field);
    engine.index(document);
  }
}

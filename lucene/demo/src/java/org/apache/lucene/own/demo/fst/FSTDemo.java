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

package org.apache.lucene.own.demo.fst;

import java.io.IOException;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.FSTCompiler;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;

public class FSTDemo {
  public static void main(String[] args) throws IOException {
    String[] inputValues = {"lu", "lucene", "luk", "pat", "push", "start", "tart"};
    long[] outputValues = {101, 90, 80, 66, 83, 57, 56};

    PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
    FSTCompiler.Builder<Long> builder = new FSTCompiler.Builder<>(FST.INPUT_TYPE.BYTE1, outputs);
    FSTCompiler<Long> build = builder.build();
    IntsRefBuilder intsRefBuilder = new IntsRefBuilder();
    for (int i = 0; i < inputValues.length; i ++) {
      BytesRef bytesRef = new BytesRef(inputValues[i]);
      build.add(Util.toIntsRef(bytesRef, intsRefBuilder), outputValues[i]);
    }

    FST<Long> fst = build.compile();
    BytesRef bytesRef = new BytesRef("lucene");
    Long aLong = Util.get(fst, Util.toIntsRef(bytesRef, intsRefBuilder));
    assert aLong == 90;
  }
}


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

package org.apache.lucene.demo2;

import org.apache.lucene.util.FixedBitSet;

public class TestBitSet {
  public static void main(String[] args) {
    FixedBitSet fixedBitSet = new FixedBitSet(100);
    fixedBitSet.set(3);

    // 下面4行都会打印 3
    System.out.println(fixedBitSet.nextSetBit(0));
    System.out.println(fixedBitSet.nextSetBit(1));
    System.out.println(fixedBitSet.nextSetBit(2));
    System.out.println(fixedBitSet.nextSetBit(3));

    // 这行会打印 int 的最大值
    System.out.println(fixedBitSet.nextSetBit(4));
  }
}

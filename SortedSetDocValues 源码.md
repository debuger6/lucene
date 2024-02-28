### 内存写入流程

#### Demo

```java
public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Engine engine = new Engine("./data/demo2");
        engine.batchIndex(getSortedSetDocValues());
        engine.flush();
    }

    public static List<Document> getSortedSetDocValues() {
        List<Document> docs = new ArrayList<>();

        Document doc1 = new Document();
        doc1.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("hello")));
        doc1.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("allen")));
        docs.add(doc1);

        Document doc2 = new Document();
        doc2.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("hi")));
        doc2.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("brother")));
        docs.add(doc2);

        Document doc3 = new Document();
        doc3.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("action")));
        doc3.add(new SortedSetDocValuesField("sorted_set_field", new BytesRef("lucene")));
        docs.add(doc3);

        return docs;
    }
}
```

#### SortedSetDocValuesWriter

##### 关键成员变量

```java
  final BytesRefHash hash; // 用来存 term->termID 的映射
  private final PackedLongValues.Builder pending; // stream of all termIDs 存储所有的文档对应的 termIDs
  private PackedLongValues.Builder pendingCounts; // termIDs per doc 每个文档对应的 termID 数
  private final DocsWithFieldSet docsWithField; // 包含该 field 的 docs
  private final Counter iwBytesUsed;
  private long bytesUsed; // this only tracks differences in 'pending' and 'pendingCounts'
  private final FieldInfo fieldInfo;
  private int currentDoc = -1; // 记录当前处理的 docID
  private int[] currentValues = new int[8]; // 存储当前 doc 对于的 values，这里实际存的是 value 对应的 termID
  private int currentUpto; // currentValues 的偏移
  private int maxCount; // 记录 termIDs per doc 的最大值

  private PackedLongValues finalOrds;
  private PackedLongValues finalOrdCounts;
  private int[] finalSortedValues;
  private int[] finalOrdMap;
```

##### addValue

```java
  public void addValue(int docID, BytesRef value) {
    if (docID != currentDoc) { // 如果add的docID和当前记录的currentDoc不相等，说明currentDoc的所有field value 已经添加完毕，finish当前doc
      finishCurrentDoc();
      currentDoc = docID;
    }

    addOneValue(value);
    updateBytesUsed();
  }
```

该方法是写入 SortedSetDocValues 的入口函数。首先比较写入的 docID 和当前记录的 currentDoc 是否相等，如果不相等，说明 currentDoc 对应的 values 已经添加完毕，需要收尾处理 currentDoc。下面依次讲解 addOneValue 和 finishCurrentDoc。

###### addOneValue

```java
  private void addOneValue(BytesRef value) {
    int termID = hash.add(value);
    // 省略非重要逻辑

    currentValues[currentUpto] = termID;
    currentUpto++;
  }
```

addOneValue  的逻辑非常简单，首先将 value 加入到 hash 中，返回当前 value 对于的 termID；然后将 termID 加入到 currentValues。如果 hash 中不存在当前 value，则会生成对应的 termID，termID 是递增生成的。demo 中 value 生成的 termID 如下：

| value | *hello* | *allen* |  *hi*  | *brother* | *action* | *lucene* |
| :---: | :---: | :---: | :--: | :-----: | :----: | :----: |
| termID | 0 | 1 | 2 | 3 | 4 | 5 |

###### finishCurrentDoc

```java
  private void finishCurrentDoc() {
    if (currentDoc == -1) {
      return;
    }
    Arrays.sort(currentValues, 0, currentUpto); // 排序 termID
    int lastValue = -1;
    int count = 0;
    for (int i = 0; i < currentUpto; i++) {
      int termID = currentValues[i];
      // if it's not a duplicate
      if (termID != lastValue) {
        pending.add(termID); // record the term id
        count++;
      }
      lastValue = termID;
    }
    // record the number of unique term ids for this doc
    if (pendingCounts != null) {
      pendingCounts.add(count);
    } else if (count != 1) {
      pendingCounts = PackedLongValues.deltaPackedBuilder(PackedInts.COMPACT);
      for (int i = 0; i < docsWithField.cardinality(); ++i) {
        pendingCounts.add(1);
      }
      pendingCounts.add(count);
    }
    maxCount = Math.max(maxCount, count);
    currentUpto = 0;
    docsWithField.add(currentDoc);
  }
```

finishCurrentDoc 的目的主要是将 currentValues 中的 values 即 termIDs 存入到 **pending** 中，同时将当前 doc 包含的 termID 个数存入 **pendingCounts**。注意这里 currentValues 中的 termIDs 会去重，因此 lucene 的 SortedSetDocValues 只会存储重复字符串中的一个。最后会将 currentDoc 加入到 **docsWithField** 中，表示 currentDoc 包含当前处理的字段。

经过 addValue 流程，docID 和 docValues 数据便已写入到内存结构中。内存中的数据 ready 好后，便会在某个时刻刷入到磁盘中生成对应的文件，下面会详细介绍。

##### flush

```java
  public void flush(SegmentWriteState state, Sorter.DocMap sortMap, DocValuesConsumer dvConsumer)
      throws IOException {
    final int valueCount = hash.size();
    final PackedLongValues ords;
    final PackedLongValues ordCounts;
    final int[] sortedValues; // ord -> termID, 下标表示 ord，值是 termID
    final int[] ordMap; // termID -> ord，下标表示 termID，值是 ord

    if (finalOrds == null) {
      assert finalOrdCounts == null && finalSortedValues == null && finalOrdMap == null;
      finishCurrentDoc(); // finish 最后一个 doc
      ords = pending.build();
      ordCounts = pendingCounts == null ? null : pendingCounts.build();
      sortedValues = hash.sort();
      ordMap = new int[valueCount];
      for (int ord = 0; ord < valueCount; ord++) {
        ordMap[sortedValues[ord]] = ord;
      }
    } else {
      ords = finalOrds;
      ordCounts = finalOrdCounts;
      sortedValues = finalSortedValues;
      ordMap = finalOrdMap;
    }

    final DocOrds docOrds;
    if (sortMap != null) {
      docOrds =
          new DocOrds(
              state.segmentInfo.maxDoc(),
              sortMap,
              getValues(sortedValues, ordMap, hash, ords, ordCounts, maxCount, docsWithField),
              PackedInts.FASTEST);
    } else {
      docOrds = null;
    }
    dvConsumer.addSortedSetField(
        fieldInfo,
        new EmptyDocValuesProducer() {
          @Override
          public SortedSetDocValues getSortedSet(FieldInfo fieldInfoIn) {
            if (fieldInfoIn != fieldInfo) {
              throw new IllegalArgumentException("wrong fieldInfo");
            }
            final SortedSetDocValues buf =
                getValues(sortedValues, ordMap, hash, ords, ordCounts, maxCount, docsWithField);
            if (docOrds == null) {
              return buf;
            } else {
              return new SortingSortedSetDocValues(buf, docOrds);
            }
          }
        });
  }
```

这里面注意两个关键结构：**sortedValues**，**ordMap**。**sortedValues 是 ord 到 termID 的映射**，下标代表 ord，值是 termID；相反，**ordMap 是 termID 到 ord 的映射**，下标表示 termID，值是 ord。下面我们看 finalOrds == null 这个分支，因为最后添加的 doc 还没 finish，这里还要调用一次 finishCurrentDoc，完了后，所有 doc 的数据都存入到内存结构中了；接着会对 hash 按 term（注意不是 termID） 排序，并将对应的 termID 放到排序后的位置，存储结构就是 sortedValues；下一步会将根据 sortedValues 生成 ordMap。至此，内存视图如下：

**pending**: [0,1,2,3,4,5]

**pendingCounts**: [2,2,2]

**sortedValues**: [4,1,3,0,2,5]

**ordMap**: [3,1,4,2,0,5]

**hash**: hello -> 0, allen->1, hi -> 2, brother -> 3, action -> 4, lucene -> 5

| value  | *hello* | *allen* | *hi* | *brother* | *action* | *lucene* |
| :----: | :-----: | :-----: | :--: | :-------: | :------: | :------: |
| termID |    0    |    1    |  2   |     3     |    4     |    5     |
|  Ord   |    3    |    1    |  4   |     2     |    0     |    5     |

最后会调用 dvConsumer.addSortedSetField 将内存中的数据写入到 Segment 中，具体的写入逻辑下面会详细分析。

#### Lucene90DocValuesConsumer

本小结，我们会一步一步根据源码来探究 SortedSetDocvalues 的文件组织格式，并会着重分析其中涉及的关键技术。

##### 成员变量

```java
  IndexOutput data, meta; // data 代表数据文件（.dvd）, meta 代表元数据文件（.dvm）
  final int maxDoc; // 最大文档数
  private byte[] termsDictBuffer;
```

##### addSortedSetField

这个方法是写入 Segment 的入口，代码比较长，我们逐一分解。

```java
    meta.writeInt(field.number);
    meta.writeByte(Lucene90DocValuesFormat.SORTED_SET);
```

将当前 field 的编号写入 meta，占用 4 字节；再将 docValues 的类型（这里是 SORTED_SET）写入到 meta，占用 1 字节。当前 meta 的文件格式如下：

```text
+--------+----------+---------+
| Header | field_No | DV_type |
+--------+----------+---------+
```

我们接着看往下看：

```java
    if (isSingleValued(valuesProducer.getSortedSet(field))) {
      meta.writeByte((byte) 0); // multiValued (0 = singleValued)
      doAddSortedField(
          field,
          new EmptyDocValuesProducer() {
            @Override
            public SortedDocValues getSorted(FieldInfo field) throws IOException {
              return SortedSetSelector.wrap(
                  valuesProducer.getSortedSet(field), SortedSetSelector.Type.MIN);
            }
          });
      return;
    }
    meta.writeByte((byte) 1); // multiValued (1 = multiValued)
```

首先判断当前 field 是否为单值的，所谓单值就是指该字段在所有文档中都只有一个值。如果是单值会在 meta 中写入标志位 0，否则写入 1，表示多值。该标志位占用一个字节。当前 meta 的文件格式如下：

```
+--------+----------+---------+--------------+
| Header | field_No | DV_type | SingleValued |
+--------+----------+---------+--------------+
```

如果是单值，其实就退化成 SortedDocValues，那么下面的处理逻辑则和 SortedDocValues 一样。本章的 Demo 会走 多值的分支，所以我们继续往下分析。

##### doAddSortedNumericField

该方法是专门针对多值处理的，代码如下：

```java
  private void doAddSortedNumericField(FieldInfo field, DocValuesProducer valuesProducer)
      throws IOException {
    long[] stats = writeValues(field, valuesProducer, false);
    int numDocsWithField = Math.toIntExact(stats[0]);
    long numValues = stats[1];
    assert numValues >= numDocsWithField;

    meta.writeInt(numDocsWithField);
    if (numValues > numDocsWithField) {
      long start = data.getFilePointer();
      meta.writeLong(start);
      meta.writeVInt(DIRECT_MONOTONIC_BLOCK_SHIFT);

      final DirectMonotonicWriter addressesWriter =
          DirectMonotonicWriter.getInstance(
              meta, data, numDocsWithField + 1L, DIRECT_MONOTONIC_BLOCK_SHIFT);
      long addr = 0;
      addressesWriter.add(addr);
      SortedNumericDocValues values = valuesProducer.getSortedNumeric(field);
      for (int doc = values.nextDoc();
          doc != DocIdSetIterator.NO_MORE_DOCS;
          doc = values.nextDoc()) {
        addr += values.docValueCount();
        addressesWriter.add(addr);
      }
      addressesWriter.finish();
      meta.writeLong(data.getFilePointer() - start);
    }
  }
```

我们先关注 writeValues 这个方法。

##### writeValues

```
```










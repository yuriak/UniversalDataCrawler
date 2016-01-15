package org.edu.dufe.dufedata.tfidf;

import java.util.List;



 /// <summary>
    /// 分词器接口
    /// </summary>
    public interface ISegmenter
    {
        List<String> partition(String input);
    }

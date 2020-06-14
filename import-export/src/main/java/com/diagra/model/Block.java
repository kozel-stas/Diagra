package com.diagra.model;

import java.util.List;
import java.util.Map;

public interface Block {

    String getText();

    List<String> commands();

    BlockType blockType();

    Map<String, String> metaInfo();


}

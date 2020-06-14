package com.diagra.dao.model;

import java.util.List;

public interface Block extends MetaInfo {

    List<String> getText();

    BlockType blockType();

    String getId();
}

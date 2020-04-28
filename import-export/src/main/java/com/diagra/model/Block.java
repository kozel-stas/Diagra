package com.diagra.model;

import java.util.List;

public interface Block {

    String getText();

    List<String> commands();

    BlockType blockType();


}

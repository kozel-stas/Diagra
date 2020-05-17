package com.diagra.mapper;

import java.io.IOException;

public interface EntityMapper<D, E> {

    D toDto(E entity);

    E fromDto(D dto) throws IOException;

}

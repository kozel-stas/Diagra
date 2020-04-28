package com.diagra.antlr;

public interface Error {

    /**
     * @return text error representation
     */
    @Override
    String toString();

    default MetaInfo metaInfo() {
        return MetaInfo.NULL;
    }

    interface MetaInfo {

        MetaInfo NULL = new MetaInfo() {
            @Override
            public int line() {
                return -1;
            }

            @Override
            public int position() {
                return -1;
            }

            @Override
            public String msg() {
                return null;
            }
        };

        int line();

        int position();

        String msg();

    }


}

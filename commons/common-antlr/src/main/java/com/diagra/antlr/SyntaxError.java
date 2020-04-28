package com.diagra.antlr;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxError implements Error {

    private final Recognizer<?, ?> recognizer;
    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String message;
    private final RecognitionException e;

    SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        this.recognizer = recognizer;
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = msg;
        this.e = e;
    }

    @Override
    public String toString() {
        return "Syntax error: " + "line " + line + ":" + charPositionInLine + " " + message;
    }

    @Override
    public MetaInfo metaInfo() {
        return new MetaInfo() {
            @Override
            public int line() {
                return line;
            }

            @Override
            public int position() {
                return charPositionInLine;
            }

            @Override
            public String msg() {
                return message;
            }
        };
    }
}

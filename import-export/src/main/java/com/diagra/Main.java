package com.diagra;

import com.diagra.antlr.SyntaxErrorListener;
import com.diagra.java.JavaLexer;
import com.diagra.java.JavaParser;
import com.diagra.source.java.SchemeGenerator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {

    public static void main(String[] args) {
        String lox = "sdsds";
        switch (lox) {
            case "a":
                System.out.println("aa");
                break;
        }

        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        JavaLexer lexer = new JavaLexer(CharStreams.fromString("public class Main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        String lox = \"sdsds\";\n" +
                "        switch (lox){\n" +
                "            case \"aaa\": " +
                "                System.out.println(\"lox\");\n" +
                "                break;\n" +
                "            case \"bbb\":\n" +
                "                break;\n" +
                "            default:\n" +
                "                System.out.println(lox);\n" +
                "                break;\n" +
                "        }" +
                "}" +
                "}"));
        lexer.getErrorListeners().clear();
        lexer.addErrorListener(syntaxErrorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        parser.getErrorListeners().clear();
        parser.addErrorListener(syntaxErrorListener);

        if (!syntaxErrorListener.errors().isEmpty()) {
            throw new IEException(syntaxErrorListener.toString());
        }

        ParseTreeWalker walker = new ParseTreeWalker();
        SchemeGenerator listener = new SchemeGenerator();
        walker.walk(listener, parser.compilationUnit());

        listener.getScheme();
    }

}

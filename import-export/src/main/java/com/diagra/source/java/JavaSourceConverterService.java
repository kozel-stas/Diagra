package com.diagra.source.java;

import com.diagra.ConverterService;
import com.diagra.IEException;
import com.diagra.Resource;
import com.diagra.ResourceType;
import com.diagra.antlr.SyntaxErrorListener;
import com.diagra.java.JavaLexer;
import com.diagra.java.JavaParser;
import com.google.common.base.Preconditions;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;

@Service
public class JavaSourceConverterService implements ConverterService {

    @Override
    public ResourceType outputType() {
        return ResourceType.INNER_REPRESENTATION;
    }

    @Override
    public ResourceType inputType() {
        return ResourceType.SOURCE_CODE_JAVA;
    }

    @Override
    public Resource convert(Resource resource) {
        Preconditions.checkState(resource.getResourceType() == ResourceType.SOURCE_CODE_JAVA);
        SyntaxErrorListener syntaxErrorListener = new SyntaxErrorListener();
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(resource.getObject().toString()));
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
        return new Resource(ResourceType.INNER_REPRESENTATION, listener.getScheme());
    }

}

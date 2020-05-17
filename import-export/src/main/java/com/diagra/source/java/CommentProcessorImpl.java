package com.diagra.source.java;

import com.diagra.java.JavaLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommentProcessorImpl implements CommentProcessor {

    private static final Function<Token, String> SINGLE_COMMENTS = s -> {
        String text = s.getText();
        if (text.startsWith("//")) {
            return text.substring(2);
        }
        return null;
    };

    private static final Function<Token, String> COMMENTS_BLOCK = s -> {
        String text = s.getText();
        if (text.startsWith("/*") && text.endsWith("*/")) {
            List<String> res = new LinkedList<>();
            for (String s1 : text.substring(2).substring(0, text.length() - 4).split("\n")) {
                int i = s1.indexOf("*");
                String sub = s1;
                if (i != -1) {
                    sub = sub.substring(i + 1);
                }
                if (StringUtils.isNotBlank(sub)) {
                    res.add(StringEscapeUtils.unescapeHtml4(sub));
                }
            }
            return String.join("\n", res);
        }
        return null;
    };

    private final List<Token> singleComments = new LinkedList<>();
    private final List<Token> commentsBlock = new LinkedList<>();

    public CommentProcessorImpl(Supplier<CharStream> charStream) {
        JavaLexer lexer = new JavaLexer(charStream.get());
        CommonTokenStream singleComment = new CommonTokenStream(lexer, JavaLexer.COMMENT_LINE);
        singleComment.fill();
        singleComments.addAll(singleComment.getTokens().stream().filter(token -> token.getChannel() == JavaLexer.COMMENT_LINE).collect(Collectors.toList()));

        lexer = new JavaLexer(charStream.get());
        CommonTokenStream blockComment = new CommonTokenStream(lexer, JavaLexer.COMMENT_BLOCK);
        blockComment.fill();
        commentsBlock.addAll(blockComment.getTokens().stream().filter(token -> token.getChannel() == JavaLexer.COMMENT_BLOCK).collect(Collectors.toList()));
    }

    @Override
    public String comment(ParserRuleContext context) {
        List<Token> comments = new LinkedList<>();
        comments.addAll(collectUntil(context, singleComments.iterator()));
        comments.addAll(collectUntil(context, commentsBlock.iterator()));
        comments.sort(Comparator.comparingInt(Token::getStartIndex));
        consume(context);
        return convertToText(comments, SINGLE_COMMENTS, COMMENTS_BLOCK);
    }

    private void consume(ParserRuleContext context) {
        removeUntil(context, singleComments.iterator());
        removeUntil(context, commentsBlock.iterator());
    }

    private static void removeUntil(ParserRuleContext context, Iterator<Token> token) {
        while (token.hasNext()) {
            Token comment = token.next();
            if (comment.getStopIndex() < context.start.getStartIndex()) {
                token.remove();
                continue;
            }
            break;
        }
    }

    private static List<Token> collectUntil(ParserRuleContext context, Iterator<Token> token) {
        List<Token> tokens = new LinkedList<>();
        while (token.hasNext()) {
            Token comment = token.next();
            if (comment.getStopIndex() < context.start.getStartIndex()) {
                tokens.add(comment);
                continue;
            }
            break;
        }
        return tokens;
    }

    @SafeVarargs
    private static String convertToText(Collection<Token> tokens, Function<Token, String>... transformers) {
        List<String> text = new LinkedList<>();
        for (Token token : tokens) {
            for (Function<Token, String> transformer : transformers) {
                String transformResult = transformer.apply(token);
                if (StringUtils.isNotBlank(transformResult)) {
                    text.add(transformResult);
                    break;
                }
            }
        }
        return String.join("\n", text);
    }

}

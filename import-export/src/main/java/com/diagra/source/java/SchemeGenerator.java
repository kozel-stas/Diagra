package com.diagra.source.java;

import com.diagra.IEException;
import com.diagra.java.JavaParser;
import com.diagra.java.JavaParserBaseListener;
import com.diagra.model.AlgorithmScheme;
import com.diagra.model.AlgorithmSchemeBuilder;
import com.diagra.model.Block;
import com.diagra.model.Edge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SchemeGenerator extends JavaParserBaseListener {

    private static final Logger LOG = LogManager.getLogger(SchemeGenerator.class);

    private final Map<String, AlgorithmSchemeBuilder> methodBuilders = new HashMap<>();
    private final LinkedList<ParseState> states = new LinkedList<>();
    private final SchemeVisitor schemeVisitor = new SchemeVisitor();
    private AlgorithmSchemeBuilder currentMethodBuilder;

    private AlgorithmScheme generatedSheme;

    //<editor-fold desc="method">

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        if (methodBuilders.containsKey(ctx.IDENTIFIER().getText())) {
            throw new IEException("Method " + ctx.IDENTIFIER().getText() + " duplication.");
        }
        currentMethodBuilder = AlgorithmSchemeBuilder.builder(ctx.IDENTIFIER().getText());
        methodBuilders.put(ctx.IDENTIFIER().getText(), currentMethodBuilder);
        changeState(ParseState.METHOD);
        LOG.debug("Method {} parsing was started. ", currentMethodBuilder.getName());
    }

    @Override
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        LOG.debug("Method {} parsing was ended. ", currentMethodBuilder.getName());
        currentMethodBuilder = null;
        removeUntil(ParseState.METHOD);
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        if (subState(ParseState.METHOD)) {
            String text = ctx.accept(schemeVisitor);
            currentMethodBuilder.input(text);
            LOG.debug("Method {} input param parsed.", text);
        }
    }

    @Override
    public void enterLastFormalParameter(JavaParser.LastFormalParameterContext ctx) {
        if (subState(ParseState.METHOD)) {
            String text = ctx.accept(schemeVisitor);
            currentMethodBuilder.input(text);
            LOG.debug("Method {} input param parsed.", text);
        }
    }

    @Override
    public void enterMethodBody(JavaParser.MethodBodyContext ctx) {
        changeState(ParseState.METHOD_BODY);
    }

    @Override
    public void exitMethodBody(JavaParser.MethodBodyContext ctx) {
        removeUntil(ParseState.METHOD_BODY);
    }

    //</editor-fold>

    //<editor-fold desc="body">

    @Override
    public void enterBlockStatement(JavaParser.BlockStatementContext ctx) {

    }

    @Override
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            String text = ctx.accept(schemeVisitor);
            currentMethodBuilder.process(text);
            LOG.debug("Method {} local declaration processed {}", currentMethodBuilder.getName(), text);
        }
    }

    @Override
    public void enterMethodCall(JavaParser.MethodCallContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            super.enterMethodCall(ctx);
        }
    }

    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            if (ctx.IF() != null) {
                changeState(ParseState.IF);
                changeState(ParseState.IF_BLOCK);
                LOG.debug("IF {} in method {} parsing was started.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                currentMethodBuilder.decision(ctx.parExpression().accept(schemeVisitor));
                currentMethodBuilder.decisionBlock("true");
            }
            if (ctx.ELSE() != null) {
                changeState(ParseState.ELSE_BLOCK);
                LOG.debug("ELSE {} in method {} parsing was started.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                currentMethodBuilder.decisionBlock("false");
            }
            if (ctx.SWITCH() != null) {
                changeState(ParseState.SWITCH_STATEMENT);
                LOG.debug("SWITCH {} in method {} parsing was started.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                currentMethodBuilder.decision(ctx.parExpression().accept(schemeVisitor));
            }
            if (ctx.expression() != null && !ctx.expression().isEmpty()) {
                for (JavaParser.ExpressionContext expressionContext : ctx.expression()) {
                    String text = expressionContext.accept(schemeVisitor);
                    if (expressionContext.methodCall() != null && (expressionContext.bop == null || (expressionContext.bop.getType() == JavaParser.DOT && expressionContext.THIS() != null))) {
                        LOG.debug("Expression/Method {} in method {}.", text, currentMethodBuilder.getName());
                        currentMethodBuilder.method(text);
                    } else {
                        LOG.debug("Expression/Process {} in method {}.", text, currentMethodBuilder.getName());
                        currentMethodBuilder.process(text);
                    }
                }
            }
        }
    }

    @Override
    public void exitStatement(JavaParser.StatementContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            if (ctx.IF() != null) {
                LOG.debug("IF {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                removeUntil(ParseState.IF_BLOCK);
                currentMethodBuilder.endDecisionBlock();
            }
            if (ctx.ELSE() != null) {
                LOG.debug("ELSE {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                removeUntil(ParseState.ELSE_BLOCK);
                removeUntil(ParseState.IF);
                currentMethodBuilder.endDecisionBlock();
                currentMethodBuilder.endDecision();
            }
            if (ctx.SWITCH() != null) {
                LOG.debug("SWITCH {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                removeUntil(ParseState.SWITCH_STATEMENT);
                currentMethodBuilder.endDecision();
            }
        }
    }

    //</editor-fold>

    @Override
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        List<Block> blocks = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        String name = "";
        for (AlgorithmSchemeBuilder value : methodBuilders.values()) {
            AlgorithmScheme algorithmScheme = value.build();
            blocks.addAll(algorithmScheme.getBlocks());
            edges.addAll(algorithmScheme.getEdges());
            name = algorithmScheme.getName();
        }
        generatedSheme = new AlgorithmScheme(name, blocks, edges);
    }

    public AlgorithmScheme getScheme() {
        return generatedSheme;
    }

    //<editor-fold desc="utils">

    private void removeUntil(ParseState parseState) {
        Iterator<ParseState> iterator = states.descendingIterator();
        while (iterator.hasNext()) {
            ParseState state = iterator.next();
            if (state == parseState) {
                iterator.remove();
                break;
            }
            if (state == ParseState.IF) {
                LOG.debug("State IF recovered.");
                currentMethodBuilder.endDecision();
            }
            iterator.remove();
        }
        LOG.debug("Current parse state {}.", states.peekLast());
    }

    private void changeState(ParseState parseState) {
        states.addLast(parseState);
        LOG.debug("Current parse state {}.", states.peekLast());
    }

    private ParseState state() {
        return states.peekLast();
    }

    private boolean subState(ParseState state) {
        return states.contains(state);
    }

    private enum ParseState {
        METHOD,
        METHOD_BODY,
        IF,
        IF_BLOCK,
        ELSE_BLOCK,
        SWITCH_STATEMENT,
        SWITCH_CASE_BLOCK,
        CYCLE_STATEMENT,
        ;
    }

    //</editor-fold>

}

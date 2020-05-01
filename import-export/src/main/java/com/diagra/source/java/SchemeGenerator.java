package com.diagra.source.java;

import com.diagra.IEException;
import com.diagra.java.JavaParser;
import com.diagra.java.JavaParserBaseListener;
import com.diagra.model.AlgorithmScheme;
import com.diagra.model.AlgorithmSchemeBuilder;
import com.diagra.model.Block;
import com.diagra.model.Edge;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SchemeGenerator extends JavaParserBaseListener {

    private static final Logger LOG = LogManager.getLogger(SchemeGenerator.class);

    private final Map<String, AlgorithmSchemeBuilder> methodBuilders = new HashMap<>();
    private final LinkedList<ParseState> states = new LinkedList<>();
    private final SchemeVisitor schemeVisitor = new SchemeVisitor();
    private AlgorithmSchemeBuilder currentMethodBuilder;

    private AlgorithmScheme generatedSchema;

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
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            String text = ctx.accept(schemeVisitor);
            currentMethodBuilder.process(text);
            LOG.debug("Method {} local declaration processed {}", currentMethodBuilder.getName(), text);
        }
    }

    @Override
    public void enterStatement(JavaParser.StatementContext ctx) {
        if (subState(ParseState.METHOD_BODY)) {
            if (ctx.IF() != null) {
                changeState(ParseState.IF);
                if (ctx.ELSE() != null) {
                    changeState(ParseState.ELSE_BLOCK);
                    LOG.debug("ELSE {} in method {} detected.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                }
                changeState(ParseState.IF_BLOCK);
                LOG.debug("IF {} in method {} parsing was started.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                currentMethodBuilder.decision(ctx.parExpression().expression().accept(schemeVisitor));
                currentMethodBuilder.decisionBlock("true");
            }
            if (ctx.SWITCH() != null) {
                changeState(ParseState.SWITCH);
                currentMethodBuilder.decision(ctx.parExpression().expression().accept(schemeVisitor));
                LOG.debug("SWITCH {} in method {} parsing was started.", ctx.parExpression().getText(), currentMethodBuilder.getName());
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
            processIfStatement(ctx.getText());
            if (ctx.IF() != null) {
                if (ctx.ELSE() != null) {
                    removeUntil(ParseState.ELSE_BLOCK);
                    currentMethodBuilder.endDecisionBlock();
                    LOG.debug("ELSE {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                } else {
                    LOG.debug("ELSE was emulated in method {} .", currentMethodBuilder.getName());
                    currentMethodBuilder.decisionBlock("false");
                    currentMethodBuilder.endDecisionBlock();
                }
                LOG.debug("IF {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                removeUntil(ParseState.IF);
                currentMethodBuilder.endDecision();
            }
            if (ctx.SWITCH() != null) {
                LOG.debug("SWITCH {} in method {} parsing was ended.", ctx.parExpression().getText(), currentMethodBuilder.getName());
                removeUntil(ParseState.SWITCH);
                currentMethodBuilder.endDecision();
            }
        }
    }

    @Override
    public void enterSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        if (subState(ParseState.METHOD)) {
            changeState(ParseState.SWITCH_BLOCK);
        }
    }

    @Override
    public void exitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        if (subState(ParseState.METHOD)) {
            removeUntil(ParseState.SWITCH_BLOCK);
            JavaParser.BlockStatementContext blockStatementContext = ctx.blockStatement(ctx.blockStatement().size() - 1);
            boolean casted = blockStatementContext != null && blockStatementContext.statement() != null && blockStatementContext.statement().BREAK() == null;
            if (!casted) {
                currentMethodBuilder.endDecisionBlock();
            } else {
                LOG.debug("SWITCH {} in method {} have casted block.", ctx.getText(), currentMethodBuilder.getName());
                currentMethodBuilder.endDecisionBlockCast();
            }
        }
    }

    @Override
    public void enterSwitchLabel(JavaParser.SwitchLabelContext ctx) {
        if (subState(ParseState.METHOD)) {
            ParseTree data;
            if (ctx.IDENTIFIER() != null) {
                data = ctx.IDENTIFIER();
            } else if (ctx.DEFAULT() != null) {
                data = ctx.DEFAULT();
            } else {
                data = ctx.expression();
            }
            currentMethodBuilder.decisionBlock(data.accept(schemeVisitor));
            if (state() != ParseState.SWITCH_BLOCK) {
                LOG.debug("SWITCH {} in method {} have casted block.", ctx.getText(), currentMethodBuilder.getName());
                currentMethodBuilder.endDecisionBlockCast();
            }
        }
    }

    //</editor-fold>

    private void processIfStatement(String text) {
        if (state() == ParseState.IF_BLOCK) {
            removeUntil(ParseState.IF_BLOCK);
            currentMethodBuilder.endDecisionBlock();
            if (state() == ParseState.ELSE_BLOCK) {
                LOG.debug("ELSE {} in method {} parsing was started.", text, currentMethodBuilder.getName());
                currentMethodBuilder.decisionBlock("false");
            }
        }
    }

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
        generatedSchema = new AlgorithmScheme(name, blocks, edges);
    }

    public AlgorithmScheme getScheme() {
        return generatedSchema;
    }

    //<editor-fold desc="utils">

    private void removeUntil(ParseState parseState) {
        Iterator<ParseState> iterator = states.descendingIterator();
        while (iterator.hasNext()) {
            ParseState state = iterator.next();
            if (state == parseState) {
                iterator.remove();
                LOG.debug("Current parse state {}.", states.peekLast());
                return;
            }
            if (state == ParseState.IF_BLOCK || state == ParseState.ELSE_BLOCK) {
                LOG.debug("State {} recovered.", state);
                currentMethodBuilder.endDecisionBlock();
            }
            iterator.remove();
        }
        throw new IEException("Inconsistent state of parser.");
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
        SWITCH,
        SWITCH_BLOCK,
        CYCLE_STATEMENT,
        ;
    }

    //</editor-fold>

}

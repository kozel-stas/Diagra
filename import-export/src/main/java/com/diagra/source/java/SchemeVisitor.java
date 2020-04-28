package com.diagra.source.java;

import com.diagra.java.JavaParserBaseVisitor;
import com.google.common.collect.ImmutableSet;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Set;

public class SchemeVisitor extends JavaParserBaseVisitor<String> {

    //TODO: Replace by constant from JavaParser
    private final Set<Integer> ignoreSpaceTerminal = ImmutableSet.of(
            61, 65, 111, 69, 62, 66
    );
    //TODO: Replace by constant from JavaParser
    private final Set<Integer> ignoreSpaceRule = ImmutableSet.of(
            80, 92, 81
    );

    @Override
    public String visitChildren(RuleNode node) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < node.getChildCount(); i++) {
            String childResult = node.getChild(i).accept(this);
            if (childResult != null && i != 0) {
                boolean ignoreSpace = node.getChild(i) instanceof TerminalNode &&
                        ignoreSpaceTerminal.contains(((TerminalNode) node.getChild(i)).getSymbol().getType()) || node.getChild(i) instanceof RuleContext &&
                        ignoreSpaceRule.contains(((RuleContext) node.getChild(i)).getRuleIndex());
                if (!ignoreSpace) {
                    result.append(" ");
                }
            }
            result.append(childResult);
        }
        return result.toString();
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return node.getText();
    }

}

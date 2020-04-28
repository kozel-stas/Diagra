package com.diagra.utils;

public class TransitionUtil {

    public static final String TRANSITION_LINK_CONTROLLER = "/api/transition";

    private TransitionUtil() {
    }

    public static String generateTransitionLink(String link) {
        return TRANSITION_LINK_CONTROLLER + "/" + link;
    }

    public static String subTransitionLink(String link) throws IllegalArgumentException {
        if (link.startsWith(TRANSITION_LINK_CONTROLLER)) {
            return link.replace(TRANSITION_LINK_CONTROLLER + "/", "");
        }
        throw new IllegalArgumentException("Link doesn't correspond inner format. " + link);
    }

}

package com.groovesquid.gui.style;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class DefaultStyle extends Style {

    public DefaultStyle() {
        super();
        initVariables();
    }

    private void initVariables() {
        undecorated = false;
        buttonBackgrounds = false;

    }
}

package fr.dwightstudio.jarmemu.base.fx;

import javafx.scene.Node;
import javafx.scene.text.TextFlow;

public class AdjustedTextFlow extends TextFlow {

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        /*
        double maxChildWidth = 0;
        double maxChildHeight = 0;
        for (Node child : getManagedChildren()) {
            maxChildWidth = Math.max(maxChildWidth, child.getLayoutBounds().getMaxX());
            maxChildHeight = Math.max(maxChildHeight, child.getLayoutBounds().getMaxY());
        }
        double insetWidth = getInsets().getLeft() + getInsets().getRight();
        double insetHeight = getInsets().getTop() + getInsets().getBottom();

        setMaxSize(maxChildWidth + insetWidth, maxChildHeight + insetHeight);
         */

        double prefWidth = computePrefWidth(0);
        setMaxSize(prefWidth, computePrefHeight(prefWidth));
    }
}

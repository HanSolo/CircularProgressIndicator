/*
 * Copyright (c) 2016 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hansolo.fx.circularprogressindicator;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


/**
 * Created by hansolo on 08.04.16.
 */
public class CircularProgressIndicator extends Region {
    private static final double               PREFERRED_WIDTH  = 24;
    private static final double               PREFERRED_HEIGHT = 24;
    private static final double               MINIMUM_WIDTH    = 12;
    private static final double               MINIMUM_HEIGHT   = 12;
    private static final double               MAXIMUM_WIDTH    = 1024;
    private static final double               MAXIMUM_HEIGHT   = 1024;
    private              DoubleProperty       dashOffset       = new SimpleDoubleProperty(0);
    private              DoubleProperty       dashArray_0      = new SimpleDoubleProperty(1);
    private              double               size;
    private              Pane                 pane;
    private              Circle               circle;
    private              Timeline             timeline;
    private              RotateTransition     paneRotation;
    private              InvalidationListener listener;
    private              FadeTransition       fade;
    private              boolean              isRunning;


    // ******************** Constructors **************************************
    public CircularProgressIndicator() {
        getStylesheets().add(CircularProgressIndicator.class.getResource("circular-progress-indicator.css").toExternalForm());
        getStyleClass().add("circular-progress");
        isRunning = false;
        timeline  = new Timeline();
        fade      = new FadeTransition(Duration.millis(500), this);
        listener  = observable -> {
            circle.setStrokeDashOffset(dashOffset.get());
            circle.getStrokeDashArray().setAll(dashArray_0.getValue(), 200d);
        };

        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        circle = new Circle();
        circle.setCenterX(PREFERRED_WIDTH * 0.5);
        circle.setCenterY(PREFERRED_WIDTH * 0.5);
        circle.setRadius(PREFERRED_WIDTH * 0.45);
        circle.getStyleClass().add("indicator");
        circle.setStrokeWidth(PREFERRED_WIDTH * 0.1);
        circle.setStrokeDashOffset(dashOffset.get());
        circle.getStrokeDashArray().setAll(dashArray_0.getValue(), 200d);

        pane = new StackPane(circle);
        getChildren().setAll(pane);

        // Setup timeline animation
        KeyValue kvDashOffset_0    = new KeyValue(dashOffset, 0, Interpolator.EASE_BOTH);
        KeyValue kvDashOffset_50   = new KeyValue(dashOffset, -32, Interpolator.EASE_BOTH);
        KeyValue kvDashOffset_100  = new KeyValue(dashOffset, -64, Interpolator.EASE_BOTH);

        KeyValue kvDashArray_0_0   = new KeyValue(dashArray_0, 5, Interpolator.EASE_BOTH);
        KeyValue kvDashArray_0_50  = new KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH);
        KeyValue kvDashArray_0_100 = new KeyValue(dashArray_0, 89, Interpolator.EASE_BOTH);

        KeyValue kvRotate_0        = new KeyValue(circle.rotateProperty(), -10, Interpolator.LINEAR);
        KeyValue kvRotate_100      = new KeyValue(circle.rotateProperty(), 370, Interpolator.LINEAR);

        KeyFrame kf0               = new KeyFrame(Duration.ZERO, kvDashOffset_0, kvDashArray_0_0, kvRotate_0);
        KeyFrame kf1               = new KeyFrame(Duration.millis(1000), kvDashOffset_50, kvDashArray_0_50);
        KeyFrame kf2               = new KeyFrame(Duration.millis(1500), kvDashOffset_100, kvDashArray_0_100, kvRotate_100);

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().setAll(kf0, kf1, kf2);

        // Setup additional pane rotation
        paneRotation = new RotateTransition();
        paneRotation.setNode(pane);
        paneRotation.setFromAngle(0);
        paneRotation.setToAngle(-360);
        paneRotation.setInterpolator(Interpolator.LINEAR);
        paneRotation.setCycleCount(Timeline.INDEFINITE);
        paneRotation.setDuration(new Duration(4500));

        setOpacity(0);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        dashOffset.addListener(listener);
    }


    // ******************** Methods *******************************************
    public void start() {
        if (isRunning) return;
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
        timeline.play();
        paneRotation.play();
        isRunning = true;
    }
    public void stop() {
        if (!isRunning) return;
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
        timeline.stop();
        paneRotation.stop();
        isRunning = false;
    }

    public boolean isRunning() { return isRunning; }


    // ******************** Resizing ******************************************
    private void resize() {
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            double factor = size / 24;
            circle.setScaleX(factor);
            circle.setScaleY(factor);
        }
    }
}

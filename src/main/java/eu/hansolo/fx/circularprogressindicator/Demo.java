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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Created by hansolo on 08.04.16.
 */
public class Demo extends Application {
    private CircularProgressIndicator indicator;

    @Override  public void init() {
        indicator = new CircularProgressIndicator();
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(indicator);

        Scene scene = new Scene(pane);

        stage.setTitle("Circular Progress Indicator");
        stage.setScene(scene);
        stage.show();

        indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

}

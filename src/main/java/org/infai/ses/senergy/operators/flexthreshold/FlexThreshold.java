/*
 * Copyright 2021 InfAI (CC SES)
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

package org.infai.ses.senergy.operators.flexthreshold;

import org.infai.ses.senergy.exceptions.NoValueException;
import org.infai.ses.senergy.operators.BaseOperator;
import org.infai.ses.senergy.operators.Helper;
import org.infai.ses.senergy.operators.Message;

import java.util.HashMap;
import java.util.Map;


public class FlexThreshold extends BaseOperator {
    private boolean debug;
    private final Map<String, InputSettings> inputSettings = new HashMap<>();
    private int maxScore = 0;

    public FlexThreshold(Map<String, String> inputMap) {
        debug = Boolean.parseBoolean(Helper.getEnv("DEBUG", "false"));

        for (Map.Entry<String, String> entry : inputMap.entrySet()) {
            double threshold = Double.parseDouble(config.getConfigValue(entry.getKey() + "_threshold", "0"));
            boolean upperLimit = Boolean.parseBoolean(config.getConfigValue(entry.getKey() + "_upper_limit", "true"));
            int points = Integer.parseInt(config.getConfigValue(entry.getKey() + "_points", "0"));
            maxScore += points;
            inputSettings.put(entry.getKey(), new InputSettings(threshold, upperLimit, points));

            if (debug) {
                System.out.println("Adding input " + entry.getKey() + " with " + (upperLimit ? "upper" : "lower") +
                        " threshold of " + threshold + " and " + points + " points");
            }
        }
    }

    @Override
    public void run(Message message) {
        if (debug) {
            System.out.println("----------");
        }
        int score = 0;
        for (Map.Entry<String, InputSettings> entry : inputSettings.entrySet()) {
            double value;
            try {
                value = message.getInput(entry.getKey()).getValue(Double.class);
            } catch (NoValueException e) {
                System.err.println("ERROR: No value for input " + entry.getKey());
                continue;
            }
            if (entry.getValue().isUpperLimit()) {
                if (value < entry.getValue().getThreshold()) {
                    if (debug) {
                        System.out.println(entry.getKey() + " is below upper threshold (" + value + " < " + entry.getValue().getThreshold() + ") and " + entry.getValue().getPoints() + " points are added");
                    }
                    score += entry.getValue().getPoints();
                } else if (debug) {
                    System.out.println(entry.getKey() + " is not below upper threshold (" + value + " >= " + entry.getValue().getThreshold() + ")");
                }
            } else {
                if (value > entry.getValue().getThreshold()) {
                    if (debug) {
                        System.out.println(entry.getKey() + " is above lower threshold (" + value + " > " + entry.getValue().getThreshold() + ") and " + entry.getValue().getPoints() + " points are added");
                    }
                    score += entry.getValue().getPoints();
                } else if (debug) {
                    System.out.println(entry.getKey() + " is not above lower threshold (" + value + " <= " + entry.getValue().getThreshold() + ")");
                }
            }
        }

        message.output("score", score);
        message.output("score_percentage", (int)(100.0 * score / maxScore));
    }

    @Override
    public Message configMessage(Message message) {
        for (String key : inputSettings.keySet()) {
            message.addInput(key);
        }
        return message;
    }
}

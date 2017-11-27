/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <viadee Unternehmensberatung GmbH> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV.processing.checker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.viadee.bpm.vPAV.BPMNScanner;
import de.viadee.bpm.vPAV.RuntimeConfig;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.ConfigItemNotFoundException;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;

/**
 * Factory decides which Checkers will be used in defined situations
 *
 */
public final class CheckerFactory {

    public static String implementation;

    private final static String externLocation = "external_Location";

    private final static String internLocation = "de.viadee.bpm.vPAV.processing.checker.";

    private static Logger logger = Logger.getLogger(CheckerFactory.class.getName());

    /**
     * create checkers
     *
     * @param ruleConf
     *            rules for checker
     * @param resourcesNewestVersions
     *            resourcesNewestVersions in context
     * @param element
     *            given BpmnElement
     * @param bpmnScanner
     *            bpmnScanner for model
     * @return checkers returns checkers
     *
     * @throws ConfigItemNotFoundException
     *             exception when ConfigItem (e.g. rule) not found
     */
    public static Collection<ElementChecker> createCheckerInstancesBpmnElement(
            final Map<String, Rule> ruleConf, final Collection<String> resourcesNewestVersions,
            final BpmnElement element, BPMNScanner bpmnScanner)
            throws ConfigItemNotFoundException {

        final Collection<ElementChecker> checkers = new ArrayList<ElementChecker>();
        final BaseElement baseElement = element.getBaseElement();
        if (baseElement == null) {
            throw new RuntimeException("Bpmn Element couldn't be found");
        }

        for (Map.Entry<String, Rule> rule : ruleConf.entrySet()) {
            String fullyQualifiedName = getFullyQualifiedName(rule);

            if (!fullyQualifiedName.isEmpty() && !rule.getKey().equals("ProcessVariablesModelChecker")) {
                try {
                    if (!rule.getKey().equals("VersioningChecker")) {
                        Constructor<?> c = Class.forName(fullyQualifiedName).getConstructor(Rule.class,
                                BPMNScanner.class);
                        AbstractElementChecker aChecker = (AbstractElementChecker) c.newInstance(rule.getValue(),
                                bpmnScanner);
                        checkers.add(aChecker);
                    } else {
                        Constructor<?> c = Class.forName(fullyQualifiedName).getConstructor(Rule.class,
                                BPMNScanner.class, Collection.class);
                        AbstractElementChecker aChecker = (AbstractElementChecker) c.newInstance(rule.getValue(),
                                bpmnScanner,
                                resourcesNewestVersions);
                        checkers.add(aChecker);
                    }

                } catch (NoSuchMethodException | SecurityException | ClassNotFoundException
                        | InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    logger.warning("Class " + fullyQualifiedName + " not found or couldn't be instantiated");
                    rule.getValue().deactivate();
                }
            }
        }
        return checkers;
    }

    /**
     * get the fullyQualifiedName of the rule
     * 
     * @param rule
     *            Rule in Map
     * @return fullyQualifiedName
     */
    private static String getFullyQualifiedName(Map.Entry<String, Rule> rule) {
        String fullyQualifiedName = "";
        if (Arrays.asList(RuntimeConfig.getInstance().getViadeeRules()).contains(rule.getKey())
                && rule.getValue().isActive()) {
            fullyQualifiedName = internLocation + rule.getValue().getName().trim();
        } else if (rule.getValue().isActive() && rule.getValue().getSettings() != null
                && rule.getValue().getSettings().containsKey(externLocation)) {
            fullyQualifiedName = rule.getValue().getSettings().get(externLocation).getValue()
                    + "." + rule.getValue().getName().trim();
        }
        if (fullyQualifiedName.isEmpty())
            logger.warning("Checker '" + rule.getValue().getName()
                    + "' not found. Please add setting for external_location in ruleSet.xml.");
        return fullyQualifiedName;
    }
}

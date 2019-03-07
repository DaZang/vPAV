/**
 * BSD 3-Clause License
 *
 * Copyright © 2019, viadee Unternehmensberatung AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV.processing.checker;

import de.viadee.bpm.vPAV.BpmnScanner;
import de.viadee.bpm.vPAV.RuntimeConfig;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

public class EmbeddedGroovyScriptCheckerTest {

    private static final String BASE_PATH = "src/test/resources/";

    private static ElementChecker checker;

    private static ClassLoader cl;

    final Rule rule = new Rule("EmbeddedGroovyScriptChecker", true, null, null, null, null);

    @BeforeClass
    public static void setup() throws MalformedURLException {
        final File file = new File(".");
        final String currentPath = file.toURI().toURL().toString();
        final URL classUrl = new URL(currentPath + "src/test/java");
        final URL[] classUrls = { classUrl };
        cl = new URLClassLoader(classUrls);
        RuntimeConfig.getInstance().setClassLoader(cl);
        RuntimeConfig.getInstance().getResource("en_US");
    }

    /**
     * Case: there is an empty script reference
     */
    @Test
    public void testEmptyScriptReference() throws IOException, SAXException, ParserConfigurationException {
        final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScriptReference.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<ScriptTask> baseElements = modelInstance
                .getModelElementsByType(ScriptTask.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() == 0) {
            Assert.fail("there should be generated an issue");
        }
        Assert.assertEquals("There is an empty script reference",
                issues.iterator().next().getMessage());
    }

    /**
     * Case: there is no script format for given script
     */
    @Test
    public void testEmptyScriptFormat() throws IOException, SAXException, ParserConfigurationException {
        final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScriptFormat.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<ServiceTask> baseElements = modelInstance
                .getModelElementsByType(ServiceTask.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() == 0) {
            Assert.fail("there should be generated an issue");
        }
        Assert.assertEquals("There is no script format for given script",
                issues.iterator().next().getMessage());
    }

    /**
     * Case: there is no script content for given script format
     */
    @Test
    public void testEmptyScript() throws IOException, SAXException, ParserConfigurationException {
        final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScript.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<ServiceTask> baseElements = modelInstance
                .getModelElementsByType(ServiceTask.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() == 0) {
            Assert.fail("there should be generated an issue");
        }
        Assert.assertEquals("There is no script content for given script format",
                issues.iterator().next().getMessage());
    }

    /**
     * Case: Script content in Input/Output mapping of a task
     */
    @Test
    public void testScriptInInputOutputMapping() throws IOException, SAXException, ParserConfigurationException {
        final String PATH = BASE_PATH + "ProcessVariablesMapping_InputScript.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<ServiceTask> baseElements = modelInstance
                .getModelElementsByType(ServiceTask.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() != 0) {
            Assert.fail("there should be generated an issue");
        }
        final String message = issues.iterator().next().getMessage();
        Assert.assertTrue(message.startsWith("startup failed:"));
    }


    /**
     * Case: invalid script for groovy script format
     */
    @Test
    public void testInvalidGroovyScript() throws IOException, SAXException, ParserConfigurationException {
        final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_InvalidGroovyScript.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<ServiceTask> baseElements = modelInstance
                .getModelElementsByType(ServiceTask.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() == 0) {
            Assert.fail("there should be generated an issue");
        }
        final String message = issues.iterator().next().getMessage();
        Assert.assertTrue(message.startsWith("startup failed:"));
    }

    /**
     * Case: there is no script content for given script format in TaskListener (userTask)
     */
    @Test
    public void testTaskListener() throws IOException, SAXException, ParserConfigurationException {

        final String PATH = BASE_PATH + "EmbeddedGroovyScriptCheckerTest_EmptyScriptTaskListener.bpmn";

        final BpmnScanner bpmnScanner = new BpmnScanner(PATH);

        checker = new EmbeddedGroovyScriptChecker(rule, bpmnScanner);

        // parse bpmn model
        final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

        final Collection<Task> baseElements = modelInstance
                .getModelElementsByType(Task.class);

        final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

        final Collection<CheckerIssue> issues = checker.check(element);

        if (issues.size() == 0) {
            Assert.fail("there should be generated an issue");
        }
        Assert.assertEquals("There is no script content for given script format",
                issues.iterator().next().getMessage());
    }
}

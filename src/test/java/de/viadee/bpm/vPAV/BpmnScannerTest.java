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
package de.viadee.bpm.vPAV;

import de.viadee.bpm.vPAV.processing.code.flow.BpmnElement;
import de.viadee.bpm.vPAV.processing.code.flow.ControlFlowGraph;
import de.viadee.bpm.vPAV.processing.code.flow.FlowAnalysis;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// TODO still relevant since I changed the getimplementation method to be independent of version?
public class BpmnScannerTest {

	private static final String BASE_PATH = "src/test/resources/";

	/**
	 * Case: BPMN-Model in Version 1
	 */
	@Test
	public void testModelVersionV1() {
		final String PATH = BASE_PATH + "BPMN_Model_Version_V1.bpmn";
		final String impClass = "camunda:delegateExpression";

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<ServiceTask> baseElements = modelInstance.getModelElementsByType(ServiceTask.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		String imp = scanner.getImplementation(element.getBaseElement().getId());

		assertEquals("Get unexpected implementation", imp, impClass);
	}

	/**
	 * Case: BPMN-Model in Version 2
	 */
	@Test
	public void testModelVersionV2() {
		final String PATH = BASE_PATH + "BPMN_Model_Version_V2.bpmn";
		final String impEx = "camunda:class";

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<ServiceTask> baseElements = modelInstance.getModelElementsByType(ServiceTask.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		String imp = scanner.getImplementation(element.getBaseElement().getId());

		assertEquals("Get unexpected implementation", imp, impEx);
	}

	/**
	 * Case: BPMN-Model in Version 3
	 */
	@Test
	public void testModelVersionV3() {
		final String PATH = BASE_PATH + "BPMN_Model_Version_V3.bpmn";
		final String impDel = "camunda:expression";

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<ServiceTask> baseElements = modelInstance.getModelElementsByType(ServiceTask.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		String imp = scanner.getImplementation(element.getBaseElement().getId());
		String reference = scanner.getImplementationReference(element.getBaseElement().getId(), imp);

		assertEquals("Get unexpected implementation", imp, impDel);
		assertEquals("Get unexpected implementation reference", reference, "org.camunda.bpm.platform.example.servlet.ExampleServiceTask");
	}

	/**
	 * Case: Test getScriptType
	 */
	@Test
	public void testGetScriptType() {
		final String PATH = BASE_PATH + "BPMN_Model_Version_V1.bpmn";
		final String scriptType = "inputParameter";

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<ServiceTask> baseElements = modelInstance.getModelElementsByType(ServiceTask.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		ArrayList<String> scripts = scanner.getScriptTypes(element.getBaseElement().getId());

		assertTrue("Get unexpected implementation", scripts.contains(scriptType));
	}

	/**
	 * Case: Test getXorGateWays
	 */
	@Test
	public void testGetXorGateWays() {
		final String PATH = BASE_PATH + "BPMNScannerXorGateway.bpmn";
		final String gatewayId = "ExclusiveGateway_Id";

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<Gateway> baseElements = modelInstance.getModelElementsByType(Gateway.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		String gwId = scanner.getXorGateWays(element.getBaseElement().getId());

		assertEquals("Get unexpected Element", gwId, gatewayId);
	}

	/**
	 * Case: Test getOutgoing
	 */
	@Test
	public void testGetOutgoing() {
		final String PATH = BASE_PATH + "BPMNScannerXorGateway.bpmn";
		final int anzOut = 2;

		// parse bpmn model
		final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

		final Collection<Gateway> baseElements = modelInstance.getModelElementsByType(Gateway.class);

		final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next(), new ControlFlowGraph(),
				new FlowAnalysis());

		BpmnScanner scanner = new BpmnScanner(PATH);
		int out = scanner.getOutgoing(element.getBaseElement().getId());

		assertEquals("More or less outgoing sequentflows as expected", out, anzOut);
	}
}

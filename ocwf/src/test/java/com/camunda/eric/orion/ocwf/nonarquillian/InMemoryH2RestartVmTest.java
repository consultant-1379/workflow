/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.camunda.eric.orion.ocwf.nonarquillian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions.*;
import static org.junit.Assert.*;

import java.util.*;

import org.apache.ibatis.logging.LogFactory;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.*;

import com.camunda.eric.orion.ocwf.task.CheckOrderStatusTask;
import com.camunda.eric.orion.ocwf.task.VmOperationTask;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2RestartVmTest {

    @ClassRule
    @Rule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    private static final String PROCESS_DEFINITION_KEY = "restart_vm";

    static {
        LogFactory.useSlf4jLogging(); // MyBatis
    }

    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    private Map<String, Object> initializeInputs() {

        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("vm_id", "ac66ed91-3efd-46e8-bf6e-e431932e6c03");
        vars.put("firstCommand", "stop");
        vars.put("secondCommand", "start");
        vars.put("base_url", "ieatecm11.athtem.eei.ericsson.se/ecm_service");
        vars.put("service", "vms");
        vars.put("tenant_id", "do-orion");
        vars.put("auth_header", "Basic dGVhbW9yaW9uOkRvT3Jpb24yMDE4Iw==");

        return vars;
    }

    private void mockSubprocess(final String origProcessKey, final String mockProcessId, final String fileName) {

        final BpmnModelInstance modelInstance = Bpmn.createExecutableProcess(origProcessKey).id(mockProcessId).startEvent()
                .id("StartEventProcessStarted").serviceTask().id("Task_0bk6xhv").camundaClass(VmOperationTask.class.getName()).serviceTask()
                .id("Task_14mw706").camundaClass(CheckOrderStatusTask.class.getName()).endEvent().id("EndEvent_0z3l3s4").done();

        rule.getRepositoryService().createDeployment().addModelInstance(fileName + ".bpmn", modelInstance).deploy();
    }

    /**
     * Just tests if the process definition is deployable.
     */
    @Test
    @Deployment(resources = { "bpmn/start_stop_recreate_vm.bpmn", "bpmn/restart_vm.bpmn" })
    public void testRestartFail() {

        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("vm_id", "ac66ed91-3efd-46e8-bf6e-e431932e6c03");
        vars.put("firstCommand", "sto");
        vars.put("secondCommand", "start");
        vars.put("base_url", "ieatecm11.athtem.eei.ericsson.se/ecm_service");
        vars.put("service", "vms");
        vars.put("tenant_id", "do-orion");
        vars.put("auth_header", "Basic dGVhbW9yaW9uOkRvT3Jpb24yMDE4Iw==");

        final ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, vars);

        final List<HistoricVariableInstance> firstProcessErrorVariables = rule.getProcessEngine().getHistoryService()
                .createHistoricVariableInstanceQuery().variableName("firstProcessError").list();

        final Boolean firstProcessErrorStatus = (Boolean) firstProcessErrorVariables.get(0).getValue();
        System.out.println("### first process error val:" + firstProcessErrorStatus);

        assertEquals(true, firstProcessErrorStatus);

        assertThat(processInstance.isEnded());

    }

    @Test
    @Deployment(resources = { "bpmn/start_stop_recreate_vm.bpmn", "bpmn/restart_vm.bpmn" })
    public void testRestartHappyPath() {

        final Map<String, Object> vars = initializeInputs();
        // mock first sub process
        this.mockSubprocess("start_stop_recreate_vm", "SubProcess_1mmdpba", "bpmn/start_stop_recreate_vm");

        System.out.println("stop sub process mocking done");
        // mock second sub process
        this.mockSubprocess("start_stop_recreate_vm", "SubProcess_1nno22x", "bpmn/start_stop_recreate_vm");

        System.out.println("start sub process mocking done");

        System.out.println("main process starting....");

        final ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, vars);

        try {
            Thread.sleep(30000L);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Job job = rule.getManagementService().createJobQuery().singleResult();
        rule.getManagementService().executeJob(job.getId());

        assertThat(processInstance.isEnded());
    }

}

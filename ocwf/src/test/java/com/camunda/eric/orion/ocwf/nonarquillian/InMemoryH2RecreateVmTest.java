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
import org.junit.*;

/**
 * Test case starting an in-memory database-backed Process Engine.
 */
public class InMemoryH2RecreateVmTest {

    @ClassRule
    @Rule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();

    private static final String PROCESS_DEFINITION_KEY = "start_stop_recreate_vm";

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
        vars.put("command", "recreate");
        vars.put("base_url", "ieatecm11.athtem.eei.ericsson.se/ecm_service");
        vars.put("service", "vms");
        vars.put("tenant_id", "do-orion");
        vars.put("auth_header", "Basic dGVhbW9yaW9uOkRvT3Jpb24yMDE4Iw==");

        return vars;
    }

    /**
     * Just tests if the process definition is deployable.
     */
    @Test
    @Deployment(resources = "bpmn/start_stop_recreate_vm.bpmn")
    public void testRecreateFail() {

        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("vm_id", "ac66ed91-3efd-46e8-bf6e-e431932e6c03");
        vars.put("command", "recreat");
        vars.put("base_url", "ieatecm11.athtem.eei.ericsson.se/ecm_service");
        vars.put("service", "vms");
        vars.put("tenant_id", "do-orion");
        vars.put("auth_header", "Basic dGVhbW9yaW9uOkRvT3Jpb24yMDE4Iw==");

        final ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, vars);

        final List<HistoricVariableInstance> variables = rule.getProcessEngine().getHistoryService().createHistoricVariableInstanceQuery()
                .variableName("error").list();

        final Boolean outputStatus = (Boolean) variables.get(0).getValue();
        System.out.println("###error val:" + outputStatus);

        assertEquals(true, outputStatus);
        assertThat(processInstance.isEnded());

    }

    @Test
    @Deployment(resources = "bpmn/start_stop_recreate_vm.bpmn")
    public void testRecreateHappyPath() {

        final Map<String, Object> vars = initializeInputs();

        final ProcessInstance processInstance = rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY, vars);

        try {
            Thread.sleep(30000L);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final Job job = rule.getManagementService().createJobQuery().singleResult();
        rule.getManagementService().executeJob(job.getId());

        final List<HistoricVariableInstance> errorVariables = rule.getProcessEngine().getHistoryService().createHistoricVariableInstanceQuery()
                .variableName("error").list();

        final Boolean errorOutputStatus = (Boolean) errorVariables.get(0).getValue();
        System.out.println("###error val:" + errorOutputStatus);

        assertEquals(false, errorOutputStatus);

        final List<HistoricVariableInstance> variablesFinished = rule.getProcessEngine().getHistoryService().createHistoricVariableInstanceQuery()
                .variableName("finished").list();

        final Boolean finishedOutputStatus = (Boolean) variablesFinished.get(0).getValue();
        System.out.println("###finished val:" + finishedOutputStatus);

        assertEquals(true, finishedOutputStatus);

        assertThat(processInstance.isEnded());
    }

}

package com.flowshop.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

public class ConfigurationFileReaderTest {

    @Test
    void testReadWorkstationProperties() throws FileNotFoundException {
        String testPath = getClass().getResource("/configuration-test").getPath();
        ConfigurationJsonReader configurationReader = new ConfigurationJsonReader(testPath);
        WorkstationRequirements[] requirements = configurationReader.getWorkstationsRequirements();
        assertNotNull(requirements);
        assertEquals("workstation1", requirements[0].getName());
        assertEquals("workgroup2", requirements[1].getName());
        assertEquals("buffered_workstation", requirements[2].getName());
    }

    @Test
    void testReadOperatorsProperties() throws FileNotFoundException {
        String testPath = getClass().getResource("/configuration-test").getPath();
        ConfigurationJsonReader configurationReader = new ConfigurationJsonReader(testPath);
        OperatorRequirements[] requirements = configurationReader.getOperatorRequirements();
        assertNotNull(requirements);
        assertEquals("operator1", requirements[0].getName());
        assertEquals("operator2", requirements[1].getName());
        assertEquals("users", requirements[1].getGroup());
    }

    @Test
    void testReadWorkstationPropertiesWrongFilename() {
        assertThrows(FileNotFoundException.class, () -> {
            new ConfigurationJsonReader("/configuration-test-wrong");
        });

    }

    @Test
    void readOrders() throws FileNotFoundException {
        String testPath = getClass().getResource("/configuration-test").getPath();
        ConfigurationJsonReader ordersReader = new ConfigurationJsonReader(testPath);
        OrderRequirements[] orders = ordersReader.getOrdersRequirements();
        assertNotNull(orders);
        assertEquals(1, orders.length);
        OrderRequirements order = orders[0];
        assertEquals("ITEM-10", order.getOrderId());
        assertEquals(3, order.getOperations().length);
        OperationRequrements operation1 = order.getOperations()[0];
        OperationRequrements operation2 = order.getOperations()[1];
        OperationRequrements operation3 = order.getOperations()[2];
        assertEquals("ITEM-10.OP01", operation1.getOperationId());
        assertEquals(20l, operation1.getCycleTime());
        assertEquals("WRK1", operation1.getWorkstation());
        assertEquals(2, operation1.getOperatorsAny());
        assertEquals(2, operation1.getOperatorsGroups().size());
        assertEquals("USERS", operation1.getOperatorsGroups().get(0).getGroupId());
        assertEquals(2, operation1.getOperatorsGroups().get(0).getRequired());
        assertEquals("ELC", operation1.getOperatorsGroups().get(1).getGroupId());
        assertEquals(1, operation1.getOperatorsGroups().get(1).getRequired());
        assertEquals("ITEM-10.OP02", operation2.getOperationId());
        assertEquals(40l, operation2.getCycleTime());
        assertEquals("WRK2", operation2.getWorkstation());
        assertEquals(2, operation2.getOperatorsAny());
        assertNull(operation2.getOperatorsGroups());
        assertEquals("ITEM-10.OP03", operation3.getOperationId());
        assertEquals(30l, operation3.getCycleTime());
        assertEquals("WRK3", operation3.getWorkstation());
        assertEquals(0, operation3.getOperatorsAny());
        assertNull(operation3.getOperatorsGroups());

    }
}

package com.skorikov.sendReceiv.unit;

import com.skorikov.sendReceiv.dto.PayloadDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class PayloadTest {

    @Test
    public void payloadTest() {
        PayloadDto payload = new PayloadDto();
        payload.setId(1L);
        payload.setData("Data");

        assertEquals(1L, (long) payload.getId());
        assertEquals("Data", payload.getData());

        PayloadDto payload2 = new PayloadDto(1L, "Data");

        assertEquals(payload.toString(), payload2.toString());
        assertEquals(payload.hashCode(), payload2.hashCode());
        assertEquals(payload, payload);
        assertEquals(payload, payload2);

        payload2.setData("New Data");
        assertNotEquals(payload, payload2);
    }
}

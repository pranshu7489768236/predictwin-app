package com.predictwin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictwin.config.RateLimitProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRateLimitFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimitProperties props;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void loginRateLimitShouldReturn429AfterThreshold() throws Exception {
        int limit = props.getIpLogin();
        String body = mapper.writeValueAsString(new java.util.HashMap<String,String>() {{ put("username","testuser"); put("password","x"); }});

        int received429 = 0;
        for (int i = 0; i < limit + 2; i++) {
            int status = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andReturn()
                    .getResponse()
                    .getStatus();
            if (status == 429) received429++;
        }

        // Expect at least one 429 when exceeding the configured limit
        assertThat(received429).isGreaterThanOrEqualTo(1);
    }
}

package com.dood.spring.services.webclient;

import com.dood.spring.services.webclient.service.WebAccountsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebAccountsService.class)
@WebAppConfiguration
public class WebServiceClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}

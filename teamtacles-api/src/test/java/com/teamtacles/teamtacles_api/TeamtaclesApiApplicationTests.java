package com.teamtacles.teamtacles_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeamtaclesApiApplicationTests {

	@Test
	void testCreateValidTask_ReturnsCreated() {
		String json = """
		 		{
  					"title": "Finalizar documentação do projeto",
  					"description": "Revisar e concluir a documentação técnica do sistema.",
  					"dueDate": "30/06/2025 18:00",
  					"usersResponsability": [1]
		 		}
		 	""";
	}

}

package com.andyserver.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class SpringBootDockerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {

		// Setup Rest
		restConfiguration().component("servlet").port(8080);

		// Expose Endpoints
		rest("/").bindingMode(RestBindingMode.json).produces("application/json")
			.get("/{type}").to("direct:restdocker")
			.get("/")
				.route()
					.setHeader("type")
						.constant("info")
					.to("direct:restdocker")
			.endRest();
		
		// Globally handle exceptions
		onException(Exception.class)
			.handled(true)
			.to("log:com.andyserver.camel.springboot.RouteException?showAll=true")
			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
				.setBody().constant("Error Occurred Processing Request");

		// Determine request type and set headers
		from("direct:restdocker")
				.choice()
					.when(header("type").isEqualTo("images"))
						.setHeader("dockerRequest").constant("image/list")
						.to("direct:requestdocker")
					.when(header("type").isEqualTo("containers"))
						.setHeader("dockerRequest").constant("container/list")
						.to("direct:requestdocker")
					.when(header("type").isEqualTo("info"))
						.setHeader("dockerRequest").constant("info")
						.to("direct:requestdocker")
					.when(header("type").isEqualTo("version"))
						.setHeader("dockerRequest").constant("version")
						.to("direct:requestdocker")
					.otherwise()
						.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
						    .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
						    .setBody().constant("Invalid Request")
				.end();
		
		// Call Docker
		from("direct:requestdocker")
			.recipientList(simple("docker://${header.dockerRequest}?host={{docker.server}}&port={{docker.port}}"));

		
		// Listen for Docker Events and invoke websocket connection
		from("docker://events?host={{docker.server}}&port={{docker.port}}")
				.log("${body}")
				.convertBodyTo(String.class)
				.to("ahc-ws://localhost:8080{{server.context-path}}/websocket?sendToAll=true");

	}

}

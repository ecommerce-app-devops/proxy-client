package com.selimhorri.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.selimhorri.app")
@ComponentScan(
	basePackages = "com.selimhorri.app",
	excludeFilters = {
		// Excluir SecurityConfig de user-service
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.selimhorri\\.app\\.config\\.security\\.SecurityConfig"),
		// Excluir todos los componentes de otros servicios (service, repository, domain, resource)
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.selimhorri\\.app\\.service\\..*"),
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.selimhorri\\.app\\.repository\\..*"),
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.selimhorri\\.app\\.domain\\..*"),
		// Excluir recursos de otros servicios - solo escanear los de proxy-client en business.*
		@ComponentScan.Filter(
			type = FilterType.REGEX, 
			pattern = "com\\.selimhorri\\.app\\.resource\\..*"
		),
		// Excluir cualquier clase llamada OrderController que no esté en business.order.controller
		@ComponentScan.Filter(
			type = FilterType.CUSTOM,
			classes = {ProxyClientApplication.ComponentExclusionFilter.class}
		)
	}
)
public class ProxyClientApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ProxyClientApplication.class, args);
	}
	
	// Filtro personalizado para excluir componentes de otros servicios
	static class ComponentExclusionFilter implements TypeFilter {
		@Override
		public boolean match(MetadataReader metadataReader,
				MetadataReaderFactory metadataReaderFactory) {
			String className = metadataReader.getClassMetadata().getClassName();
			// Excluir OrderController, UserController, etc. que no estén en business.*
			if (className.contains(".OrderController") && !className.contains(".business.order.controller")) {
				return true; // Excluir
			}
			if (className.contains(".UserController") && !className.contains(".business.user.controller")) {
				return true; // Excluir
			}
			if (className.contains(".ProductController") && !className.contains(".business.product.controller")) {
				return true; // Excluir
			}
			return false; // No excluir
		}
	}
	
}

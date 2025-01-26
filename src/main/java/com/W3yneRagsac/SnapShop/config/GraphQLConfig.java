package com.W3yneRagsac.SnapShop.config;

import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GraphQLConfig {
    @Bean
    public GraphQL graphQL() throws IOException {
        SchemaParser schemaParser = new SchemaParser();

        List<TypeDefinitionRegistry> typeRegistries = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource("GraphQL/queries/");

        File[] files = resource.getFile().listFiles((dir, name) -> name.endsWith(".graphqls"));
        for (File file : files) {
            TypeDefinitionRegistry typeRegistry = schemaParser.parse(new FileInputStream(file));
            typeRegistries.add(typeRegistry);
        }

        // Combine all the individual schemas
        TypeDefinitionRegistry combinedTypeRegistry = new TypeDefinitionRegistry();
        for (TypeDefinitionRegistry registry : typeRegistries) {
            combinedTypeRegistry.merge(registry);
        }

        // Create RuntimeWiring and GraphQLSchema
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .scalar(ExtendedScalars.DateTime)
                .build();

        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(combinedTypeRegistry, wiring);

        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.DateTime);
    }
}

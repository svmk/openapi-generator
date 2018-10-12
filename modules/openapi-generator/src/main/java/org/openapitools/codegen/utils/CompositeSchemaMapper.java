package org.openapitools.codegen.utils;

import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.openapitools.codegen.CodegenComposed;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenProperty;
import org.openapitools.codegen.CodegenConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompositeSchemaMapper {
    protected CodegenConfig codegenConfig;

    public CompositeSchemaMapper(CodegenConfig codegenConfig) {
        this.codegenConfig = codegenConfig;
    }

    protected List<CodegenModel> prepareComposedValues(List<Schema> childSchemas, Map<String, Schema> allDefinitions) {
        ArrayList<CodegenModel> result = new ArrayList();
        if (childSchemas != null) {
            int num = 0;
            for (Schema schema : childSchemas) {
                String name = String.valueOf(num);
                if (schema.get$ref() != null) {
                    String ref = ModelUtils.getSimpleRef(schema.get$ref());
                    if (ref != null && !ref.isEmpty() && allDefinitions.containsKey(ref)) {
                        schema = allDefinitions.get(ref);
                        name = ref;
                    }
                }
                result.add(codegenConfig.fromModel(name, schema, allDefinitions));
                num = num + 1;
            }
        }
        return result;
    }

    public void setValues(CodegenComposed model, ComposedSchema composedSchema, Map<String, Schema> allDefinitions) {
        List<CodegenModel> allOf = prepareComposedValues(composedSchema.getAllOf(), allDefinitions);
        model.setAllOf(allOf);
        model.setHasAllOf(!allOf.isEmpty());

        List<CodegenModel> oneOf = prepareComposedValues(composedSchema.getOneOf(), allDefinitions);
        model.setOneOf(oneOf);
        model.setHasOneOf(!oneOf.isEmpty());

        List<CodegenModel> anyOf = prepareComposedValues(composedSchema.getAnyOf(), allDefinitions);
        model.setAnyOf(anyOf);
        model.setHasAnyOf(!anyOf.isEmpty());
    }

    public void setProperties(String name, List<CodegenProperty> properties, Schema objectSchema, Map<String, Schema> allDefinitions) {
        for (CodegenProperty property: properties) {
            if (objectSchema.getProperties() != null) {
                Map<String, Schema> objectProperties = objectSchema.getProperties();
                Schema schema = objectProperties.get(property.getName());
                if (schema instanceof ComposedSchema) {
                    setValues(property, (ComposedSchema) schema, allDefinitions);
                }
            }
        }
    }
}

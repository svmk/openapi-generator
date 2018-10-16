/*
 * Copyright 2018 OpenAPI-Generator Contributors (https://openapi-generator.tech)
 * Copyright 2018 SmartBear Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openapitools.codegen.languages;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.openapitools.codegen.*;
import org.openapitools.codegen.utils.ModelUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StaticMarkdownGenerator extends DefaultCodegen implements CodegenConfig {
    protected String invokerPackage = "org.openapitools.client";
    protected String groupId = "org.openapitools";
    protected String artifactId = "openapi-client";
    protected String artifactVersion = "1.0.0";

    public StaticMarkdownGenerator() {
        super();
        embeddedTemplateDir = templateDir = "markdownDocs";
        modelTemplateFiles.put("model.mustache", ".md");
        apiTemplateFiles.put("operation.mustache", ".md");
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.DOCUMENTATION;
    }

    @Override
    public String getName() {
        return "markdown";
    }

    @Override
    public String getHelp() {
        return "Generates a directory with markdown files.";
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separator + File.separator + "operations";
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + File.separator + File.separator + "models";
    }

    @Override
    public CodegenProperty fromProperty(String name, Schema p) {
        CodegenProperty result = super.fromProperty(name, p);
        if (!result.isPrimitiveType && result.isNotContainer) {
            result.vendorExtensions.put("resolvedModelName", result.complexType);
        }
        return result;
    }

    @Override
    public CodegenParameter fromParameter(Parameter parameter, Set<String> imports)
    {
        CodegenParameter result = super.fromParameter(parameter, imports);
        if (!result.isPrimitiveType) {
            result.vendorExtensions.put("resolvedModelName", result.baseType);
        }
        return result;
    }

    @Override
    public String getTypeDeclaration(Schema p) {
        if (ModelUtils.isArraySchema(p)) {
            ArraySchema ap = (ArraySchema) p;
            Schema inner = ap.getItems();
            return "[]" + getTypeDeclaration(inner);
        } else if (ModelUtils.isMapSchema(p)) {
            Schema inner = ModelUtils.getAdditionalProperties(p);
            return "map[string]" + getTypeDeclaration(inner);
        }
        return super.getTypeDeclaration(p);
    }

    @Override
    public Map<String, Object> postProcessOperationsWithModels(Map<String, Object> objs, List<Object> allModels) {
        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        List<CodegenOperation> operationList = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation op : operationList) {
            op.httpMethod = op.httpMethod.toLowerCase(Locale.ROOT);
            for (CodegenResponse response : op.responses) {
                if ("0".equals(response.code)) {
                    response.code = "default";
                }
            }
        }
        return objs;
    }

}

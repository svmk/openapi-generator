package org.openapitools.codegen;

import java.util.List;

public interface CodegenComposed {
    List<CodegenModel> getAllOf();
    boolean isHasAllOf();
    void setHasAllOf(boolean hasAllOf);
    void setAllOf(List<CodegenModel> allOf);

    List<CodegenModel> getAnyOf();
    boolean isHasAnyOf();
    void setHasAnyOf(boolean hasAnyOf);
    void setAnyOf(List<CodegenModel> anyOf);

    List<CodegenModel> getOneOf();
    boolean isHasOneOf();
    void setHasOneOf(boolean hasOneOf);
    void setOneOf(List<CodegenModel> oneOf);
}

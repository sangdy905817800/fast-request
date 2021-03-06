package io.github.kings1990.plugin.fastrequest.generator.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import io.github.kings1990.plugin.fastrequest.config.Constant;
import io.github.kings1990.plugin.fastrequest.config.FastRequestComponent;
import io.github.kings1990.plugin.fastrequest.generator.FastUrlGenerator;
import io.github.kings1990.plugin.fastrequest.model.FastRequestConfiguration;
import io.github.kings1990.plugin.fastrequest.model.ParamGroup;
import io.github.kings1990.plugin.fastrequest.model.ParamNameType;
import io.github.kings1990.plugin.fastrequest.parse.BodyParamParse;
import io.github.kings1990.plugin.fastrequest.parse.PathValueParamParse;
import io.github.kings1990.plugin.fastrequest.parse.RequestParamParse;
import io.github.kings1990.plugin.fastrequest.util.UrlUtil;
import io.github.kings1990.plugin.fastrequest.view.CommonConfigView;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpringMethodUrlGenerator extends FastUrlGenerator {
    private FastRequestConfiguration config = FastRequestComponent.getInstance().getState();
    private static final Logger LOGGER = Logger.getInstance(CommonConfigView.class);
    private PathValueParamParse pathValueParamParse = new PathValueParamParse();
    private BodyParamParse bodyParamParse = new BodyParamParse();
    private RequestParamParse requestParamParse = new RequestParamParse();

    @Override
    public String generate(PsiElement psiElement) {
        String domain = config.getDomain();
        ParamGroup paramGroup = config.getParamGroup();
        if (!(psiElement instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }
        PsiMethod psiMethod = (PsiMethod) psiElement;
        String methodUrl = getMethodRequestMappingUrl(psiMethod);
        String classUrl = getClassRequestMappingUrl(psiMethod);
        String originUrl = classUrl + "/" + methodUrl;
        originUrl = originUrl.replace("//", "/");
        paramGroup.setOriginUrl(originUrl);

        List<ParamNameType> methodUrlParamList = getMethodUrlParamList(psiMethod);
        List<ParamNameType> methodBodyParamList = getMethodBodyParamList(psiMethod);

        //pathParam
        LinkedHashMap<String, Object> pathParamMap = pathValueParamParse.parseParam(config, methodUrlParamList);
//        methodUrl = buildPathParamUrl(methodUrl, pathParamMap);
//        String url = classUrl + "/" + methodUrl;
//        url = url.replace("//","/");
//        paramGroup.setUrl(url);


        //requestParam
        LinkedHashMap<String, Object> requestParamMap = requestParamParse.parseParam(config, methodUrlParamList);

        //bodyParam
        LinkedHashMap<String, Object> bodyParamMap = bodyParamParse.parseParam(config, methodBodyParamList);

        //methodType
        String methodType = getMethodType(psiMethod);

        paramGroup.setPathParamMap(pathParamMap);
        paramGroup.setRequestParamMap(requestParamMap);
        paramGroup.setBodyParamMap(bodyParamMap);
        paramGroup.setMethodType(methodType);
        return null;
    }


    private String buildPathParamUrl(String url, Map<String, Object> data) {
        List<String> paramNameList = UrlUtil.paramPathParam(url);
        if (paramNameList.isEmpty()) {
            return url;
        }
        for (String paramName : paramNameList) {
            Object value = data.get(paramName);
            if (value == null) {
                continue;
            }
            String paramNameWithSymbol = "{" + paramName + "}";
            url = url.replace(paramNameWithSymbol, value.toString());
        }
        return url;
    }


    @Override
    public String getMethodRequestMappingUrl(PsiMethod psiMethod) {
        Constant.SpringMappingConfig[] mappingConfigArray = Constant.SpringMappingConfig.values();
        PsiAnnotation annotationRequestMapping = null;
        for (Constant.SpringMappingConfig mappingConfig : mappingConfigArray) {
            String code = mappingConfig.getCode();
            annotationRequestMapping = psiMethod.getAnnotation(code);
            if (annotationRequestMapping != null) {
                break;
            }
        }
        if (annotationRequestMapping == null) {
            return StringUtils.EMPTY;
        }
        PsiAnnotationMemberValue value = annotationRequestMapping.findDeclaredAttributeValue("value");
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value.getText().replace("\"", "");
    }

    /**
     * ????????????mappingUrl
     * //@Controller("url") @RestController("url")
     *
     * @param psiMethod psi?????????
     * @return {@link String }
     * @author Kings
     * @date 2021/05/23
     */
    public String getClassRequestMappingUrl(PsiMethod psiMethod) {
        String mapping = Constant.SpringMappingConfig.REQUEST_MAPPING.getCode();
        PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            return StringUtils.EMPTY;
        }
        PsiAnnotation annotationMapping = containingClass.getAnnotation(mapping);;

        if (annotationMapping == null) {
            return StringUtils.EMPTY;
        }
        PsiAnnotationMemberValue value = annotationMapping.findDeclaredAttributeValue("value");
        if (value == null) {
            return StringUtils.EMPTY;
        }
        return value.getText().replace("\"", "");
    }

    public List<ParamNameType> getMethodBodyParamList(PsiMethod psiMethod) {
        List<ParamNameType> result = new ArrayList<>();
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        for (PsiParameter param : parameters) {
            PsiClass psiClass = PsiUtil.resolveClassInType(param.getType());
            PsiAnnotation annotation = param.getAnnotation(Constant.SpringUrlParamConfig.REQUEST_BODY.getCode());
            if (annotation != null) {
                ParamNameType paramNameType = new ParamNameType(param.getName(), param.getType().getCanonicalText(), psiClass, Constant.SpringUrlParamConfig.REQUEST_BODY.getParseType());
                result.add(paramNameType);
            }
        }
        return result;
    }

    @Override
    public List<ParamNameType> getMethodUrlParamList(PsiMethod psiMethod) {
        List<ParamNameType> result = new ArrayList<>();
        Constant.SpringUrlParamConfig[] urlParamConfigArray = Constant.SpringUrlParamConfig.values();
        PsiParameterList parameterList = psiMethod.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();

        for (PsiParameter param : parameters) {
            boolean parseFlag = false;
            PsiClass psiClass = PsiUtil.resolveClassInType(param.getType());
            for (Constant.SpringUrlParamConfig config : urlParamConfigArray) {
                PsiAnnotation annotation = param.getAnnotation(config.getCode());
                if (annotation != null) {
                    //PathVariable  RequestParam
                    Integer parseType = config.getParseType();
                    if (parseType == 1 || parseType == 2) {
                        ParamNameType paramNameType = new ParamNameType(param.getName(), param.getType().getCanonicalText(), psiClass, parseType);
                        result.add(paramNameType);
                        parseFlag = true;
                        break;
                    }
                    parseFlag = true;
                }
            }
            if (!parseFlag) {
                //????????????????????? requestParam
                ParamNameType paramNameType = new ParamNameType(param.getName(), param.getType().getCanonicalText(), psiClass, 2);
                result.add(paramNameType);
            }
        }
        return result;
    }

    private String getMethodType(PsiMethod psiMethod) {
        Constant.SpringMappingConfig[] mappingConfigs = Constant.SpringMappingConfig.values();
        for (Constant.SpringMappingConfig mappingConfig : mappingConfigs) {
            String code = mappingConfig.getCode();
            String methodType = mappingConfig.getMethodType();
            PsiAnnotation annotation = psiMethod.getAnnotation(code);
            if (annotation != null) {
                if (StringUtils.isNotEmpty(methodType)) {
                    return methodType;
                } else {
                    //RequestMapping
                    PsiAnnotationMemberValue method = annotation.findDeclaredAttributeValue("method");
                    if (method == null) {
                        //????????????GET
                        return "GET";
                    }
                    String methodText = method.getText();
                    String method0 = methodText.split(",")[0];
                    return method0.substring(method0.lastIndexOf(".") + 1);
                }

            }
        }
        return "GET";
    }


}

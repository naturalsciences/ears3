package eu.eurofleets.ears2.controller;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import org.jvnet.ws.wadl.Application;
import org.jvnet.ws.wadl.Doc;
import org.jvnet.ws.wadl.Param;
import org.jvnet.ws.wadl.ParamStyle;
import org.jvnet.ws.wadl.Representation;
import org.jvnet.ws.wadl.Request;
import org.jvnet.ws.wadl.Resource;
import org.jvnet.ws.wadl.Resources;
import org.jvnet.ws.wadl.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Controller
@RequestMapping({"/application.wadl"})
public class WADLController {

    String xs_namespace = "http://www.w3.org/2001/XMLSchema";
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @RequestMapping(method = {RequestMethod.GET}, produces = {"application/xml"})
    @ResponseBody
    public Application generateWadl(HttpServletRequest request) {
        Application result = new Application();
        Doc doc = new Doc();
        doc.setTitle("Ears2 List Web Service WADL");
        result.getDoc().add(doc);
        Resources wadResources = new Resources();
        wadResources.setBase(getBaseUrl(request));

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = this.handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {

            HandlerMethod handlerMethod = (HandlerMethod) entry.getValue();

            Object object = handlerMethod.getBean();
            Object bean = this.webApplicationContext.getBean(object.toString());

            boolean isRestContoller = bean.getClass().isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class);
            if (isRestContoller) {

                RequestMappingInfo mappingInfo = (RequestMappingInfo) entry.getKey();

                System.out.println(mappingInfo);

                Set<String> pattern = mappingInfo.getPatternsCondition().getPatterns();
                Set<RequestMethod> httpMethods = mappingInfo.getMethodsCondition().getMethods();
                ProducesRequestCondition producesRequestCondition = mappingInfo.getProducesCondition();
                Set<MediaType> mediaTypes = producesRequestCondition.getProducibleMediaTypes();
                Resource wadlResource = null;
                for (RequestMethod httpMethod : httpMethods) {
                    org.jvnet.ws.wadl.Method wadlMethod = new org.jvnet.ws.wadl.Method();

                    for (String uri : pattern) {
                        wadlResource = createOrFind(uri, wadResources);
                        wadlResource.setPath(uri);
                    }

                    wadlMethod.setName(httpMethod.name());
                    java.lang.reflect.Method javaMethod = handlerMethod.getMethod();
                    wadlMethod.setId(javaMethod.getName());
                    Doc wadlDocMethod = new Doc();
                    wadlDocMethod.setTitle(javaMethod.getDeclaringClass().getSimpleName() + "." + javaMethod.getName());
                    wadlMethod.getDoc().add(wadlDocMethod);

                    Request wadlRequest = new Request();

                    Annotation[][] annotations = javaMethod.getParameterAnnotations();
                    Class<?>[] paramTypes = javaMethod.getParameterTypes();
                    int i = 0;
                    for (Annotation[] annotation : annotations) {
                        Class<?> paramType = paramTypes[i];
                        i++;
                        for (Annotation annotation2 : annotation) {
                            if ((annotation2 instanceof RequestParam)) {
                                RequestParam param2 = (RequestParam) annotation2;
                                Param waldParam = new Param();
                                QName nm = convertJavaToXMLType(paramType);
                                waldParam.setName(param2.value());
                                waldParam.setStyle(ParamStyle.QUERY);
                                waldParam.setRequired(Boolean.valueOf(param2.required()));
                                String defaultValue = cleanDefault(param2.defaultValue());
                                if (!defaultValue.equals("")) {
                                    waldParam.setDefault(defaultValue);
                                }
                                waldParam.setType(nm);
                                wadlRequest.getParam().add(waldParam);
                            } else if ((annotation2 instanceof PathVariable)) {
                                PathVariable param2 = (PathVariable) annotation2;
                                QName nm = convertJavaToXMLType(paramType);
                                Param waldParam = new Param();
                                waldParam.setName(param2.value());
                                waldParam.setStyle(ParamStyle.TEMPLATE);
                                waldParam.setRequired(Boolean.valueOf(true));
                                wadlRequest.getParam().add(waldParam);
                                waldParam.setType(nm);
                            }
                        }
                    }
                    if (!wadlRequest.getParam().isEmpty()) {
                        wadlMethod.setRequest(wadlRequest);
                    }

                    if (!mediaTypes.isEmpty()) {
                        Response wadlResponse = new Response();
                        Class methodReturn = handlerMethod.getReturnType().getClass();
                        ResponseStatus status = (ResponseStatus) handlerMethod.getMethodAnnotation(ResponseStatus.class);
                        if (status == null) {
                            wadlResponse.getStatus().add(Long.valueOf(HttpStatus.OK.value()));
                        } else {
                            HttpStatus httpcode = status.value();
                            wadlResponse.getStatus().add(Long.valueOf(httpcode.value()));
                        }

                        for (MediaType mediaType : mediaTypes) {
                            Representation wadlRepresentation = new Representation();
                            wadlRepresentation.setMediaType(mediaType.toString());
                            wadlResponse.getRepresentation().add(wadlRepresentation);
                        }
                        wadlMethod.getResponse().add(wadlResponse);
                    }

                    wadlResource.getMethodOrResource().add(wadlMethod);
                }
            }
        }
        HandlerMethod handlerMethod;
        Set<String> pattern;
        Set<MediaType> mediaTypes;
        Resource wadlResource;
        result.getResources().add(wadResources);

        return result;
    }

    private QName convertJavaToXMLType(Class<?> type) {
        QName nm = new QName("");
        String classname = type.toString();
        if (classname.indexOf("String") >= 0) {
            nm = new QName(this.xs_namespace, "string", "xs");
        } else if (classname.indexOf("Integer") >= 0) {
            nm = new QName(this.xs_namespace, "int", "xs");
        }
        return nm;
    }

    private Resource createOrFind(String uri, Resources wadResources) {
        List<Resource> current = wadResources.getResource();
        for (Resource resource : current) {
            if (resource.getPath().equalsIgnoreCase(uri)) {
                return resource;
            }
        }
        Resource wadlResource = new Resource();
        current.add(wadlResource);
        return wadlResource;
    }

    private String getBaseUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + requestUri;
    }

    private String cleanDefault(String value) {
        value = value.replaceAll("\t", "");
        value = value.replaceAll("\n", "");
        return value;
    }
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/controller/WADLController.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */

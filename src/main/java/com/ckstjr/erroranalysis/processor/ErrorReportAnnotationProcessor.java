package com.ckstjr.erroranalysis.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Set;

public class ErrorReportAnnotationProcessor extends AbstractProcessor {

    private static final String ERROR_REPORT_ANNOTATION = "com.ckstjr.erroranalysis.aop.ErrorReport";
    private static final String EXCEPTION_HANDLER_ANNOTATION = "org.springframework.web.bind.annotation.ExceptionHandler";
    private static final String ERROR_REPORT_EXCLUDE_ANNOTATION = "com.ckstjr.erroranalysis.aop.ErrorReport.Exclude";

    private static final String ERROR_MESSAGE = "@ErrorReport가 적용된 클래스의 @ExceptionHandler 메서드는 Exception 타입 파라미터를 선언해야 합니다. " +
                                                "대상에서 제외하려면 @ErrorReport.Exclude 어노테이션을 사용하세요.";

    private TypeMirror exceptionType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.exceptionType = processingEnv.getElementUtils().getTypeElement("java.lang.Exception").asType();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(
                ERROR_REPORT_ANNOTATION,
                EXCEPTION_HANDLER_ANNOTATION
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement errorReportElement = processingEnv.getElementUtils().getTypeElement(ERROR_REPORT_ANNOTATION);
        if (errorReportElement == null) {
            return false;
        }

        Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(errorReportElement);
        for (Element element : annotatedClasses) {
            if (element instanceof TypeElement typeElement) {
                validateMethodsInClass(typeElement);
            }
        }

        return true;
    }

    private void validateMethodsInClass(TypeElement typeElement) {
        for (ExecutableElement method : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
            // 검증 대상인지 확인 (ExceptionHandler이면서 Exclude가 아닌 경우)
            // && Exception 파라미터가 없으면
            if (isTargetMethod(method) && !hasExceptionParameter(method)) {
                printCompileError(method);
            }
        }
    }

    private boolean isTargetMethod(ExecutableElement method) {
        return isAnnotatedWith(method, EXCEPTION_HANDLER_ANNOTATION)
            && !isAnnotatedWith(method, ERROR_REPORT_EXCLUDE_ANNOTATION);
    }

    private boolean isAnnotatedWith(Element element, String annotationName) {
        return element.getAnnotationMirrors().stream()
                .anyMatch(mirror -> mirror.getAnnotationType().toString().equals(annotationName));
    }

    private boolean hasExceptionParameter(ExecutableElement method) {
        return method.getParameters().stream()
            .anyMatch(parameter -> processingEnv.getTypeUtils().isAssignable(parameter.asType(), exceptionType));
    }

    private void printCompileError(Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, ERROR_MESSAGE, element);
    }
}

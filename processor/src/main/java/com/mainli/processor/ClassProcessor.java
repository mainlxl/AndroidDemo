package com.mainli.processor;

import com.mainli.annotations.BindView;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

//@AutoService(Processor.class)
public class ClassProcessor extends AbstractProcessor {
    /**
     * 被注解处理工具调用
     *
     * @param processingEnv 处理器环境
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    String PACK_NAME = "com.mainli.processor";

    /**
     * 处理方法注解方法
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("*******************************************************ClassProcessor.process11111111");
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            if (element.getKind() == ElementKind.FIELD) {
                //获取父类元素
                Element enclosingElement = element.getEnclosingElement();
                PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(enclosingElement);
                print("字段所在类-packName:" + packageOf.getQualifiedName());
                print("字段所在类-ClassName:" + enclosingElement.getSimpleName());
                //获取自己注解元素
                BindView annotation = element.getAnnotation(BindView.class);
                int id = annotation.value();
                print("注解-value:" + id);
                print(String.format("被注解对象名称:%s\n----------------------------------------------------------------------------", element.toString()));
            }
        }
        if (roundEnv.processingOver()) {
            JavaFile javaFile = JavaFile.builder(PACK_NAME, TypeSpec.classBuilder("Log").addJavadoc("生成$SLog类doc,Log类用于记录process的日志\n", PACK_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL).addField(FieldSpec.builder(String.class, "log").addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC).initializer("$S", mLog.toString()).build()).build()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                System.out.println("*******************************************************ClassProcessor.process");
                e.printStackTrace();
            }
            writeLog(javaFile.toString());
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        String canonicalName = BindView.class.getCanonicalName();
        print(String.format("生成Log类:%s.Log\n使用注解:%s\n----------------------------------------------------------------------------", PACK_NAME, canonicalName));
        return Collections.singleton(canonicalName);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private StringBuffer mLog = new StringBuffer();

    /**
     * 我的gradle Console一直报以下错误看不到日志故使用文件来记录日志
     * Caused by: java.io.IOException: CreateProcess error=2, 系统找不到指定的文件。
     * at java.lang.ProcessImpl.create(Native Method)
     * at java.lang.ProcessImpl.<init>(ProcessImpl.java:386)
     * at java.lang.ProcessImpl.start(ProcessImpl.java:137)
     * at java.lang.ProcessBuilder.start(ProcessBuilder.java:1029)
     * ... 87 more
     *
     * @param msg
     */
    public void print(String msg) {
        mLog.append(msg).append("\n\n\r");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,msg);
    }

    final String LOG_PATH = new File(".").getPath();

    public void writeLog(String str) {
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream = null;
        File file = new File(LOG_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.write("------------------------\n\r".getBytes());
            fileOutputStream.write(str.toString().getBytes());
            fileOutputStream.write("------------------------\n\r".getBytes());
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }
    }
}
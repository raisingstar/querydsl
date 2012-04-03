package com.mysema.query.apt;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.mysema.query.apt.hibernate.HibernateAnnotationProcessor;
import com.mysema.query.codegen.GenericExporter;
import com.mysema.query.codegen.Keywords;
import com.mysema.query.domain.AbstractEntityTest;

public class GenericExporterTest extends AbstractProcessorTest{
    
    private static final String PACKAGE_PATH = "src/test/java/com/mysema/query/domain/";
    
    private static final List<String> CLASSES = getFiles(PACKAGE_PATH);
    
    @Test // 2
    public void Execute() throws IOException {
        // via APT
        process(QuerydslAnnotationProcessor.class, CLASSES, "QuerydslAnnotationProcessor");
        
        // via GenericExporter
        GenericExporter exporter = new GenericExporter();
        exporter.setTargetFolder(new File("target/GenericExporterTest"));
        exporter.export(AbstractEntityTest.class.getPackage());
        
        List<String> expected = new ArrayList<String>();
        // delegates are not supported
        expected.add("QDelegateTest_SimpleUser.java");
        expected.add("QDelegateTest_SimpleUser2.java");
        expected.add("QDelegateTest_User.java");
        expected.add("QDelegate2Test_Entity.java");
        // projections are not supported
        expected.add("QQueryProjectionTest_EntityWithProjection.java");
        
        // FIXME
        expected.add("QExternalEntityTest_MyEntity.java");
        expected.add("QQueryEmbeddable2Test_User.java");
        expected.add("QSuperclass3Test_Subtype.java");
        expected.add("QExamples_OrderBys.java");
        
        execute(expected, "GenericExporterTest", "QuerydslAnnotationProcessor");
    }

    @Test // 1
    public void Execute2() throws IOException {
        // via APT
        process(HibernateAnnotationProcessor.class, CLASSES, "HibernateAnnotationProcessor");
        
        // via GenericExporter
        GenericExporter exporter = new GenericExporter();
        exporter.setKeywords(Keywords.JPA);
        exporter.setEntityAnnotation(Entity.class);
        exporter.setEmbeddableAnnotation(Embeddable.class);
        exporter.setEmbeddedAnnotation(Embedded.class);        
        exporter.setSupertypeAnnotation(MappedSuperclass.class);
        exporter.setSkipAnnotation(Transient.class);
        exporter.setTargetFolder(new File("target/GenericExporterTest2"));
        exporter.export(AbstractEntityTest.class.getPackage());
        
        List<String> expected = new ArrayList<String>();
        // projections are not supported
        expected.add("QQueryProjectionTest_EntityWithProjection.java");
        // GenericExporter doesn't include field/method selection
        expected.add("QFileAttachment.java");
        
        expected.add("QJodaTest_BaseEntity.java");
        
        // FIXME
        expected.add("QEntityInheritanceTest_TestEntity.java");
        
        execute(expected, "GenericExporterTest2", "HibernateAnnotationProcessor");
    }
    
    
    private void execute(List<String> expected, String genericExporterFolder, String aptFolder) throws IOException {
        List<String> failures = new ArrayList<String>();
        int successes = 0;
        for (File file : new File("target/"+genericExporterFolder+"/com/mysema/query/domain").listFiles()){
            File other = new File("target/"+aptFolder+"/com/mysema/query/domain", file.getName());
            if (!other.exists() || !other.isFile()) continue;
            String result1 = FileUtils.readFileToString(file, "UTF-8");
            String result2 = FileUtils.readFileToString(other, "UTF-8");
            if (!result1.equals(result2)){
                if (!expected.contains(file.getName())) {
                    System.err.println(file.getName());
                    failures.add(file.getName());    
                }                
            } else {
                successes++;
            }
        }
        if (!failures.isEmpty()){
            fail("Failed with " + failures.size() + " failures, " + successes + " succeeded");
        }
    }

}

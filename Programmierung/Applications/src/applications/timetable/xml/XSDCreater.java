/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.xml;

import applications.timetable.model.ProblemDefinition;
import java.io.File;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author bode
 */
public class XSDCreater {

    public static void main(String[] args) throws IOException, JAXBException {
//        createScheme(Problem.class, new File("/home/bode/Desktop"));
//        createScheme(TimeTableMatrix.class, new File("/home/bode/Desktop"));#
        createScheme(ProblemDefinition.class, new File("/home/bode/Desktop"));
    }
    
    public static void createScheme(Class<?> cls, File baseDir) throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(cls);
        context.generateSchema(new MySchemaOutputResolver(baseDir, cls.getSimpleName()+".xsd"));
    }

    static class MySchemaOutputResolver extends SchemaOutputResolver {
        File baseDir;
        String fileName;

        public MySchemaOutputResolver(File baseDir, String fileName) {
            this.baseDir = baseDir;
            this.fileName = fileName;
        }

        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
            return new StreamResult(new File(baseDir, fileName));
        }
    }
}

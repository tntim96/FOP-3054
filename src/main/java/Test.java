import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Test {

  public static void main(String[] args) throws Exception {
    InputStream xml = Test.class.getResourceAsStream("/somepath/data.xml");
    Source src = new StreamSource(xml);

    File file = File.createTempFile("testFile", ".pdf");
    System.out.println("file = " + file);
    OutputStream out = new FileOutputStream(file);

    Test test = new Test();
    InputStream xsltInputStream = Test.class.getResourceAsStream("/somepath/style.xslt");
    test.generateFopPdf(src, out, xsltInputStream);
  }


  public void generateFopPdf(Source src, OutputStream out, InputStream xsltInputStream) {
    try {
      ResourceResolver rr = ResourceResolverFactory
          .createTempAwareResourceResolver(new InMemoryTempResourceResolver(), new CustomResolver());

      FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(URI.create("https://www.fop.com.au/"), rr);

      DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
      Configuration config = cfgBuilder.build(this.getClass().getResourceAsStream("/somepath/fop-config.xml"));

      fopFactoryBuilder.setConfiguration(config);

      FopFactory fopFactory = fopFactoryBuilder.build();
      fopFactory.getFontManager().disableFontCache();
      // configure fopFactory as desired

      FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
      // configure foUserAgent as desired

      // Construct fop with desired output format
      Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

      // Using the Saxon implementation directly because this is the only point at which I care about the particular Transformer
      // implementation and I don't want to change the default for all other methods using the transformer factory
      TransformerFactory factory = new TransformerFactoryImpl();
      Transformer transformer = factory.newTransformer(new StreamSource(xsltInputStream));

      // Resulting SAX events (the generated FO) must be piped through to FOP
      Result res = new SAXResult(fop.getDefaultHandler());

      // Start XSLT transformation and FOP processing-
      transformer.transform(src, res);
    } catch (TransformerException | SAXException | ConfigurationException e) {
      e.printStackTrace(System.err);
    }
  }


  class InMemoryTempResourceResolver implements TempResourceResolver {

    private final Map<String, ByteArrayOutputStream> tempBaos = Collections.synchronizedMap(new HashMap<>());


    public OutputStream getOutputStream(String id) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      tempBaos.put(id, baos);
      return baos;
    }


    public org.apache.xmlgraphics.io.Resource getResource(final String id) {
      if (!tempBaos.containsKey(id)) {
        throw new IllegalArgumentException("Temp resource with id = " + id
            + " does not exist");
      }
      return new org.apache.xmlgraphics.io.Resource(new ByteArrayInputStream(tempBaos.remove(id).toByteArray()));
    }
  }
}

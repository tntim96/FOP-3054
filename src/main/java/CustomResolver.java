import org.apache.xmlgraphics.io.ResourceResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;


public class CustomResolver implements ResourceResolver {



  public org.apache.xmlgraphics.io.Resource getResource(URI uri) throws IOException {
    if (uri.toString().startsWith("classpath:")) {
      String actualPath = uri.toString().substring("classpath:".length());
      InputStream resourceAsStream = this.getClass().getResourceAsStream(actualPath);
      if (resourceAsStream != null) {
        return new org.apache.xmlgraphics.io.Resource(resourceAsStream);
      } else {
        throw new IllegalStateException("Unable to find classpath resource: " + actualPath);
      }
    } else {
      return new org.apache.xmlgraphics.io.Resource(uri.toURL().openStream());
    }
  }


  public OutputStream getOutputStream(URI uri) throws IOException {
    return new FileOutputStream(new File(uri));
  }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.ontology;

/**
 *
 * @author Thomas Vandenberghe
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

/**
 * This example shows how to upload files using POST requests with encoding type
 * "multipart/form-data". For more details please read the full tutorial on
 * https://javatutorial.net/java-file-upload-rest-service
 *
 * @author javatutorial.net
 */
public class FileUploaderClient {

    public static void main(String[] args) {

        // the file we want to upload
        File inFile = new File("/home/thomas/NetBeansProjects/PlatformEARS/build/testuserdir/config/onto/earsv2-onto-vessel.rdf");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inFile);
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

            // server back-end URL
            HttpPost httppost = new HttpPost("http://localhost:8080/ears2Ont/uploadVesselOntology");
            MultipartEntity entity = new MultipartEntity();
            // set the file input stream and file name as arguments
            entity.addPart("file", new InputStreamBody(fis, inFile.getName()));
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials("ears", "REPLACEME");
            httppost.addHeader(new BasicScheme(StandardCharsets.UTF_8).authenticate(creds, httppost, null));
            httppost.removeHeaders("Content-Disposition");
            httppost.removeHeaders("Content-Transfer-Encoding");
            httppost.removeHeaders("Content-Type");
            httppost.setEntity(entity);
            // execute the request
            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");

            System.out.println("[" + statusCode + "] " + responseString);

        } catch (ClientProtocolException e) {
            System.err.println("Unable to make connection");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Unable to read file");
            e.printStackTrace();

        } catch (AuthenticationException e) {
            System.err.println("Unable to authenticate");
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
        }
    }

}

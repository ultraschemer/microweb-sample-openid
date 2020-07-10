package microweb.sample.controller;

import com.ultraschemer.microweb.entity.User;
import com.ultraschemer.microweb.error.StandardException;
import com.ultraschemer.microweb.persistence.EntityUtil;
import com.ultraschemer.microweb.vertx.SimpleController;
import freemarker.template.Template;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import microweb.sample.domain.ImageManagement;
import microweb.sample.domain.bean.ImageRegistrationData;
import microweb.sample.view.FtlHelper;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.util.Set;
import java.util.UUID;

public class GuiImageCreationController extends SimpleController {
    private static Template homePageTemplate = null;

    static {
        try {
            homePageTemplate = FtlHelper.getConfiguration().getTemplate("homePage.ftl");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public GuiImageCreationController() {
        super(500, "98473a7b-3fd6-4ef3-b1b7-b53210f7a75b");
    }

    @Override
    public void executeEvaluation(RoutingContext context, HttpServerResponse response) throws Throwable {
        Set<FileUpload> uploads = context.fileUploads();

        if(uploads.size()>1) {
            // Delete all received files:
            for(FileUpload f : uploads) {
                new File(f.uploadedFileName()).delete();
            }

            throw new ValidationException("Only one file is expected.");
        }

        // Process upload:
        for (FileUpload f : uploads) {
            // Verify file extension:
            if(!f.fileName().toLowerCase().matches("^.*\\.(jpg|jpeg|png|bmp|tiff|svg|ico|gif|webp)$")) {
                // Delete file:
                new File(f.uploadedFileName()).delete();
                throw new ValidationException("Unexpected format for an image. Use files with these extensions: jpg, jpeg, png, bmp, tiff, svg, ico, gif or webp.");
            }

            //
            // Save the file in database, asynchronously:
            //

            // Create the business rule class, injecting the structural dependencies (Hibernate and Vertx):
            ImageManagement imageManagement = new ImageManagement(EntityUtil.getSessionFactory(), context.vertx());

            // Create the input bean:
            User u = context.get("user");
            ImageRegistrationData imageRegistrationData = new ImageRegistrationData();
            imageRegistrationData.setUserId(u.getId());
            imageRegistrationData.setImageFileName(f.uploadedFileName());

            // Replaces the file name by given name, adding the extension:
            String [] fileNameParts = f.fileName().split("\\.");
            imageRegistrationData.setName(context.request().getFormAttribute("fileName") +
                    "." + fileNameParts[fileNameParts.length-1]);

            // Save file:
            imageManagement.save(imageRegistrationData, (UUID uuid, StandardException e) -> {
                // Process results using default method asyncEvaluation, which treats any error
                // and ensures HTTP evaluation finalization:
                asyncEvaluation(500, "d7056121-3bf2-4f72-92f4-7b0435954572", context, () -> {
                    // Delete processed file:
                    new File(f.uploadedFileName()).delete();

                    // Raises exception, in the case of error - Microweb will deal with it suitably:
                    if(e != null) {
                        throw e;
                    }

                    // Redirect home:
                    response.putHeader("Content-type", "text/html")
                            .putHeader("Location", "/v0")
                            .setStatusCode(303)
                            .end();
                });
            });
        }
    }
}

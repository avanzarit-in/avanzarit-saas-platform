import com.avanzarit.platform.saas.aws.s3.S3Item;
import com.avanzarit.platform.saas.aws.s3.S3ItemBuilder;
import com.avanzarit.platform.saas.aws.s3.S3KeyGenerator;
import com.avanzarit.platform.saas.aws.s3.S3ObjectMetadata;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Sample {
    public static void main(String[] args) throws Exception {
        S3ItemBuilder<File> s3ItemBuilder = new S3ItemBuilder<>();
        try {
            S3Item s3Item = s3ItemBuilder
                    .withS3Metadata(new S3ObjectMetadata<File>(new File("")) {
                        @Override
                        public Map<String, String> getS3ObjectMetadata() {
                            return new HashMap<>();
                        }
                    })
                    .withS3KeyGenerator(new S3KeyGenerator<File>() {
                        @Override
                        public String generateKey() {
                            return "dfdfd";
                        }
                    })

                    .build().orElseThrow(Exception::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    S3Manager manager=new S3Manager(S3Constants.S3_CLIENT);
        //    manager.writeItem("sample",s3Item);

        //manager.readPage()
    }
}

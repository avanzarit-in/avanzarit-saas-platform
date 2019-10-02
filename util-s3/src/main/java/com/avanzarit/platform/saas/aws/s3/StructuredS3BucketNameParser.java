package com.avanzarit.platform.saas.aws.s3;

import com.avanzarit.platform.saas.aws.util.CmwContext;

/**
 * StructuredTableNameParser is a class that can be used for parsing table names in String format to the more structured
 * {@link StructuredS3BucketName} representation. Next to being able to parse table names it is also capable of creating
 * unstructured String table names based on structured content.
 */
public class StructuredS3BucketNameParser {
    private static final String SPLIT_CHARACTER = "_";

    /**
     * Parses the given table name into a structured representation, extracting the project, the
     * environment, the layer and the table. It supports both table names consisting of four and of
     * five parts.
     * <p>
     * In case the table name consists of five parts instead of four, the parser assumes that the
     * layer contains an underscore ('_') character and it will concatenate the 3th and the 4th
     * part using an underscore ('_') character.
     *
     * @return A structured representation of the table name.
     * <p>
     * Example 1 (four-part name):<br/>
     * Input: mw_develop_canonical_product<br/>
     * Output: A structured table name object with the following values:
     * <ul>
     * <li>project: "mw"</li>
     * <li>environment: "develop"</li>
     * <li>layer: "canonical"</li>
     * <li>table: "product"</li>
     * </ul>
     * <p>
     * Example 2 (five-part name):<br/>
     * Input: mw_develop_ser_cq5_product<br/>
     * Output: A structured table name object with the following values:
     * <ul>
     * <li>project: "mw"</li>
     * <li>environment: "develop"</li>
     * <li>layer: "ser_cq5"</li>
     * <li>table: "product"</li>
     * </ul>
     */
    public StructuredS3BucketName parse(String tableName) {
        StructuredS3BucketName result = null;
        if (tableName != null) {
            String[] split = tableName.split(SPLIT_CHARACTER);
            if (split.length == 4) {
                result = new StructuredS3BucketName();
                result.setProject(split[0]);
                result.setEnvironment(split[1]);
                result.setLayer(split[2]);
                result.setBucket(split[3]);
            }
            //serializers
            if (split.length == 5) {
                result = new StructuredS3BucketName();
                result.setProject(split[0]);
                result.setEnvironment(split[1]);
                result.setLayer(split[2] + SPLIT_CHARACTER + split[3]);
                result.setBucket(split[4]);
            }
        }
        return result;
    }

    /**
     * Generates a table name using the given parts.
     *
     * @return A concatenation of all the parts using an underscore ('_') as the separator. When
     * invoked like:
     * <p>
     * {@code createTableName("part1", "part2", "part3", "...", "partN")}
     * <p>
     * The output will be "part1_part2_part3_..._partN".
     */
    public String createTableName(String... parts) {
        StringBuffer result = new StringBuffer("");
        if (parts != null) {
            for (String part : parts) {
                if (!result.toString().equals("")) {
                    result.append("_");
                }
                result.append(part);
            }
        }
        return result.toString();
    }

    /**
     * Generates a table name based on the given {@link CmwContext} instance and suffix.
     *
     * @return A concatenation of the prefix, layer fields of the {@link CmwContext} object and the
     * given suffix. The output will look like "prefix_layer_suffix".
     */
    public String createTableName(CmwContext cmwContext, String suffix) {
        return createTableName(
                cmwContext.getPrefix(),
                cmwContext.getLayer(),
                suffix
        );
    }

    /**
     * Generates a table name based on the given {@link CmwContext} instance.
     *
     * @return A concatenation of the prefix, layer and component fields contained in the
     * {@link CmwContext} object. The output will look like "prefix_layer_component".
     */
    public String createTableName(CmwContext cmwContext) {
        return createTableName(
                cmwContext.getPrefix(),
                cmwContext.getLayer(),
                cmwContext.getComponent()
        );
    }
}

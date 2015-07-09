package cn.momia.common.secret;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordEncryptorTest {
    @Test
    public void testEncrypt() {
        String mobile = "13918872284";
        String password = "qiufeng!@123Wu?";
        String secretKey = "jnv923opvFGUekfke23iomek!@@#02oidmopz3";

        String encryptedPassword = PasswordEncryptor.encrypt(mobile, password, secretKey);
        Assert.assertEquals(encryptedPassword, "9XCjB+hLjmUHc2UX6/gBrQ==");
    }
}

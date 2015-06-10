package cn.momia.common.secret;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordEncryptorTest {
    @Test
    public void testEncrypt() {
        String salt = "qiufeng.wu";
        String password = "qiufeng!@123Wu?";

        String encryptedPassword = PasswordEncryptor.encrypt(salt, password);
        Assert.assertEquals(encryptedPassword, "hiqC+hr1lIhbtVSzIVBBZw==");
    }
}

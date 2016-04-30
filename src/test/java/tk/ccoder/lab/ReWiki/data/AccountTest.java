package tk.ccoder.lab.ReWiki.data;

import tk.ccoder.lab.ReWiki.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AccountTest {
  @Test
  public void validation() {
    Account target = new Account("test", "test@example.com");
    target.setPassword("ThisIsAVeryLongPassword");
    assertTrue("Same password should be valid", target.validatePassword("ThisIsAVeryLongPassword"));
    assertFalse("Wrong password are 'wrong'", target.validatePassword("ThisIsVeryLongPassword"));
  }
}
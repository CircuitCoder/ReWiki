package tk.ccoder.lab.ReWiki.data;

import tk.ccoder.lab.ReWiki.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Every.everyItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by CircuitCoder on 4/30/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RevisionTest {
  @Test
  public void blame() {
    Account a = new Account("a", "a@example.com");
    Account b = new Account("b", "b@example.com");

    String content1 = "# Some title\r\n" +
            "> Blockqote\r\n" +
            "\r\n" +
            "- 1\r\n" +
            "- 2";
    String content2 = "# Some title\r\n" +
            "> BBBBlockquote\r\n" +
            "\r\n" +
            "- 1\r\n" +
            "- 1.5\r\n" +
            "- 2\r\n";

    Revision rev1 = new Revision(null, a, content1, "Initial");
    Revision rev2 = new Revision(rev1, b, content2, "Modify");

    List<Revision.Blame> blame1 = rev1.getBlame();
    assertEquals("Rev1 should has 1 blame record", 1, rev1.getBlame().size());
    assertEquals("That blame should start with 0", 0, blame1.get(0).start);
    assertEquals("That blame should have a size of 5", 5, blame1.get(0).length);
    assertEquals("That blame should be referenced to current commit", rev1.getRef(), blame1.get(0).ref);
    assertEquals("That blame should be committed by author a", a, blame1.get(0).author);

    List<Revision.Blame> blame2 = rev2.getBlame();
    assertEquals("Rev2 should has 5 blames", blame2.size(), 5);
    assertThat("The first, third and fifth should be committed by author a", Arrays.asList(
            blame2.get(0).author,
            blame2.get(2).author,
            blame2.get(4).author
    ), everyItem(equalTo(a)));

    assertThat("The second and forth should be committed by author b", Arrays.asList(
            blame2.get(1).author,
            blame2.get(3).author
    ), everyItem(equalTo(b)));

    assertEquals("First blame has a length of 1", blame2.get(0).length, 1);
    assertEquals("Second blame has a length of 1", blame2.get(1).length, 1);
    assertEquals("Third blame has a length of 2", blame2.get(2).length, 2);
    assertEquals("Forth blame has a length of 1", blame2.get(3).length, 1);
    assertEquals("Fifth blame has a length of 1", blame2.get(4).length, 1);

    assertEquals("Last blame starts at 5", blame2.get(4).start, 5);
  }
}

package tk.ccoder.lab.ReWiki.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by CircuitCoder on 4/29/16.
 */

public class Account {
  @Id
  private String id;

  @Indexed(unique = true)

  private String email;

  private String password;

  public Account(String id, String email) {
    this.id = id;
    this.email = email;
  }

  public void setPassword(String password) {
    try {
      this.password = this.hashPassword(password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean validatePassword(String password) {
    try {
      return this.password == this.hashPassword(password);
    } catch(Exception e) {
      return false;
    }
  }

  private String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    String raw = password + id;
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(raw.getBytes("UTF-8"));
    return DatatypeConverter.printHexBinary(md.digest());
  }
}

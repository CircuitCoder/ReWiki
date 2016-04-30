package tk.ccoder.lab.ReWiki.data;

/**
 * Created by CircuitCoder on 4/29/16.
 */
public enum EntryLang {
  en("English Int.","EN"), zh_CN("简体中文","CN"), zh_TW("正體中文","TW");

  private String name;
  private String abbr;

  EntryLang(String name, String abbr) {
    this.name = name;
    this.abbr = abbr;
  }

  public String getName() {
    return name;
  }

  public String getAbbr() {
    return abbr;
  }
}

package com.pippsford.beantester.factories.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pippsford.beantester.TestContext;
import com.pippsford.beantester.ValueType;
import org.junit.jupiter.api.Test;

class StringValueFactoryTest {

  @Test
  void test() {
    TestContext.get().setRepeatable(StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "MHh-geG4_H",
        ".rt46O",
        "wa_.P4NW",
        "kaJEeaDS",
        "Z_EkARw",
        "yFU",
        "ύṏञȫґҺ(l",
        "*@mhA2CS#\\",
        "ṰŻṪĖӣợÁ",
        "9fOa#k6",
        "ῷⱩК",
        "FhfYhlw49-",
        "Zjg{^6",
        "5ZyXbA",
        "jcNSDso",
        "JLpbulLo0V",
        "\\nHRp#!",
        ":)'cgQ(8C>",
        "ŏƾधĀ",
        "©4Ôt",
        "ar3oJs",
        "CyvhY Nk",
        "67x",
        "37r>E_",
        "ӞЎӱḉ",
        "Obtuse Embezzle Sweatband",
        "'ѡЪƺᾹῈѱẙ",
        "Billiard",
        "UFj_81jZVD",
        "s³ÒÑ]ÒpEs",
        "KO\\bY",
        "N-uJK",
        "rúÐßÊÀSR",
        "Ò0Å",
        "oŠµ@",
        "ze¯",
        "Miser Onlooker Indulge",
        "GyOX",
        "Cubic Supportive",
        "n+s%#Xw",
        "IslW",
        "O=8kJ",
        "ūὮЈƃß",
        "ñö?kN",
        "Payday Impartial",
        "}7B$ws6wWM",
        "PhyHylvyR",
        "z$=6",
        "Pxifv",
        "gIwmlNcI"
    };
    for (int i = 0; i < 50; i++) {
      assertEquals(expected[i], factory.create(ValueType.RANDOM));
    }
  }

  @Test
  void alphanumeric() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "HguEDKDl",
        "ydh3t",
        "9K7nY",
        "I4PtUdBU",
        "EdJ",
        "EI8SLmAsV",
        "hLvS",
        "3SFPDOL",
        "zkPpnDK",
        "TCIoKqMoD"
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.ALPHANUMERIC.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }


  @Test
  void ascii() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "ua=:Hjts",
        "@i#o}",
        "t@J~r",
        "5\\)U;IUu",
        "d1<",
        "*o7$Auy-L",
        "j~=S",
        "?[PBai~",
        ",~mqNiR",
        "fr<;@-|zK"
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.ASCII.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }


  @Test
  void iso8859() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "ÇžÔ3.¶ñ»",
        "ò.šÇ·",
        "Û;4|m",
        "7YŠéinRÆ",
        "m,÷",
        "öìºÊãO'êª",
        "ó$,o",
        "¿9Ù*fÒý",
        "³aÉ.Ïfô",
        "LÎŒøQ?Ž%l"
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.ISO8859.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }

  @Test
  void pgpWords() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "Clamshell Ultimate",
        "Chopper",
        "Crumpled Publisher Inverse",
        "Exceed Aftermath Sawdust",
        "Stockman Gadgetry Lockup",
        "Tracker Retraction",
        "Quadrant",
        "Guidance Vocalist",
        "Classroom Bradbury Mohawk",
        "Bison Embezzle"
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.PGP_WORDS.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }


  @Test
  void unicode() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "ḖϾÂĆΠΪΐң",
        "áⱩẉὩп",
        "ύđȀȉȏ",
        "фẇԓὕḞ॥-;",
        "¶ȹÉ",
        "ԙдκỏҡ.ͼҶỤ",
        "ẴФẅΈ",
        "ïỚἴữῦῊẻ",
        "PἤṷẛॻBЫ",
        "ϾҺựԠẕԗǨҷẰ"
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.UNICODE.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }

  @Test
  void uppercase() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "1YE4ZOBP",
        "0VVX9",
        "HGTPM",
        "GQ57YFBK",
        "29H",
        "CMO4TW805",
        "P1T4",
        "XUZR30V",
        "PY3B9NY",
        "LS6QY8AI1",
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.UPPERCASE.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }


  @Test
  void urlSafe() {
    TestContext.get().setRepeatable(1 + StringValueFactory.class.getName().hashCode());
    StringValueFactory factory = new StringValueFactory();
    String[] expected = {
        "ZECKnG3B",
        "cZBPV",
        "b8FjE",
        "WUtHWPnk",
        "qPt",
        "4mi8Xwein",
        "vNhs",
        "ruRxDcp",
        "dEln~zQ",
        "JgAemOsMN",
    };
    for (int i = 0; i < 10; i++) {
      String s = (String) StringValueFactory.URL_SAFE.create(ValueType.RANDOM);
      assertEquals(expected[i], s);
    }
  }

}

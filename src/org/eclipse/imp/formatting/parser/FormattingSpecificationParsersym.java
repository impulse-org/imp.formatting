package org.eclipse.imp.formatting.parser;

public interface FormattingSpecificationParsersym {
    public final static int
      TK_begin_formatter = 2,
      TK_end_formatter = 3,
      TK_begin_language = 4,
      TK_end_language = 5,
      TK_begin_rules = 6,
      TK_end_rules = 7,
      TK_begin_rule = 8,
      TK_end_rule = 9,
      TK_begin_box = 10,
      TK_end_box = 11,
      TK_begin_example = 12,
      TK_end_example = 13,
      TK_IDENTIFIER = 14,
      TK_any = 1,
      TK_EOF_TOKEN = 15,
      TK_ERROR_TOKEN = 16;

      public final static String orderedTerminalSymbols[] = {
                 "",
                 "any",
                 "begin_formatter",
                 "end_formatter",
                 "begin_language",
                 "end_language",
                 "begin_rules",
                 "end_rules",
                 "begin_rule",
                 "end_rule",
                 "begin_box",
                 "end_box",
                 "begin_example",
                 "end_example",
                 "IDENTIFIER",
                 "EOF_TOKEN",
                 "ERROR_TOKEN"
             };

    public final static boolean isValidForParser = true;
}

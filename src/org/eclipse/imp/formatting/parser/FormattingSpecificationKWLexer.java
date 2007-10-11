package org.eclipse.imp.formatting.parser;

import lpg.runtime.*;

public class FormattingSpecificationKWLexer extends FormattingSpecificationKWLexerprs implements FormattingSpecificationParsersym
{
    private char[] inputChars;
    private final int keywordKind[] = new int[12 + 1];

    public int[] getKeywordKinds() { return keywordKind; }

    public int lexer(int curtok, int lasttok)
    {
        int current_kind = getKind(inputChars[curtok]),
            act;

        for (act = tAction(START_STATE, current_kind);
             act > NUM_RULES && act < ACCEPT_ACTION;
             act = tAction(act, current_kind))
        {
            curtok++;
            current_kind = (curtok > lasttok
                                   ? Char_EOF
                                   : getKind(inputChars[curtok]));
        }

        if (act > ERROR_ACTION)
        {
            curtok++;
            act -= ERROR_ACTION;
        }

        return keywordKind[act == ERROR_ACTION  || curtok <= lasttok ? 0 : act];
    }

    public void setInputChars(char[] inputChars) { this.inputChars = inputChars; }


    //
    // Each upper case letter is mapped into is corresponding
    // lower case counterpart. For example, if an 'A' appears
    // in the input, it is mapped into Char_a just like 'a'.
    //
    final static int tokenKind[] = new int[128];
    static
    {
        tokenKind['$'] = Char_DollarSign;
        tokenKind['_'] = Char__;

        tokenKind['a'] = Char_a;
        tokenKind['b'] = Char_b;
        tokenKind['c'] = Char_c;
        tokenKind['d'] = Char_d;
        tokenKind['e'] = Char_e;
        tokenKind['f'] = Char_f;
        tokenKind['g'] = Char_g;
        tokenKind['h'] = Char_h;
        tokenKind['i'] = Char_i;
        tokenKind['j'] = Char_j;
        tokenKind['k'] = Char_k;
        tokenKind['l'] = Char_l;
        tokenKind['m'] = Char_m;
        tokenKind['n'] = Char_n;
        tokenKind['o'] = Char_o;
        tokenKind['p'] = Char_p;
        tokenKind['q'] = Char_q;
        tokenKind['r'] = Char_r;
        tokenKind['s'] = Char_s;
        tokenKind['t'] = Char_t;
        tokenKind['u'] = Char_u;
        tokenKind['v'] = Char_v;
        tokenKind['w'] = Char_w;
        tokenKind['x'] = Char_x;
        tokenKind['y'] = Char_y;
        tokenKind['z'] = Char_z;
    };

    final int getKind(char c)
    {
        return (c < 128 ? tokenKind[c] : 0);
    }


    public FormattingSpecificationKWLexer(char[] inputChars, int identifierKind)
    {
        this.inputChars = inputChars;
        keywordKind[0] = identifierKind;

        //
        // Rule 1:  Keyword ::= < f o r m a t t e r >
        //
        keywordKind[1] = (TK_begin_formatter);
      
    
        //
        // Rule 2:  Keyword ::= < / f o r m a t t e r >
        //
        keywordKind[2] = (TK_end_formatter);
      
    
        //
        // Rule 3:  Keyword ::= < l a n g u a g e >
        //
        keywordKind[3] = (TK_begin_language);
      
    
        //
        // Rule 4:  Keyword ::= < / l a n g u a g e >
        //
        keywordKind[4] = (TK_end_language);
      
    
        //
        // Rule 5:  Keyword ::= < r u l e s >
        //
        keywordKind[5] = (TK_begin_rules);
      
    
        //
        // Rule 6:  Keyword ::= < / r u l e s >
        //
        keywordKind[6] = (TK_end_rules);
      
    
        //
        // Rule 7:  Keyword ::= < r u l e >
        //
        keywordKind[7] = (TK_begin_rule);
      
    
        //
        // Rule 8:  Keyword ::= < / r u l e >
        //
        keywordKind[8] = (TK_end_rule);
      
    
        //
        // Rule 9:  Keyword ::= < b o x >
        //
        keywordKind[9] = (TK_begin_box);
      
    
        //
        // Rule 10:  Keyword ::= < / b o x >
        //
        keywordKind[10] = (TK_end_box);
      
    
        //
        // Rule 11:  Keyword ::= < e x a m p l e >
        //
        keywordKind[11] = (TK_begin_box);
      
    
        //
        // Rule 12:  Keyword ::= < / e x a m p l e >
        //
        keywordKind[12] = (TK_end_box);
      
    

        for (int i = 0; i < keywordKind.length; i++)
        {
            if (keywordKind[i] == 0)
                keywordKind[i] = identifierKind;
        }
    }
}


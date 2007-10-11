package org.eclipse.imp.formatting.parser;

public class FormattingSpecificationLexerprs implements lpg.runtime.ParseTable, FormattingSpecificationLexersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            9,4,8,15,16,17,18,19,20,21,
            22,23,24,25,26,27,28,29,30,31,
            32,33,34,35,36,37,38,39,40,11,
            12,13,2,3,5,6,7,10,14,42,
            1,41
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,2,2,
            1,2,2,1,2,1,2,2,2,3,
            3,1,1,2,2,3,1,2,2,2,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            33,33,33,33,33,33,33,33,33,33,
            33,33,33,33,33,33,33,33,33,33,
            33,34,34,34,2,2,35,35,35,30,
            30,30,31,31,38,38,38,36,36,37,
            37,1,1,1,1,1,1,1,1,1,
            1,4,4,5,5,6,6,7,7,8,
            8,9,9,10,10,11,11,12,12,13,
            13,14,14,15,15,16,16,17,17,18,
            18,19,19,20,20,21,21,22,22,23,
            23,24,24,25,25,26,26,27,27,28,
            28,29,29,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3,42,
            42,42,42,32,32,32,32,32,40,40,
            40,40,40,40,40,40,40,40,40,40,
            40,40,40,40,40,40,40,40,40,40,
            40,40,40,40,40,40,40,40,40,40,
            39,39,39,39,39,39,101,24,286,21,
            103,104,105,106,107,108,109,110,111,112,
            113,114,115,116,117,118,119,120,121,122,
            123,124,125,126,127,128,285,25,37,217,
            256,3,296,215,258,1,171,307,170,103,
            104,105,106,107,108,109,110,111,112,113,
            114,115,116,117,118,119,120,121,122,123,
            124,125,126,127,128,274,24,206,263,24,
            297,296,24,299,40,172,184,23,366,22,
            103,104,105,106,107,108,109,110,111,112,
            113,114,115,116,117,118,119,120,121,122,
            123,124,125,126,127,128,250,25,27,248,
            274,24,301,274,24,303,315,318,25,333,
            25,344,25,355,25,367,369,309,331,331,
            331,331,331,331,331,331,28,248,331,331,
            331,331,331,331,331,331,331,331,38,331,
            331
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,36,37,38,39,
            40,41,42,43,44,45,46,47,48,49,
            50,51,52,53,54,55,56,57,58,59,
            60,61,62,63,64,65,66,67,68,69,
            70,0,0,73,74,75,76,77,78,79,
            80,81,82,83,84,85,86,87,88,89,
            90,91,92,93,94,95,96,97,98,99,
            0,1,2,3,4,5,6,7,8,9,
            10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,36,37,38,39,
            40,41,42,43,44,45,46,47,48,49,
            50,51,52,53,54,55,56,57,58,59,
            60,61,62,63,64,65,66,67,68,69,
            70,71,72,73,74,75,76,77,78,79,
            80,81,82,0,1,2,3,4,5,6,
            7,8,9,10,11,12,0,0,0,16,
            17,18,19,20,21,22,23,24,25,26,
            27,28,29,30,31,32,33,34,35,36,
            37,38,39,40,41,42,43,44,45,46,
            47,48,49,50,51,52,53,54,55,56,
            57,58,59,60,61,62,63,64,65,0,
            1,2,3,4,5,6,7,8,9,10,
            11,12,0,1,2,3,4,5,6,7,
            8,9,10,0,1,2,3,4,5,6,
            7,8,9,10,0,1,2,3,4,5,
            6,7,8,9,10,0,1,2,3,4,
            5,6,7,8,9,10,0,0,0,0,
            0,0,0,0,0,0,67,0,1,2,
            3,4,5,6,7,8,9,10,14,15,
            68,69,0,1,2,3,4,5,6,7,
            8,9,10,0,1,2,3,4,5,6,
            7,8,9,10,0,1,2,3,4,5,
            6,7,8,9,10,0,0,0,0,0,
            0,0,0,0,0,0,11,12,70,13,
            66,13,0,0,0,71,72,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,100,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            5,372,373,374,375,376,377,378,379,380,
            381,390,391,495,505,506,382,384,386,388,
            392,394,396,398,400,402,404,406,408,410,
            412,414,416,418,420,422,424,426,428,430,
            432,383,385,387,389,393,395,397,399,401,
            403,405,407,409,411,413,415,417,419,421,
            423,425,427,429,431,433,504,478,469,470,
            499,331,331,474,483,487,488,492,493,494,
            497,471,472,498,473,484,479,486,475,476,
            477,496,500,480,481,482,485,489,490,491,
            331,372,373,374,375,376,377,378,379,380,
            381,390,391,306,467,468,382,384,386,388,
            392,394,396,398,400,402,404,406,408,410,
            412,414,416,418,420,422,424,426,428,430,
            432,383,385,387,389,393,395,397,399,401,
            403,405,407,409,411,413,415,417,419,421,
            423,425,427,429,431,433,464,245,339,340,
            307,465,466,305,337,344,345,338,349,348,
            346,342,343,1,372,373,374,375,376,377,
            378,379,380,381,390,391,331,331,331,382,
            384,386,388,392,394,396,398,400,402,404,
            406,408,410,412,414,416,418,420,422,424,
            426,428,430,432,383,385,387,389,393,395,
            397,399,401,403,405,407,409,411,413,415,
            417,419,421,423,425,427,429,431,433,2,
            372,373,374,375,376,377,378,379,380,381,
            363,364,331,372,373,374,375,376,377,378,
            379,380,381,331,372,373,374,375,376,377,
            378,379,380,381,34,372,373,374,375,376,
            377,378,379,380,381,35,372,373,374,375,
            376,377,378,379,380,381,331,331,16,331,
            331,331,331,331,4,331,251,29,372,373,
            374,375,376,377,378,379,380,381,467,468,
            290,293,36,372,373,374,375,376,377,378,
            379,380,381,31,372,373,374,375,376,377,
            378,379,380,381,30,372,373,374,375,376,
            377,378,379,380,381,26,331,331,10,331,
            331,331,331,331,331,331,363,364,370,351,
            464,350,331,331,331,465,466,331,331,331,
            331,331,331,331,331,331,331,331,331,331,
            331,331,331,331,331,331,330
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }
    public final int asb(int index) { return 0; }
    public final int asr(int index) { return 0; }
    public final int nasb(int index) { return 0; }
    public final int nasr(int index) { return 0; }
    public final int terminalIndex(int index) { return 0; }
    public final int nonterminalIndex(int index) { return 0; }
    public final int scopePrefix(int index) { return 0;}
    public final int scopeSuffix(int index) { return 0;}
    public final int scopeLhs(int index) { return 0;}
    public final int scopeLa(int index) { return 0;}
    public final int scopeStateSet(int index) { return 0;}
    public final int scopeRhs(int index) { return 0;}
    public final int scopeState(int index) { return 0;}
    public final int inSymb(int index) { return 0;}
    public final String name(int index) { return null; }
    public final int getErrorSymbol() { return 0; }
    public final int getScopeUbound() { return 0; }
    public final int getScopeSize() { return 0; }
    public final int getMaxNameLength() { return 0; }

    public final static int
           NUM_STATES        = 20,
           NT_OFFSET         = 102,
           LA_STATE_OFFSET   = 506,
           MAX_LA            = 1,
           NUM_RULES         = 175,
           NUM_NONTERMINALS  = 42,
           NUM_SYMBOLS       = 144,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 176,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 100,
           EOLT_SYMBOL       = 103,
           ACCEPT_ACTION     = 330,
           ERROR_ACTION      = 331;

    public final static boolean BACKTRACK = false;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getStartSymbol() { return lhs[0]; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}

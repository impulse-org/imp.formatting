package org.eclipse.imp.formatting.parser;

public class FormattingSpecificationKWLexerprs implements lpg.runtime.ParseTable, FormattingSpecificationKWLexersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
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
            2,1
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
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
            11,12,10,11,7,8,6,7,5,6,
            9,10
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,15,15,11,1,22,4,17,
            31,16,35,36,26,38,40,41,45,46,
            44,49,50,51,59,61,62,64,65,66,
            69,72,71,73,7,74,75,79,85,86,
            89,90,91,97,99,94,9,100,101,103,
            105,108,111,116,118,113,119,123,125,129,
            120,130,132,135,137,140,141,144,145,147,
            150,153,84,84
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,0,2,0,4,5,0,1,0,1,
            0,8,12,13,0,0,0,2,18,4,
            5,0,16,0,16,0,10,12,13,19,
            0,17,11,3,0,0,11,0,3,0,
            0,4,8,0,0,0,3,0,0,0,
            0,11,8,14,5,10,8,7,0,1,
            0,0,2,0,0,0,3,6,0,4,
            0,0,0,0,0,11,5,7,0,1,
            0,7,14,10,0,0,2,15,0,0,
            0,6,3,0,4,7,0,1,0,0,
            0,3,0,3,0,0,2,0,15,10,
            0,9,0,6,4,0,1,0,0,0,
            3,9,0,1,0,6,2,9,0,0,
            2,0,0,2,0,1,0,1,9,0,
            0,2,2,0,0,1,0,0,5,0,
            1,5,0,1,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static byte termAction[] = {0,
            84,84,17,84,20,19,84,91,84,92,
            84,24,18,22,84,84,84,27,21,30,
            29,84,53,84,63,84,25,28,31,83,
            84,16,23,26,84,84,34,84,33,84,
            84,35,32,84,84,84,40,84,84,84,
            84,37,38,36,42,39,41,43,84,93,
            84,84,44,84,84,84,46,45,84,48,
            84,84,84,84,84,47,50,51,84,94,
            84,55,49,54,84,84,56,52,84,84,
            84,57,59,84,60,58,84,89,84,84,
            84,61,84,65,84,84,67,84,62,64,
            84,66,84,68,69,84,90,84,84,84,
            70,71,84,95,84,75,73,72,84,84,
            74,84,84,77,84,87,84,96,76,84,
            84,78,79,84,84,88,84,84,80,84,
            85,81,84,86
        };
    };
    public final static byte termAction[] = TermAction.termAction;
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
           NUM_STATES        = 68,
           NT_OFFSET         = 32,
           LA_STATE_OFFSET   = 96,
           MAX_LA            = 0,
           NUM_RULES         = 12,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 34,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 13,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 19,
           EOLT_SYMBOL       = 33,
           ACCEPT_ACTION     = 83,
           ERROR_ACTION      = 84;

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

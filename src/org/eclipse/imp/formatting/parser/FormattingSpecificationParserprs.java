package org.eclipse.imp.formatting.parser;

public class FormattingSpecificationParserprs implements lpg.runtime.ParseTable, FormattingSpecificationParsersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            1,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            2,3,4,5,6,7,8,1
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            5,3,3,0,2,3,3,3,-1,0,
            -3,-4,0,-6,0,-7,-11,0,-13,-2,
            0,-5,0,-8,-9,0,-10,-12,-14,-15,
            -16,0
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static byte baseAction[] = {
            1,1,2,3,5,5,6,7,4,2,
            20,6,5,12,12,14,31,1,25,17,
            3,17,7,5,14,13,29,22,15,20,
            25,30,34,34
        };
    };
    public final static byte baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static byte lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,0,0,2,0,0,0,7,8,4,
            6,0,0,0,0,3,0,15,5,0,
            14,0,1,12,0,1,10,13,9,0,
            0,0,0,0,0,0,0,0,0,0,
            11,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static byte termAction[] = {0,
            34,34,34,11,34,34,34,37,19,22,
            16,34,34,34,34,35,34,33,36,34,
            24,34,28,27,34,31,30,42,40,34,
            4,34,34,34,34,34,34,34,34,34,
            41
        };
    };
    public final static byte termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }

    public interface Asb {
        public final static byte asb[] = {0,
            1,14,3,5,16,7,9,18,20,22,
            9,24,12,26,22,28
        };
    };
    public final static byte asb[] = Asb.asb;
    public final int asb(int index) { return asb[index]; }

    public interface Asr {
        public final static byte asr[] = {0,
            2,0,4,0,6,0,12,0,7,8,
            0,10,0,15,0,14,0,5,0,3,
            0,1,0,13,0,9,0,11,0
        };
    };
    public final static byte asr[] = Asr.asr;
    public final int asr(int index) { return asr[index]; }

    public interface Nasb {
        public final static byte nasb[] = {0,
            1,14,3,5,14,7,9,14,14,14,
            11,14,13,14,14,14
        };
    };
    public final static byte nasb[] = Nasb.nasb;
    public final int nasb(int index) { return nasb[index]; }

    public interface Nasr {
        public final static byte nasr[] = {0,
            1,0,2,0,3,0,4,0,5,0,
            6,0,7,0
        };
    };
    public final static byte nasr[] = Nasr.nasr;
    public final int nasr(int index) { return nasr[index]; }

    public interface TerminalIndex {
        public final static byte terminalIndex[] = {0,
            15,2,3,4,5,6,7,8,9,10,
            11,12,13,14,16,17
        };
    };
    public final static byte terminalIndex[] = TerminalIndex.terminalIndex;
    public final int terminalIndex(int index) { return terminalIndex[index]; }

    public interface NonterminalIndex {
        public final static byte nonterminalIndex[] = {0,
            18,19,20,21,0,22,23,0
        };
    };
    public final static byte nonterminalIndex[] = NonterminalIndex.nonterminalIndex;
    public final int nonterminalIndex(int index) { return nonterminalIndex[index]; }
    public final static int scopePrefix[] = null;
    public final int scopePrefix(int index) { return 0;}

    public final static int scopeSuffix[] = null;
    public final int scopeSuffix(int index) { return 0;}

    public final static int scopeLhs[] = null;
    public final int scopeLhs(int index) { return 0;}

    public final static int scopeLa[] = null;
    public final int scopeLa(int index) { return 0;}

    public final static int scopeStateSet[] = null;
    public final int scopeStateSet(int index) { return 0;}

    public final static int scopeRhs[] = null;
    public final int scopeRhs(int index) { return 0;}

    public final static int scopeState[] = null;
    public final int scopeState(int index) { return 0;}

    public final static int inSymb[] = null;
    public final int inSymb(int index) { return 0;}


    public interface Name {
        public final static String name[] = {
            "",
            "$empty",
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
            "any",
            "EOF_TOKEN",
            "ERROR_TOKEN",
            "specification",
            "language",
            "rules",
            "example",
            "rule",
            "box"
        };
    };
    public final static String name[] = Name.name;
    public final String name(int index) { return name[index]; }

    public final static int
           ERROR_SYMBOL      = 16,
           SCOPE_UBOUND      = -1,
           SCOPE_SIZE        = 0,
           MAX_NAME_LENGTH   = 15;

    public final int getErrorSymbol() { return ERROR_SYMBOL; }
    public final int getScopeUbound() { return SCOPE_UBOUND; }
    public final int getScopeSize() { return SCOPE_SIZE; }
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int
           NUM_STATES        = 16,
           NT_OFFSET         = 16,
           LA_STATE_OFFSET   = 42,
           MAX_LA            = 1,
           NUM_RULES         = 8,
           NUM_NONTERMINALS  = 8,
           NUM_SYMBOLS       = 24,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 9,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 15,
           EOLT_SYMBOL       = 15,
           ACCEPT_ACTION     = 33,
           ERROR_ACTION      = 34;

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

    public final int originalState(int state) {
        return -baseCheck[state];
    }
    public final int asi(int state) {
        return asb[originalState(state)];
    }
    public final int nasi(int state) {
        return nasb[originalState(state)];
    }
    public final int inSymbol(int state) {
        return inSymb[originalState(state)];
    }

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

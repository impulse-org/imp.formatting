package org.eclipse.imp.formatting.parser;

import org.eclipse.imp.formatting.parser.Ast.*;
import lpg.runtime.*;
import org.eclipse.imp.parser.IParser;

public class FormattingSpecificationParser extends PrsStream implements RuleAction, IParser
{
    private static ParseTable prs = new FormattingSpecificationParserprs();
    private DeterministicParser dtParser;

    public DeterministicParser getParser() { return dtParser; }
    private void setResult(Object object) { dtParser.setSym1(object); }
    public Object getRhsSym(int i) { return dtParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return dtParser.getToken(i); }
    public IToken getRhsIToken(int i) { return super.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return dtParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return super.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return dtParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return super.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return dtParser.getFirstToken(); }
    public IToken getLeftIToken()  { return super.getIToken(getLeftSpan()); }

    public int getRightSpan() { return dtParser.getLastToken(); }
    public IToken getRightIToken() { return super.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = dtParser.getToken(i);
        IToken err = super.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = dtParser.getToken(i);
        IToken err = super.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public FormattingSpecificationParser(ILexStream lexStream)
    {
        super(lexStream);

        try
        {
            super.remapTerminalSymbols(orderedTerminalSymbols(), FormattingSpecificationParserprs.EOFT_SYMBOL);
        }
        catch(NullExportedSymbolsException e) {
        }
        catch(NullTerminalSymbolsException e) {
        }
        catch(UnimplementedTerminalsException e)
        {
            java.util.ArrayList unimplemented_symbols = e.getSymbols();
            System.out.println("The Lexer will not scan the following token(s):");
            for (int i = 0; i < unimplemented_symbols.size(); i++)
            {
                Integer id = (Integer) unimplemented_symbols.get(i);
                System.out.println("    " + FormattingSpecificationParsersym.orderedTerminalSymbols[id.intValue()]);               
            }
            System.out.println();                        
        }
        catch(UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 FormattingSpecificationParsersym.orderedTerminalSymbols[FormattingSpecificationParserprs.EOFT_SYMBOL]));
        } 
    }

    public String[] orderedTerminalSymbols() { return FormattingSpecificationParsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return FormattingSpecificationParsersym.orderedTerminalSymbols[kind]; }            
    public int getEOFTokenKind() { return FormattingSpecificationParserprs.EOFT_SYMBOL; }
    public PrsStream getParseStream() { return (PrsStream) this; }

    /**
     * When constructing a SAFARI parser, a handler for error messages
     * can be passed to the parser.
     */
     /*
    private IMessageHandler handler = null;
    public void setMessageHandler(IMessageHandler handler)
    {
        this.handler = handler;
    }
    
    //
    // Redirect syntax error message to proper recipient.
    //
    public void reportError(int error_code, String location_info, int left_token, int right_token, String token_text)
    {
        if (this.handler == null)
            super.reportError(error_code,
                              location_info,
                              left_token,
                              right_token,
                              token_text);
        else 
        {
            int start_offset = super.getStartOffset(left_token),
                end_offset = (right_token > left_token 
                                          ? super.getEndOffset(right_token)
                                          : super.getEndOffset(left_token));

            String msg = ((error_code == DELETION_CODE ||
                           error_code == MISPLACED_CODE ||
                           token_text.equals(""))
                                       ? ""
                                       : (token_text + " ")) +
                         errorMsgText[error_code];

            handler.handleMessage(start_offset,
                                  end_offset - start_offset + 1,
                                  msg);
        }
    }

    //
    // Report error message for given error_token.
    //
    public final void reportErrorTokenMessage(int error_token, String msg)
    {
        if (this.handler == null)
        {
            int firsttok = super.getFirstRealToken(error_token),
                lasttok = super.getLastRealToken(error_token);
            String location = super.getFileName() + ':' +
                              (firsttok > lasttok
                                        ? (super.getEndLine(lasttok) + ":" + super.getEndColumn(lasttok))
                                        : (super.getLine(error_token) + ":" +
                                           super.getColumn(error_token) + ":" +
                                           super.getEndLine(error_token) + ":" +
                                           super.getEndColumn(error_token)))
                              + ": ";
            super.reportError((firsttok > lasttok ? ParseErrorCodes.INSERTION_CODE : ParseErrorCodes.SUBSTITUTION_CODE), location, msg);
        }
        else 
        {
            handler.handleMessage(super.getStartOffset(error_token),
                                  super.getTokenLength(error_token),
                                  msg);
        }
    }
    */

    public Object parser()
    {
        return parser(null, 0);
    }
        
    public Object parser(Monitor monitor)
    {
        return parser(monitor, 0);
    }
        
    public Object parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }
        
    public Object parser(Monitor monitor, int error_repair_count)
    {
        try
        {
            dtParser = new DeterministicParser(monitor, (TokenStream)this, prs, (RuleAction)this);
        }
        catch (NotDeterministicParseTableException e)
        {
            throw new Error(new NotDeterministicParseTableException
                                ("Regenerate FormattingSpecificationParserprs.java with -NOBACKTRACK option"));
        }
        catch (BadParseSymFileException e)
        {
            throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- FormattingSpecificationParsersym.java. Regenerate FormattingSpecificationParserprs.java"));
        }

        try
        {
            return (Object) dtParser.parse();
        }
        catch (BadParseException e)
        {
            reset(e.error_token); // point to error token

            DiagnoseParser diagnoseParser = new DiagnoseParser(this, prs);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }


    public void ruleAction(int ruleNumber)
    {
        switch (ruleNumber)
        {
 
            //
            // Rule 1:  specification ::= begin_formatter language rules example end_formatter
            //
            case 1: {
                setResult(
                    new specification(getLeftIToken(), getRightIToken(),
                                      new ASTNodeToken(getRhsIToken(1)),
                                      (language)getRhsSym(2),
                                      (rules)getRhsSym(3),
                                      (example)getRhsSym(4),
                                      new ASTNodeToken(getRhsIToken(5)))
                );
                break;
            } 
            //
            // Rule 2:  language ::= begin_language IDENTIFIER end_language
            //
            case 2: {
                setResult(
                    new language(getLeftIToken(), getRightIToken(),
                                 new ASTNodeToken(getRhsIToken(1)),
                                 new ASTNodeToken(getRhsIToken(2)),
                                 new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            } 
            //
            // Rule 3:  rules ::= begin_rules rulelist end_rules
            //
            case 3: {
                setResult(
                    new rules(getLeftIToken(), getRightIToken(),
                              new ASTNodeToken(getRhsIToken(1)),
                              (ruleList)getRhsSym(2),
                              new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            } 
            //
            // Rule 4:  rulelist ::= $Empty
            //
            case 4: {
                setResult(
                    new ruleList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            } 
            //
            // Rule 5:  rulelist ::= rulelist rule
            //
            case 5: {
                ((ruleList)getRhsSym(1)).add((rule)getRhsSym(2));
                break;
            } 
            //
            // Rule 6:  rule ::= begin_rule box end_rule
            //
            case 6: {
                setResult(
                    new rule(getLeftIToken(), getRightIToken(),
                             new ASTNodeToken(getRhsIToken(1)),
                             (box)getRhsSym(2),
                             new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            } 
            //
            // Rule 7:  box ::= begin_box any end_box
            //
            case 7: {
                setResult(
                    new box(getLeftIToken(), getRightIToken(),
                            new ASTNodeToken(getRhsIToken(1)),
                            new ASTNodeToken(getRhsIToken(2)),
                            new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            } 
            //
            // Rule 8:  example ::= begin_example any end_example
            //
            case 8: {
                setResult(
                    new example(getLeftIToken(), getRightIToken(),
                                new ASTNodeToken(getRhsIToken(1)),
                                new ASTNodeToken(getRhsIToken(2)),
                                new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            }
    
            default:
                break;
        }
        return;
    }
}

